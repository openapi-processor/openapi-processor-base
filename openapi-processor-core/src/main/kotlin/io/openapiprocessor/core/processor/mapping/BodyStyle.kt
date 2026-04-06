/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping

enum class BodyStyle(val kind: String) {
    OBJECT("object"),
    DESTRUCTURE("destructure")
}
