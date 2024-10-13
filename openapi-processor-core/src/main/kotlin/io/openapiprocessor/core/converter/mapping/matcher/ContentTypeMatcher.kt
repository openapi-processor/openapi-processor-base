/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.matcher

import io.openapiprocessor.core.converter.mapping.*
import io.openapiprocessor.core.converter.mapping.steps.MappingStep
import io.openapiprocessor.core.converter.mapping.steps.MatcherStep

class ContentTypeMatcher(private val query: MappingQuery): MappingMatcher, (ContentTypeMapping) -> Boolean {

    override fun match(mapping: Mapping, step: MappingStep): Boolean {
        if (mapping !is ContentTypeMapping) {
            step.add(MatcherStep(mapping, false))
            return false
        }

        val match = this.invoke(mapping)
        step.add(MatcherStep(mapping, match))
        return match
    }

    override fun invoke(mapping: ContentTypeMapping): Boolean {
        return mapping.contentType == query.contentType
    }
}
