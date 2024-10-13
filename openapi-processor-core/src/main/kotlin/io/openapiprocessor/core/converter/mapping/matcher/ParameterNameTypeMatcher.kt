/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.matcher

import io.openapiprocessor.core.converter.mapping.*
import io.openapiprocessor.core.converter.mapping.steps.MappingStep
import io.openapiprocessor.core.converter.mapping.steps.MatcherStep

class ParameterNameTypeMatcher(private val query: MappingQuery): MappingMatcher {

    override fun match(mapping: Mapping): Boolean {
        TODO("Not yet implemented")
    }

    override fun match(mapping: Mapping, step: MappingStep): Boolean {
        if (mapping !is NameTypeMapping) {
            step.add(MatcherStep(mapping, false))
            return false
        }

        val match = mapping.parameterName == query.name
        step.add(MatcherStep(mapping, match))
        return match
    }
}
