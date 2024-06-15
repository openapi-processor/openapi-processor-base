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
import io.openapiprocessor.core.writer.Identifier
import java.io.StringWriter
import java.io.Writer

private const val deprecated = "@Deprecated"

enum class Access {
    NONE, PRIVATE
}


data class PropertyData(
    val srcPropName: String,
    val propName: String,
    val baseName: String,
    val imports: Set<String>,
    val modelDataType: ModelDataType,
    val propDataType: PropertyDataType
) {
    val required = modelDataType.isRequired(srcPropName)
}


abstract class DataTypeWriterBase(
    protected val apiOptions: ApiOptions,
    protected val identifier: Identifier,
    protected val generatedWriter: GeneratedWriter,
    protected val validationAnnotations: BeanValidationFactory = BeanValidationFactory(apiOptions),
    protected val javadocWriter: JavaDocWriter
): DataTypeWriter {
    protected val annotationWriter = AnnotationWriter()

    protected fun collectPropertiesData(modelDataType: ModelDataType): List<PropertyData> {
        val propData = mutableListOf<PropertyData>()

        modelDataType.forEach { srcPropName, dataType ->
            val propDataType = dataType as PropertyDataType

            val propName = identifier.toIdentifier(srcPropName)
            val baseName = identifier.toCamelCase(srcPropName)
            val propImports = collectDataTypePropertyImports(modelDataType, srcPropName, propDataType)

            propData.add(PropertyData(
                srcPropName,
                propName,
                baseName,
                propImports,
                modelDataType,
                propDataType))
        }

        return propData
    }

    protected fun writeFileHeader(target: Writer, dataType: ModelDataType, propsData: List<PropertyData>) {
        writePackage(target, dataType)
        writeImports(target, dataType, propsData)
    }

    protected fun writePreClass(target: Writer, dataType: ModelDataType, propsData: List<PropertyData>) {
        writeJavaDoc(target, dataType)
        writeDeprecated(target, dataType)
        writeAnnotationsBeanValidation(target, dataType)
        writeAnnotationsMappings(target, dataType)
        writeAnnotationsGenerated(target)
    }

    protected fun getProp(propData: PropertyData, access: Access): String {
        val propDataType = propData.propDataType
        var result = ""

        if (apiOptions.javadoc) {
            result += javadocWriter.convert(propDataType)
        }

        result += ifDeprecated(propDataType)

        var propTypeName = propDataType.getTypeName()
        if(apiOptions.beanValidation) {
            val info = validationAnnotations.validate(propDataType.dataType, propData.required)
            val prop = info.prop
            prop.annotations.forEach {
                result += "    ${it}\n"
            }
            propTypeName = prop.dataTypeValue
        }

        if (propDataType.dataType !is ObjectDataType) {
            val builder = StringBuilder()
            writeAnnotations(builder, collectTypeAnnotations(propDataType.dataType.getSourceName()))
            result += builder.toString()
        }

        val extBuilder = StringBuilder()
        writeAnnotations(extBuilder, collectExtensionAnnotations(propDataType.extensions))
        result += extBuilder.toString()

        result += "    ${getPropertyAnnotation(propData.srcPropName, propDataType)}"

        result += if (access == Access.PRIVATE) {
            "    private $propTypeName ${propData.propName}"
        } else {
            "    $propTypeName ${propData.propName}"
        }

        return result
    }

    private fun collectTypeAnnotations(sourceName: String): Collection<Annotation> {
        val mappingFinder = MappingFinder(apiOptions.typeMappings)
        return  mappingFinder
            .findTypeAnnotations(sourceName)
            .map { Annotation(it.annotation.type, it.annotation.parameters) }
    }

    private fun collectExtensionAnnotations(extensions: Map<String, *>): Collection<Annotation> {
        val mappingFinder = MappingFinder(apiOptions.typeMappings)

        val annotations = mutableListOf<Annotation>()

        extensions.forEach { ext ->
            when (val extVal = ext.value) {
                is String -> {
                    val found = mappingFinder
                        .findExtensionAnnotations(ext.key, extVal)
                        .map { Annotation(it.annotation.type, it.annotation.parameters) }
                    annotations.addAll(found)
                }
                is Collection<*> -> {
                    val found = mappingFinder
                        .findExtensionAnnotations(ext.key, extVal.filterIsInstance<String>())
                        .map { Annotation(it.annotation.type, it.annotation.parameters) }
                    annotations.addAll(found)
                }
            }
        }

        return annotations
    }

    private fun writeAnnotations(target: StringBuilder, annotations: Collection<Annotation>) {
        annotations.forEach {
            val annotation = StringWriter()
            annotationWriter.write(annotation, Annotation(it.typeName, it.parameters))
            target.append("    $annotation\n")
        }
    }

    private fun getPropertyAnnotation(propertyName: String, propDataType: PropertyDataType): String {
        val access = getAccess(propDataType)

        var result = "@JsonProperty("
        result += if (access != null) {
            "value = \"$propertyName\", access = JsonProperty.Access.${access.value}"
        } else {
            "\"$propertyName\""
        }

        result += ")\n"
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

    private fun writeImports(target: Writer, dataType: ModelDataType, propsData: List<PropertyData>) {
        val imports: List<String> = collectImports(dataType.getPackageName(), dataType, propsData)

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
    }

    private fun collectImports(packageName: String, dataType: ModelDataType, propsData: List<PropertyData>): List<String> {
        val imports = mutableSetOf<String>()

        imports.addAll(generatedWriter.getImports())
        imports.addAll(collectDataTypeImports(dataType))
        imports.addAll(collectPropertyImports(propsData))

        return DefaultImportFilter()
            .filter(packageName, imports)
            .sorted()
    }

    private fun collectPropertyImports(propsData: List<PropertyData>): List<String> {
        return propsData
            .map { it.imports }
            .flatten()
    }

    private fun collectDataTypeImports(dataType: ModelDataType): Set<String> {
        val imports = mutableSetOf<String>()

        imports.addAll(dataType.referencedImports)

        val annotationTypeMappings = MappingFinder(apiOptions.typeMappings)
            .findTypeAnnotations(dataType.getTypeName(), true)

        annotationTypeMappings.forEach {
            imports.add(it.annotation.type)

            val parameterImports = it.annotation.parameters.values
                .filter { v -> v.import != null }
                .map { v -> v.import!! }

            imports.addAll(parameterImports)
        }

        if (apiOptions.beanValidation) {
            val info = validationAnnotations.validate(dataType)
            val prop = info.prop
            imports.addAll(prop.imports)
        }

        return imports
    }

    private fun collectDataTypePropertyImports(
        modelDataType: ModelDataType,
        srcPropName: String,
        propDataType: PropertyDataType
    ): Set<String> {
        val imports = mutableSetOf<String>()

        val target = getTarget(propDataType)

        imports.add("com.fasterxml.jackson.annotation.JsonProperty")

        if (apiOptions.beanValidation) {
            val required = modelDataType.isRequired(srcPropName)
            val propInfo = validationAnnotations.validate(target, required)
            val propProp = propInfo.prop
            imports.addAll(propProp.imports)
        }

        // do not annotate unmapped, i.e. generated pojo property
        if (propDataType.dataType is ObjectDataType) {
            return imports
        }

        val typeAnnotations = collectTypeAnnotations(target.getSourceName())
        typeAnnotations.forEach { annotation ->
            imports.addAll(annotation.imports)

            annotation.parameters.forEach {
                val import = it.value.import
                if (import != null)
                    imports.add(import)
            }
        }

        val extAnnotations = collectExtensionAnnotations(propDataType.extensions)
        extAnnotations.forEach { annotation ->
            imports.addAll(annotation.imports)

            annotation.parameters.forEach {
                val import = it.value.import
                if (import != null)
                    imports.add(import)
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
