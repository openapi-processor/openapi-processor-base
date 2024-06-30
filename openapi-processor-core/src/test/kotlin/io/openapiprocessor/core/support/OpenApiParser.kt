/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.parser.OpenApi as ParserOpenApi
import io.openapiprocessor.core.parser.ParserType
import io.openapiprocessor.core.parser.openapi.Parser
import io.openapiprocessor.test.parser.swagger.parse as parseWithSwagger
import io.openapiprocessor.test.parser.openapi4j.parse as parseWithOpenApi4j
import io.openapiprocessor.test.stream.Memory

/**
 * OpenAPI parser to read yaml from memory using the given parser.
 *
 * extract individual Schemas with the get...Schema() functions on the [ParserOpenApi] result.
 */
fun parseApi(apiYaml: String, parserType: ParserType = ParserType.INTERNAL): ParserOpenApi {
    return parse(apiYaml, parserType)
}

fun parse(apiYaml: String, parserType: ParserType = ParserType.SWAGGER): ParserOpenApi {
    return when (parserType) {
        ParserType.SWAGGER -> parseWithSwagger(apiYaml)
        ParserType.OPENAPI4J -> parseWithOpenApi4j(apiYaml)
        ParserType.INTERNAL -> parseWithInternal(apiYaml)
    }
}

fun parseWithInternal(yaml: String): ParserOpenApi {
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
