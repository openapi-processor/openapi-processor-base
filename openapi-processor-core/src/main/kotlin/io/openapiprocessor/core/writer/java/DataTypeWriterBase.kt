/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.MappingFinder
import io.openapiprocessor.core.model.Annotation
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.ModelDataType
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.model.datatypes.PropertyDataType
import java.io.StringWriter
import java.io.Writer

private const val deprecated = "@Deprecated"

enum class Access {
    NONE, PRIVATE
}

abstract class DataTypeWriterBase(
    protected val apiOptions: ApiOptions,
    protected val generatedWriter: GeneratedWriter,
    protected val validationAnnotations: BeanValidationFactory = BeanValidationFactory(),
    protected val javadocWriter: JavaDocWriter = JavaDocWriter()
): DataTypeWriter {
    protected val annotationWriter = AnnotationWriter()

    protected fun writeFileHeader(target: Writer, dataType: ModelDataType) {
        writePackage(target, dataType)
        writeImports(target, dataType)
    }

    protected fun writePreClass(target: Writer, dataType: ModelDataType) {
        writeJavaDoc(target, dataType)
        writeDeprecated(target, dataType)
        writeAnnotationsBeanValidation(target, dataType)
        writeAnnotationsMappings(target, dataType)
        writeAnnotationsGenerated(target)
    }

    protected fun getProp(
        propertyName: String,
        javaPropertyName: String,
        propDataType: PropertyDataType,
        required: Boolean,
        access: Access): String {

        var result = ""

        if (apiOptions.javadoc) {
            result += javadocWriter.convert(propDataType)
        }

        result += ifDeprecated(propDataType)

        var propTypeName = propDataType.getTypeName()
        if(apiOptions.beanValidation) {
            val info = validationAnnotations.validate(propDataType.dataType, required)
            val prop = info.prop
            prop.annotations.forEach {
                result += "    ${it}\n"
            }
            propTypeName = prop.dataTypeValue
        }

        if (propDataType.dataType !is ObjectDataType) {
            val annotationTypeMappings = MappingFinder(apiOptions.typeMappings)
                .findTypeAnnotations(propDataType.dataType.getSourceName())

            annotationTypeMappings.forEach {
                val annotation = StringWriter()
                annotationWriter.write(annotation, Annotation(it.annotation.type, it.annotation.parameters))
                result += "    $annotation\n"
            }
        }

        result += "    ${getPropertyAnnotation(propertyName, propDataType)}\n"

        result += if (access == Access.PRIVATE) {
            "    private $propTypeName $javaPropertyName"
        } else {
            "    $propTypeName $javaPropertyName"
        }

        return result
    }

    private fun getPropertyAnnotation(propertyName: String, propDataType: PropertyDataType): String {
        val access = getAccess(propDataType)

        var result = "@JsonProperty("
        result += if (access != null) {
            "value = \"$propertyName\", access = JsonProperty.Access.${access.value}"
        } else {
            "\"$propertyName\""
        }

        result += ")"
        return result
    }

    private fun getAccess(propDataType: PropertyDataType): PropertyAccess? {
        return when {
            propDataType.readOnly -> PropertyAccess("READ_ONLY")
            propDataType.writeOnly -> PropertyAccess("WRITE_ONLY")
            else -> null
        }
    }

    protected fun ifDeprecated(propDataType: DataType): String {
        return if (propDataType.deprecated) {
            "    $deprecated\n"
        } else {
            ""
        }
    }

    private fun writePackage(target: Writer, dataType: ModelDataType) {
        target.write("package ${dataType.getPackageName()};\n\n")
    }

    private fun writeImports(target: Writer, dataType: ModelDataType) {
        val imports: List<String> = collectImports(dataType.getPackageName(), dataType)
        imports.forEach {
            target.write("import ${it};\n")
        }

        if (imports.isNotEmpty()) {
            target.write("\n")
        }
    }

    private fun writeJavaDoc(target: Writer, dataType: ModelDataType) {
        if (apiOptions.javadoc) {
            target.write(javadocWriter.convert(dataType))
        }
    }

    private fun writeDeprecated(target: Writer, dataType: ModelDataType) {
        if (dataType.deprecated) {
            target.write("$deprecated\n")
        }
    }

    private fun writeAnnotationsBeanValidation(target: Writer, dataType: ModelDataType) {
        if (apiOptions.beanValidation) {
            val objectInfo = validationAnnotations.validate(dataType)
            objectInfo.annotations.forEach {
                target.write("${buildAnnotation(it)}\n")
            }
        }
    }

    private fun writeAnnotationsMappings(target: Writer, dataType: ModelDataType) {
        val annotationTypeMappings = MappingFinder(apiOptions.typeMappings)
            .findTypeAnnotations(dataType.getTypeName(), true)

        annotationTypeMappings.forEach {
            annotationWriter.write(target, Annotation(it.annotation.type, it.annotation.parameters))
            target.write("\n")
        }
    }

    private fun writeAnnotationsGenerated(target: Writer) {
        generatedWriter.writeUse(target)
        target.write("\n")
    }

    private fun collectImports(packageName: String, dataType: ModelDataType): List<String> {
        val imports = mutableSetOf<String>()

        imports.add(generatedWriter.getImport())
        imports.addAll(collectDataTypeImports(dataType))
        imports.addAll(collectBeanValidationImports(dataType))
        imports.addAll(collectDataTypePropertiesImports(dataType))

        return DefaultImportFilter()
            .filter(packageName, imports)
            .sorted()
    }

    private fun collectDataTypeImports(dataType: ModelDataType): Set<String> {
        val imports = mutableSetOf<String>()

        imports.addAll(dataType.referencedImports)

        val annotationTypeMappings = MappingFinder(apiOptions.typeMappings)
            .findTypeAnnotations(dataType.getTypeName(), true)

        annotationTypeMappings.forEach {
            imports.add(it.annotation.type)
        }

        return imports
    }

    private fun collectBeanValidationImports(dataType: ModelDataType): Set<String> {
        if (!apiOptions.beanValidation)
            return emptySet()

        val imports = mutableSetOf<String>()

        val info = validationAnnotations.validate(dataType)
        val prop = info.prop
        imports.addAll(prop.imports)

        dataType.forEach { propName, propDataType ->
            val target = getTarget(propDataType)
            val propInfo = validationAnnotations.validate(target, dataType.isRequired(propName))
            val propProp = propInfo.prop
            imports.addAll(propProp.imports)
        }

        return imports
    }

    private fun collectDataTypePropertiesImports(dataType: ModelDataType): Set<String> {
        val imports = mutableSetOf<String>()

        dataType.forEach { _, propDataType ->
            imports.add("com.fasterxml.jackson.annotation.JsonProperty")

            // do not annotate unmapped, i.e. generated pojo property
            if ((propDataType is PropertyDataType) && (propDataType.dataType is ObjectDataType)) {
                return@forEach
            }

            val target = getTarget(propDataType)
            val annotationTypeMappings = MappingFinder(apiOptions.typeMappings)
                .findTypeAnnotations(target.getSourceName())

            annotationTypeMappings.forEach { atm ->
                imports.add(atm.annotation.type)

                atm.annotation.parameters.forEach {
                    val import = it.value.import
                    if (import != null)
                        imports.add(import)
                }
            }
        }

        return imports
    }

    private fun getTarget(dataType: DataType): DataType {
        if (dataType is PropertyDataType)
            return dataType.dataType

        return dataType
    }
}

class PropertyAccess(val value: String)
