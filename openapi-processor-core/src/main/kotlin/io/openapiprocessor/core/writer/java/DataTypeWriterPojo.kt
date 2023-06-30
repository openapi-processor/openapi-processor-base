/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.MappingFinder
import io.openapiprocessor.core.model.datatypes.*
import io.openapiprocessor.core.support.capitalizeFirstChar
import java.io.StringWriter
import java.io.Writer

private const val deprecated = "@Deprecated"

/**
 * Writer for POJO classes.
 */
class DataTypeWriterPojo(
    apiOptions: ApiOptions,
    generatedWriter: GeneratedWriter,
    validationAnnotations: BeanValidationFactory = BeanValidationFactory(),
    javadocWriter: JavaDocWriter = JavaDocWriter()
) : DataTypeWriterBase(apiOptions, generatedWriter, validationAnnotations, javadocWriter) {

    override fun write(target: Writer, dataType: ModelDataType) {
        writeFileHeader(target, dataType)
        writePreClass(target, dataType)
        writeClass(target, dataType)
    }

    private fun writeClass(target: Writer, dataType: ModelDataType) {
        writeClassOpen(target, dataType)
        writeClassProperties(target, dataType)
        writeClassMethods(dataType, target)
        writeClassClose(target)
    }

    private fun writeClassOpen(target: Writer, dataType: ModelDataType) {
        val implements: DataType? = dataType.implementsDataType
        if (implements != null) {
            writeClassImplementsHeader(target, dataType, implements)
        } else {
            writeClassHeader(target, dataType)
        }
    }

    private fun writeClassProperties(target: Writer, dataType: ModelDataType) {
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

        target.write(props.joinToString(";\n\n"))
        if (props.isNotEmpty()) {
            target.write(";\n\n")
        }
    }

    private fun writeClassMethods(dataType: ModelDataType, target: Writer) {
        dataType.forEach { propName, propDataType ->
            val javaPropertyName = toCamelCase(propName)
            target.write(getGetter(javaPropertyName, propDataType))
            target.write(getSetter(javaPropertyName, propDataType))
        }
    }

    private fun writeClassClose(target: Writer) {
        target.write("}\n")
    }

    private fun writeClassImplementsHeader(
        target: Writer,
        dataType: ModelDataType,
        implements: DataType
    ) {
        target.write("public class ${dataType.getTypeName()} implements ${implements.getTypeName()} {\n\n")
    }

    private fun writeClassHeader(
        target: Writer,
        dataType: ModelDataType
    ) {
        target.write("public class ${dataType.getTypeName()} {\n\n")
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
        result += "    private $propTypeName $javaPropertyName"

        // null (JsonNullable) may have an init value
        val dataType = propDataType.dataType
        if (dataType is NullDataType && dataType.init != null) {
            result += " = ${dataType.init}"
        }

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

    private fun getGetter(propertyName: String, propDataType: DataType): String {
        var result = ""
        result += ifDeprecated(propDataType)

        result += """
            |    public ${propDataType.getTypeName()} get${toMethodTail(propertyName.capitalizeFirstChar())}() {
            |        return ${toIdentifier(propertyName)};
            |    }
            |
            |
        """.trimMargin()

        return result
    }

    private fun getSetter(propertyName: String, propDataType: DataType): String {
        var result = ""
        result += ifDeprecated(propDataType)

        val property = toIdentifier(propertyName)

        result += """
            |    public void set${toMethodTail(propertyName.capitalizeFirstChar())}(${propDataType.getTypeName()} ${property}) {
            |        this.${property} = ${property};
            |    }
            |
            |
        """.trimMargin()

        return result
    }

    private fun ifDeprecated(propDataType: DataType): String {
        return if (propDataType.deprecated) {
            "    $deprecated\n"
        } else {
            ""
        }
    }
}
