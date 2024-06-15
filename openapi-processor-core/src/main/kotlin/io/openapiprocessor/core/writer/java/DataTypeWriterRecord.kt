/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.ModelDataType
import io.openapiprocessor.core.writer.Identifier
import java.io.Writer

/**
 * Writer for POJO classes with java 14 records.
 */
class DataTypeWriterRecord(
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
        writeRecord(target, dataType, propsData)
    }

    private fun writeRecord(target: Writer, dataType: ModelDataType, propsData: List<PropertyData>) {
        writeRecordOpen(target, dataType)
        writeRecordParameter(target, dataType, propsData)
        writeRecordImplements(target, dataType)
        writeRecordClose(target)
    }

    private fun writeRecordOpen(target: Writer, dataType: ModelDataType) {
        writeRecordHeader(target, dataType)
    }

    private fun writeRecordParameter(target: Writer, dataType: ModelDataType, propsData: List<PropertyData>) {
        val props = mutableListOf<String>()

        propsData.forEach { propData ->
            val prop = getProp(
                propData.srcPropName,
                propData.propName,
                propData.propDataType,
                propData.required,
                Access.NONE)

            // todo can't init record parameter here
            // null (JsonNullable) may have an init value
            // val dataType = propDataType.dataType
            // if (dataType is NullDataType && dataType.init != null) {
            //     result += " = ${dataType.init}"
            // }

            props.add(prop)
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
}
