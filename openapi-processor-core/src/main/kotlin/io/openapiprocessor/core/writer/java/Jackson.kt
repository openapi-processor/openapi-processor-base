/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

enum class JacksonFormat(val pkg: String) {
    V2("com.fasterxml.jackson"),
    V3("tools.jackson")
}
