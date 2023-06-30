/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.ModelDataType
import io.openapiprocessor.core.model.datatypes.PropertyDataType
import java.io.Writer

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
                dataType.isRequired(propName),
                Access.NONE)

            // todo can't init record parameter here
            // null (JsonNullable) may have an init value
            // val dataType = propDataType.dataType
            // if (dataType is NullDataType && dataType.init != null) {
            //     result += " = ${dataType.init}"
            // }

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
}
