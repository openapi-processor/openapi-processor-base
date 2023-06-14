/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.MappingFinder
import io.openapiprocessor.core.model.datatypes.*
import java.io.StringWriter
import java.io.Writer

@Suppress("ConstPropertyName")
private const val deprecated = "@Deprecated"

/**
 * Writer for POJO classes with java 14 records.
 */
class DataTypeWriterRecord(
    private val apiOptions: ApiOptions,
    private val generatedWriter: GeneratedWriter,
    private val validationAnnotations: BeanValidationFactory = BeanValidationFactory(),
    private val javadocWriter: JavaDocWriter = JavaDocWriter()
) : DataTypeWriter {
    private val annotationWriter = AnnotationWriter()

    override fun write(target: Writer, dataType: ModelDataType) {
        target.write("package ${dataType.getPackageName()};\n\n")

        val imports: List<String> = collectImports(dataType.getPackageName(), dataType)
        imports.forEach {
            target.write("import ${it};\n")
        }

        if (imports.isNotEmpty()) {
            target.write("\n")
        }

        if (apiOptions.javadoc) {
            target.write(javadocWriter.convert(dataType))
        }

        if (dataType.deprecated) {
            target.write("$deprecated\n")
        }

        if (apiOptions.beanValidation) {
            val objectInfo = validationAnnotations.validate(dataType)
            objectInfo.annotations.forEach {
                target.write("${buildAnnotation(it)}\n")
            }
        }

        val annotationTypeMappings = MappingFinder(apiOptions.typeMappings)
            .findTypeAnnotations(dataType.getTypeName())

        annotationTypeMappings.forEach {
            annotationWriter.write(target, Annotation(it.annotation.type, it.annotation.parameters))
            target.write("\n")
        }

        generatedWriter.writeUse(target)
        target.write("\n")
        writeClassHeader(target, dataType)

        val props = mutableListOf<String>()
        dataType.forEach { propName, propDataType ->
            val javaPropertyName = toIdentifier(propName)
            val propSource = getProp(
                propName,
                javaPropertyName,
                propDataType as PropertyDataType,
                dataType.isRequired(propName))
            props.add(propSource)
        }

        target.write("(\n")
        target.write(props.joinToString(",\n\n"))
        target.write("\n)")

        val implements: DataType? = dataType.implementsDataType
        if (implements != null) {
            target.write(" implements ${implements.getTypeName()} ")
        }
        target.write (" {}\n")
    }

    private fun writeClassHeader(
        target: Writer,
        dataType: ModelDataType
    ) {
        target.write("public record ${dataType.getTypeName()}")
    }

    private fun getProp(
        propertyName: String,
        javaPropertyName: String,
        propDataType: PropertyDataType,
        required: Boolean): String {

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

        if (propDataType.dataType is MappedSourceDataType) {
            val annotationTypeMappings = MappingFinder(apiOptions.typeMappings)
                .findTypeAnnotations(propDataType.dataType.getSourceName())

            annotationTypeMappings.forEach {
                val annotation = StringWriter()
                annotationWriter.write(annotation, Annotation(it.annotation.type, it.annotation.parameters))
                result += "    $annotation\n"
            }
        }

        result += "    ${getPropertyAnnotation(propertyName, propDataType)}\n"
        result += "    $propTypeName $javaPropertyName"

        // todo can't init record parameter
        // null (JsonNullable) may have an init value
//        val dataType = propDataType.dataType
//        if (dataType is NullDataType && dataType.init != null) {
//            result += " = ${dataType.init}"
//        }

        return result
    }

    private fun getPropertyAnnotation(propertyName: String, propDataType: PropertyDataType): String {
        val access = getAccess(propDataType)

        var result = "@JsonProperty("
        if (access != null) {
            result += "value = \"$propertyName\", access = JsonProperty.Access.${access.value}"
        } else {
            result += "\"$propertyName\""
        }

        result += ")"
        return result
    }

    private fun getAccess(propDataType: PropertyDataType): PropertyAccess? {
        if (!propDataType.readOnly && !propDataType.writeOnly)
            return null

        return when {
            propDataType.readOnly -> PropertyAccess("READ_ONLY")
            propDataType.writeOnly -> PropertyAccess("WRITE_ONLY")
            else -> throw IllegalStateException()
        }
    }

    private fun ifDeprecated(propDataType: DataType): String {
        return if (propDataType.deprecated) {
            "    $deprecated\n"
        } else {
            ""
        }
    }

    private fun collectImports(packageName: String, dataType: ModelDataType): List<String> {
        val imports = mutableSetOf<String>()

        imports.add(generatedWriter.getImport())

        dataType.forEach { _, propDataType ->
            imports.add("com.fasterxml.jackson.annotation.JsonProperty")

            val target = getTarget(propDataType)
            if (target is MappedSourceDataType) {
                val annotationTypeMappings = MappingFinder(apiOptions.typeMappings)
                    .findTypeAnnotations(target.getSourceName())

                annotationTypeMappings.forEach {
                    imports.add(it.annotation.type)
                }
            }
        }

        imports.addAll(dataType.referencedImports)

        if (apiOptions.beanValidation) {
            val info = validationAnnotations.validate(dataType)
            val prop = info.prop
            imports.addAll(prop.imports)

            dataType.forEach { propName, propDataType ->
                val target = getTarget(propDataType)
                val propInfo = validationAnnotations.validate(target, dataType.isRequired(propName))
                val propProp = propInfo.prop
                imports.addAll(propProp.imports)
            }
        }

        val annotationTypeMappings = MappingFinder(apiOptions.typeMappings).findTypeAnnotations(
            dataType.getTypeName())

        annotationTypeMappings.forEach {
            imports.add(it.annotation.type)
        }

        return DefaultImportFilter()
            .filter(packageName, imports)
            .sorted()
    }

    private fun getTarget(dataType: DataType): DataType {
        if (dataType is PropertyDataType)
            return dataType.dataType

        return dataType
    }

}

