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
import io.openapiprocessor.core.writer.java.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *  Simple processor for testing.
 */
class TestProcessor:
    io.openapiprocessor.api.v2.OpenApiProcessor,
    io.openapiprocessor.api.v1.OpenApiProcessor
{
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

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
            val cv = ApiConverter(options, FrameworkBase())
            val api = cv.convert(openapi)

            val generatedInfo = GeneratedInfo(
                "openapi-processor-core",
                "test"
                //if (options.generatedDate) OffsetDateTime.now().toString() else null
            )

            val generatedWriter = GeneratedWriterImpl(generatedInfo, options)
            val beanValidation = BeanValidationFactory(options)
            val javaDocWriter = JavaDocWriter()

            val writer = ApiWriter(
                options,
                generatedWriter,
                ValidationWriter(options),
                InterfaceWriter(
                    options,
                    generatedWriter,
                    MethodWriter(
                        options,
                        TestProcessorMappingAnnotationWriter(),
                        TestProcessorParameterAnnotationWriter(),
                        beanValidation,
                        JavaDocWriter()
                    ),
                    TestFrameworkAnnotations(),
                    beanValidation,
                    DefaultImportFilter()
                ),
                when (options.modelType) {
                    "record" -> DataTypeWriterRecord(
                        options,
                        generatedWriter,
                        beanValidation,
                        javaDocWriter
                    )
                    else -> DataTypeWriterPojo(
                        options,
                        generatedWriter,
                        beanValidation,
                        javaDocWriter
                    )
                },
                StringEnumWriter(generatedWriter),
                InterfaceDataTypeWriter(
                    options,
                    generatedWriter,
                    javaDocWriter
                )
            )

            writer.write(api)
        } catch (e: Exception) {
            log.error ("processing failed!", e)
            throw e
        }
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
