/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.Api
import io.openapiprocessor.core.model.Interface
import io.openapiprocessor.core.model.datatypes.InterfaceDataType
import io.openapiprocessor.core.model.datatypes.ModelDataType
import io.openapiprocessor.core.model.datatypes.StringEnumDataType
import io.openapiprocessor.core.writer.*
import java.io.StringWriter
import java.io.Writer

/**
 * Root writer for the generated api files.
 */
class ApiWriter(
    private val options: ApiOptions,
    private val generatedWriter: GeneratedWriter,
    private val validationWriter: ValidationWriter,
    private val interfaceWriter: InterfaceWriter,
    private val dataTypeWriter: DataTypeWriter,
    private val enumWriter: StringEnumWriter,
    private val interfaceDataTypeWriter: InterfaceDataTypeWriter,
    private val formatter: SourceFormatter = GoogleFormatter(),
    private val writerFactory: WriterFactory = DefaultWriterFactory(options)
) {
    init {
        if (writerFactory is InitWriterTarget) {
            writerFactory.init()
        }
    }

    fun write(api: Api) {
        writeGenerated()
        writeValidation()
        writeInterfaces(api)
        writeObjectDataTypes(api)
        writeInterfaceDataTypes(api)
        writeEnumDataTypes(api)
    }

    private fun writeGenerated () {
        val writer = getWriter("${options.packageName}.support", "Generated")
        writeGenerated(writer)
        writer.close()
    }

    private fun writeInterfaces(api: Api) {
        api.forEachInterface {
            val writer = getWriter(it.getPackageName(), it.getInterfaceName())
            writeInterface(writer, it)
            writer.close()
        }
    }

    private fun writeObjectDataTypes(api: Api) {
        api.forEachModelDataType {
            val writer = getWriter(it.getPackageName(), it.getTypeName())
            writeDataType(writer, it)
            writer.close()
        }
    }

    private fun writeInterfaceDataTypes(api: Api) {
        api.forEachInterfaceDataType {
            val writer = getWriter(it.getPackageName(), it.getTypeName())
            writeDataType(writer, it)
            writer.close()
        }
    }

    private fun writeEnumDataTypes(api: Api) {
        api.forEachEnumDataType {
            val writer = getWriter(it.getPackageName(), it.getTypeName())
            writeEnumDataType(writer, it)
            writer.close()
        }
    }

    private fun writeValidation() {
        validationWriter.write({ format(it) }, writerFactory)
    }

    private fun writeAdditionalFiles() {
        additionalWriter.forEach {
            it(options, { format(it) }, writerFactory)
        }
    }

    private fun writeInterface(writer: Writer, itf: Interface) {
        val raw = StringWriter()
        interfaceWriter.write(raw, itf)
        writer.write(format(raw.toString()))
    }

    private fun writeDataType(writer: Writer, dataType: ModelDataType) {
        val raw = StringWriter()
        dataTypeWriter.write(raw, dataType)
        writer.write(format(raw.toString ()))
    }

    private fun writeDataType(writer: Writer, dataType: InterfaceDataType) {
        val raw = StringWriter()
        interfaceDataTypeWriter.write(raw, dataType)
        writer.write(format(raw.toString ()))
    }

    private fun writeEnumDataType(writer: Writer, enumDataType: StringEnumDataType) {
        val raw = StringWriter()
        enumWriter.write(raw, enumDataType)
        writer.write(format(raw.toString()))
    }

    private fun writeGenerated(writer: Writer) {
        val raw = StringWriter()
        generatedWriter.writeSource(raw)
        writer.write(format(raw.toString()))
    }

    private fun getWriter(packageName: String, className: String): Writer {
        return writerFactory.createWriter(packageName, className)
    }

    private fun format(raw: String): String {
        if (!options.formatCode) {
            return raw
        }
        return formatter.format(raw)
    }
}
