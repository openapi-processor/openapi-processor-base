/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser

import io.openapiprocessor.core.parser.Parser as ApiParser
import io.openapiprocessor.core.parser.openapi.Parser as OpenApiParser
import io.openapiprocessor.core.parser.swagger.Parser as Swagger
import java.util.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * OpenAPI parser abstraction. Supports swagger or openapi4 parser.
 */
class OpenApiParser {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    fun parse(processorOptions: Map<String, *>): OpenApi {
        val apiPath: String = processorOptions["apiPath"]?.toString() ?: throw NoOpenApiException()

        when(val parser = processorOptions["parser"]?.toString()) {
            ParserType.SWAGGER.name -> {
                log.info("using SWAGGER parser")
                return Swagger().parse(apiPath)
            }
            ParserType.OPENAPI4J.name -> {
                log.info("using (deprecated) OPENAPI4J parser")
                val openapi4j = load(ParserType.OPENAPI4J.name)
                return openapi4j.parse(apiPath)
            }
            ParserType.INTERNAL.name -> {
                log.info("using INTERNAL parser")
                return OpenApiParser().parse(apiPath)
            }
            else -> {
                if (parser != null) {
                    log.warn("unknown parser type: {}", parser)
                    log.warn("available parsers: INTERNAL, SWAGGER, OPENAPI4J")
                }
                return Swagger().parse(apiPath)
            }
        }
    }

    private fun load(name: String): ApiParser {
        val provider = ServiceLoader.load(ParserProvider::class.java)
            .find { p -> p.getName() == name } ?: throw NoParserException(name)

        return provider.getParser()
    }
}
