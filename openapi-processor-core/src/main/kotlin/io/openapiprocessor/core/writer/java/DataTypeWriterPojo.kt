/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.ModelDataType
import io.openapiprocessor.core.model.datatypes.NullDataType
import io.openapiprocessor.core.support.capitalizeFirstChar
import io.openapiprocessor.core.writer.Identifier
import java.io.Writer

/**
 * Writer for POJO classes.
 */
class DataTypeWriterPojo(
    apiOptions: ApiOptions,
    identifier: Identifier,
    generatedWriter: GeneratedWriter,
    validationAnnotations: BeanValidationFactory = BeanValidationFactory(apiOptions),
    javadocWriter: JavaDocWriter = JavaDocWriter(identifier)
) : DataTypeWriterBase(apiOptions, identifier, generatedWriter, validationAnnotations, javadocWriter) {

    override fun write(target: Writer, dataType: ModelDataType) {
        val propsData = collectPropertiesData(dataType)
        writeFileHeader(target, dataType, propsData)
        writePreClass(target, dataType, propsData)
        writeClass(target, dataType, propsData)
    }

    private fun writeClass(target: Writer, dataType: ModelDataType, propsData: List<PropertyData>) {
        writeClassOpen(target, dataType)
        writeClassProperties(target, dataType, propsData)
        writeClassMethods(target, dataType, propsData)
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

    private fun writeClassProperties(target: Writer, dataType: ModelDataType, propsData: List<PropertyData>) {
        val props = mutableListOf<String>()

        propsData.forEach { propData ->
            var prop = getProp(propData, Access.PRIVATE)

            // null (JsonNullable) may have an init value
            val pDataType = propData.propDataType.dataType
            if (pDataType is NullDataType && pDataType.init != null) {
                prop += " = ${pDataType.init}"
            }

            props.add(prop)
        }

        target.write(props.joinToString(";\n\n"))
        if (props.isNotEmpty()) {
            target.write(";\n\n")
        }
    }

    private fun writeClassMethods(target: Writer, dataType: ModelDataType, propsData: List<PropertyData>) {
        propsData.forEach {
            target.write(getGetter(it.baseName, it.propDataType))
            target.write(getSetter(it.baseName, it.propDataType))
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

        val methodName = "get${identifier.toMethodTail(propertyName.capitalizeFirstChar())}"

        result += """
            |    public ${propDataType.getTypeName()} $methodName() {
            |        return ${identifier.toIdentifier(propertyName)};
            |    }
            |
            |
        """.trimMargin()

        return result
    }

    private fun getSetter(propertyName: String, propDataType: DataType): String {
        var result = ""
        result += ifDeprecated(propDataType)

        val property = identifier.toIdentifier(propertyName)
        val methodName = "set${identifier.toMethodTail(propertyName.capitalizeFirstChar())}"

        result += """
            |    public void ${methodName}(${propDataType.getTypeName()} ${property}) {
            |        this.${property} = ${property};
            |    }
            |
            |
        """.trimMargin()

        return result
    }
}
