/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi4j

import io.openapiprocessor.core.parser.OpenApi
import io.openapiprocessor.test.stream.Memory
import org.openapi4j.parser.OpenApi3Parser
import org.openapi4j.parser.validation.v3.OpenApi3Validator

import java.net.URL

fun parse(yaml: String): OpenApi {
    Memory.add("openapi.yaml", yaml)

    val api = OpenApi3Parser()
        .parse(URL("memory:openapi.yaml"), true)

    val results = OpenApi3Validator
        .instance()
        .validate(api)

    return OpenApi(api, results)
}
