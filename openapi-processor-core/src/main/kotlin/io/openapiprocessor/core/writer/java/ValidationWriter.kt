/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.writer.SourceFormatter
import io.openapiprocessor.core.writer.WriterFactory
import java.io.StringWriter
import java.io.Writer

class ValidationWriter(
    private val options: ApiOptions,
    private val generatedWriter: GeneratedWriter,
    private val writer: StringValuesWriter = StringValuesWriter(options, generatedWriter)
) {
    fun write(formatter: SourceFormatter, writerFactory: WriterFactory) {
        if (!options.beanValidation)
            return

        val annotationWriter = createAnnotationWriter(writerFactory)
        writeValues(annotationWriter, formatter)
        annotationWriter.close()

        val validatorWriter = createValidatorWriter(writerFactory)
        writeValueValidator(validatorWriter, formatter)
        validatorWriter.close()
    }

    private fun createAnnotationWriter(writerFactory: WriterFactory): Writer {
        return writerFactory.createWriter("${options.packageName}.validation", "Values")
    }

    private fun createValidatorWriter(writerFactory: WriterFactory): Writer {
        return writerFactory.createWriter("${options.packageName}.validation", "ValueValidator")
    }

    private fun writeValues(writer: Writer, formatter: SourceFormatter) {
        val raw = StringWriter()
        this.writer.writeValues(raw)
        writer.write(formatter.format(raw.toString()))
    }

    private fun writeValueValidator(writer: Writer, formatter: SourceFormatter) {
        val raw = StringWriter()
        this.writer.writeValueValidator(raw)
        writer.write(formatter.format(raw.toString()))
    }
}
