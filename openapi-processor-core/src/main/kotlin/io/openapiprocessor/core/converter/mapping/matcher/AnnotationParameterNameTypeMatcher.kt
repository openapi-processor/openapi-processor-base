/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.matcher

import io.openapiprocessor.core.converter.mapping.AnnotationNameMapping
import io.openapiprocessor.core.converter.mapping.Mapping
import io.openapiprocessor.core.converter.mapping.MappingMatcher
import io.openapiprocessor.core.converter.mapping.MappingQuery
import io.openapiprocessor.core.converter.mapping.steps.MappingStep
import io.openapiprocessor.core.converter.mapping.steps.MatcherStep

class AnnotationParameterNameTypeMatcher(private val query: MappingQuery): MappingMatcher {

    override fun match(mapping: Mapping, step: MappingStep): Boolean {
        if (mapping !is AnnotationNameMapping) {
            step.add(MatcherStep(mapping, false))
            return false
        }

        val match = mapping.name == query.name
        step.add(MatcherStep(mapping, match))
        return match
    }
}
