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

private const val deprecated = "@Deprecated"

/**
 * Writer for POJO classes with java 14 records.
 */
class DataTypeWriterRecord(
    apiOptions: ApiOptions,
    generatedWriter: GeneratedWriter,
    validationAnnotations: BeanValidationFactory = BeanValidationFactory(),
    javadocWriter: JavaDocWriter = JavaDocWriter()
) : DataTypeWriterBase(apiOptions, generatedWriter, validationAnnotations, javadocWriter) {

    override fun write(target: Writer, dataType: ModelDataType) {
        writeFileHeader(target, dataType)
        writePreClass(target, dataType)
        writeRecord(target, dataType)
    }

    private fun writeRecord(target: Writer, dataType: ModelDataType) {
        writeRecordOpen(target, dataType)
        writeRecordParameter(target, dataType)
        writeRecordImplements(target, dataType)
        writeRecordClose(target)
    }

    private fun writeRecordOpen(target: Writer, dataType: ModelDataType) {
        writeRecordHeader(target, dataType)
    }

    private fun writeRecordParameter(target: Writer, dataType: ModelDataType) {
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
    }

    private fun writeRecordImplements(target: Writer, dataType: ModelDataType) {
        val implements: DataType? = dataType.implementsDataType
        if (implements != null) {
            target.write(" implements ${implements.getTypeName()}")
        }
    }

    private fun writeRecordClose(target: Writer) {
        target.write(" {}\n")
    }

    private fun writeRecordHeader(
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

        val annotationTypeMappings = MappingFinder(apiOptions.typeMappings)
            .findTypeAnnotations(propDataType.dataType.getSourceName())

        annotationTypeMappings.forEach {
            val annotation = StringWriter()
            annotationWriter.write(annotation, Annotation(it.annotation.type, it.annotation.parameters))
            result += "    $annotation\n"
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
}

