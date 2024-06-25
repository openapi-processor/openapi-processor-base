/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.matcher

import io.openapiprocessor.core.converter.mapping.AddParameterTypeMapping
import io.openapiprocessor.core.converter.mapping.Mapping
import io.openapiprocessor.core.converter.mapping.MappingMatcher

class AddParameterTypeMatcher: MappingMatcher {
    override fun match(mapping: Mapping): Boolean {
        return mapping is AddParameterTypeMapping
    }

    override fun toString(): String {
        return "additional parameter"
    }
}
