/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.matcher

import io.openapiprocessor.core.converter.mapping.DropParameterTypeMapping
import io.openapiprocessor.core.converter.mapping.Mapping
import io.openapiprocessor.core.converter.mapping.MappingMatcher
import io.openapiprocessor.core.converter.mapping.steps.MappingStep

class DropParameterTypeMatcher: MappingMatcher {

    override fun match(mapping: Mapping, step: MappingStep): Boolean {
        return mapping is DropParameterTypeMapping
    }

    override fun toString(): String {
        return "drop parameter"
    }
}
