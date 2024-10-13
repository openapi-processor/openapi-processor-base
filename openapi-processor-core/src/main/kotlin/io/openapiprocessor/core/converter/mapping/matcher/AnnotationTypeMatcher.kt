/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.matcher

import io.openapiprocessor.core.converter.mapping.*
import io.openapiprocessor.core.converter.mapping.steps.MappingStep
import io.openapiprocessor.core.converter.mapping.steps.MatcherStep
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AnnotationTypeMatcher(private val query: MappingQueryType): MappingMatcher {
    val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    @Deprecated("replace", replaceWith = ReplaceWith("match(mapping, step)"))
    override fun match(mapping: Mapping): Boolean {
        TODO("obsolete")
    }

    override fun match(mapping: Mapping, step: MappingStep): Boolean {
        if (mapping !is AnnotationTypeMapping) {
            step.add(MatcherStep(mapping, false))
            return false
        }

        val matchName = mapping.sourceTypeName == query.name
        val matchObject = mapping.sourceTypeName == "object"
        val matchType = mapping.sourceTypeName == query.type
        val matchFormat = mapping.sourceTypeFormat == query.format

        val match = (matchName && matchFormat)
                || (matchType && matchFormat)
                || (query.allowObject && matchObject)

        step.add(MatcherStep(mapping, match))
        return match
    }

    override fun toString(): String {
        return query.toString()
    }
}
