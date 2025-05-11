/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.openapiprocessor.core.converter.ApiConverter
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.OptionsConverter
import io.openapiprocessor.core.framework.FrameworkBase
import io.openapiprocessor.core.parser.OpenApiParser
import io.openapiprocessor.core.writer.SourceFormatter
import io.openapiprocessor.core.writer.java.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *  Simple processor for testing.
 */
class TestProcessor:
    io.openapiprocessor.api.v2.OpenApiProcessor,
    io.openapiprocessor.api.v1.OpenApiProcessor,
    io.openapiprocessor.test.api.OpenApiProcessorTest
{
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    private lateinit var apiOptions: ApiOptions

    override fun getName(): String {
        return "test"
    }

    override fun run(processorOptions: MutableMap<String, *>) {
        try {
            val parser = OpenApiParser ()
            val openapi = parser.parse(processorOptions)
            if (processorOptions.containsKey("showWarnings")) {
                openapi.printWarnings()
            }

            val options = convertOptions(processorOptions)
            apiOptions = options
            val identifier = JavaIdentifier(IdentifierOptions(options.identifierWordBreakFromDigitToLetter))
            val cv = ApiConverter(options, identifier, FrameworkBase())
            val api = cv.convert(openapi)

            val generatedInfo = GeneratedInfo("openapi-processor-core", "test")
            val generatedWriter = GeneratedWriterImpl(generatedInfo, options)
            val validationWriter = ValidationWriter(options, generatedWriter)
            val beanValidation = BeanValidationFactory(options)
            val javaDocWriter = JavaDocWriter(identifier)
            val formatter = getFormatter()

            val writer = ApiWriter(
                options,
                generatedWriter,
                validationWriter,
                InterfaceWriter(
                    options,
                    generatedWriter,
                    MethodWriter(
                        options,
                        identifier,
                        TestProcessorStatusAnnotationWriter(),
                        TestProcessorMappingAnnotationWriter(),
                        TestProcessorParameterAnnotationWriter(),
                        beanValidation,
                        javaDocWriter
                    ),
                    TestFrameworkAnnotations(),
                    beanValidation,
                    DefaultImportFilter()
                ),
                when (options.modelType) {
                    "record" -> DataTypeWriterRecord(
                        options,
                        identifier,
                        generatedWriter,
                        beanValidation,
                        javaDocWriter
                    )
                    else -> DataTypeWriterPojo(
                        options,
                        identifier,
                        generatedWriter,
                        beanValidation,
                        javaDocWriter
                    )
                },
                StringEnumWriter(options, identifier, generatedWriter),
                InterfaceDataTypeWriter(
                    options,
                    generatedWriter,
                    javaDocWriter
                ),
                listOf(),
                formatter
            )

            writer.write(api)
        } catch (e: Exception) {
            log.error ("processing failed!", e)
            throw e
        }
    }

    override fun getSourceRoot(): String? {
        if (apiOptions.targetDirOptions.standardLayout) {
            return "java"
        }
        return null
    }

    override fun getResourceRoot(): String? {
        if (apiOptions.targetDirOptions.standardLayout) {
            return "resources"
        }
        return null
    }

    private fun getFormatter(): SourceFormatter {
        return SourceFormatterFactory().getFormatter(apiOptions)
    }
}

private fun convertOptions(processorOptions: MutableMap<String, *>): ApiOptions {
    val target = mutableMapOf<String, Any>()
    processorOptions.forEach {(key, value) ->
        target[key] = value!!
    }

    val options = OptionsConverter().convertOptions(target)
    options.validate()
    return options
}
