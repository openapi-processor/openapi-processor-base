/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.parser.ParserType
import io.openapiprocessor.core.parser.openapi.Parser
import io.openapiprocessor.test.stream.Memory
import io.openapiprocessor.core.openapi.OpenApi as OpenApiOpenApi
import io.openapiprocessor.test.parser.openapi4j.parse as parseWithOpenApi4j
import io.openapiprocessor.test.parser.swagger.parse as parseWithSwagger

/**
 * OpenAPI parser to read YAML from memory using the given parser.
 *
 * extract individual Schemas with the get...Schema() functions on the [OpenApiOpenApi] result.
 */
fun parseApi(apiYaml: String, parserType: ParserType = ParserType.INTERNAL): OpenApiOpenApi {
    return when (parserType) {
        ParserType.SWAGGER -> parseWithSwagger(apiYaml)
        ParserType.OPENAPI4J -> parseWithOpenApi4j(apiYaml)
        ParserType.INTERNAL -> parseWithInternal(apiYaml)
    }
}

fun parseApi(
    header: String =
        """
        |openapi: 3.1.0
        |info:
        |  title: test API
        |  version: 1.0.0
        |
        """,
    body: String,
    parserType: ParserType = ParserType.INTERNAL
): OpenApiOpenApi {
    val merged = (
        header.trimMargin()
      + body.trimIndent()
    )
    return parseApi(merged, parserType)
}

fun parseApiBody(
    body: String,
    parserType: ParserType = ParserType.INTERNAL
): OpenApiOpenApi {
    return parseApi(body, parserType)
}

/** groovy support */

fun parseApiBody(body: String): OpenApiOpenApi {
    return parseApi(body = body)
}

fun parseWithInternal(yaml: String): OpenApiOpenApi {
    Memory.add("openapi.yaml", yaml)
    return Parser().parse("memory:openapi.yaml")
}

fun printWarnings(warnings: List<String>) {
    if (warnings.isEmpty()) {
        return
    }

    println("OpenAPI warnings:")
    warnings.forEach {
        println(it)
    }
}
