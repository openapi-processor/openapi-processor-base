/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.ModelDataType
import io.openapiprocessor.core.model.datatypes.NullDataType
import io.openapiprocessor.core.model.datatypes.PropertyDataType
import io.openapiprocessor.core.support.capitalizeFirstChar
import java.io.Writer

/**
 * Writer for POJO classes.
 */
class DataTypeWriterPojo(
    apiOptions: ApiOptions,
    generatedWriter: GeneratedWriter,
    validationAnnotations: BeanValidationFactory = BeanValidationFactory(apiOptions),
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
            var propSource = getProp(
                propName,
                javaPropertyName,
                propDataType as PropertyDataType,
                dataType.isRequired(propName),
                Access.PRIVATE)

            // null (JsonNullable) may have an init value
            val pDataType = propDataType.dataType
            if (pDataType is NullDataType && pDataType.init != null) {
                propSource += " = ${pDataType.init}"
            }

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
}
