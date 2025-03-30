/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser

import io.openapiprocessor.core.parser.openapi.Parser as OpenApiParser
import java.util.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * OpenAPI parser abstraction. Supports internal, swagger or openapi4j parser.
 */
class OpenApiParser {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    fun parse(processorOptions: Map<String, *>): OpenApi {
        val apiPath: String = processorOptions["apiPath"]?.toString() ?: throw NoOpenApiException()

        return when(val parser = processorOptions["parser"]?.toString()) {
            ParserType.SWAGGER.name -> {
                log.info("using SWAGGER parser")
                val swagger = load(ParserType.SWAGGER.name)
                swagger.parse(apiPath)
            }
            ParserType.OPENAPI4J.name -> {
                log.info("using (deprecated) OPENAPI4J parser")
                val openapi4j = load(ParserType.OPENAPI4J.name)
                openapi4j.parse(apiPath)
            }
            ParserType.INTERNAL.name -> {
                log.info("using INTERNAL parser")
                OpenApiParser().parse(apiPath)
            }
            else -> {
                if (parser != null) {
                    log.warn("unknown parser type: {}", parser)
                    log.warn("available parser:")
                    log.warn("  INTERNAL  (OpenAPI 3.1/0) - preferred")
                    log.warn("  SWAGGER   (OpenAPI 3.0)   - alternative")
                    log.warn("  OPENAPI4J (OpenAPI 3.0)   - deprecated")
                }
                OpenApiParser().parse(apiPath)
            }
        }
    }

    private fun load(name: String): Parser {
        val provider = ServiceLoader.load(ParserProvider::class.java)
            .find { p -> p.getName() == name } ?: throw NoParserException(name)

        return provider.getParser()
    }
}
