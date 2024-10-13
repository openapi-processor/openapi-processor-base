/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.openapiprocessor.core.converter.mapping.steps.MappingStep

fun interface MappingMatcher {
    @Deprecated(message = "replace", replaceWith = ReplaceWith("match(mapping, step)") )
    fun match(mapping: Mapping): Boolean

    fun match(mapping: Mapping, step: MappingStep): Boolean {
        return false
    }
}
