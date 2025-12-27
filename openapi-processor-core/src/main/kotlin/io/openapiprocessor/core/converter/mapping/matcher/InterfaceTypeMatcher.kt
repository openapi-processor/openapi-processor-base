/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.matcher

import io.openapiprocessor.core.converter.mapping.InterfaceTypeMapping
import io.openapiprocessor.core.converter.mapping.Mapping
import io.openapiprocessor.core.converter.mapping.MappingMatcher
import io.openapiprocessor.core.converter.mapping.MappingQuery
import io.openapiprocessor.core.converter.mapping.steps.MappingStep
import io.openapiprocessor.core.converter.mapping.steps.MatcherStep

class InterfaceTypeMatcher(private val query: MappingQuery): MappingMatcher {

    override fun match(mapping: Mapping, step: MappingStep): Boolean {
        if (mapping !is InterfaceTypeMapping) {
            step.add(MatcherStep(mapping, false))
            return false
        }

        val matchName = mapping.sourceTypeName == query.name
        val matchObject = mapping.sourceTypeName == "object"
        val matchType = mapping.sourceTypeName == query.type

        val match = matchName
                || matchType
                || (query.allowObject && matchObject)

        step.add(MatcherStep(mapping, match))
        return match
    }

    override fun toString(): String {
        return query.toString()
    }
}
