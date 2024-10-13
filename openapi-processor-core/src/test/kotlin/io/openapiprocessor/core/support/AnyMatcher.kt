/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.converter.mapping.Mapping
import io.openapiprocessor.core.converter.mapping.MappingMatcher
import io.openapiprocessor.core.converter.mapping.steps.MappingStep
import io.openapiprocessor.core.converter.mapping.steps.MatcherStep

class AnyMatcher: MappingMatcher {

    override fun match(mapping: Mapping, step: MappingStep): Boolean {
        step.add(MatcherStep(mapping, true))
        return true;
    }
}
