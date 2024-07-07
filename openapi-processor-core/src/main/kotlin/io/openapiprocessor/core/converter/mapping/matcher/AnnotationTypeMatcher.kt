/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.matcher

import io.openapiprocessor.core.converter.mapping.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AnnotationTypeMatcher(private val query: MappingQueryType): MappingMatcher {
    val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun match(mapping: Mapping): Boolean {
        if (mapping !is AnnotationTypeMapping) {
            log.trace("not matched: {}", mapping)
            return false
        }

        val matchObject = mapping.sourceTypeName == "object"
        val matchType = mapping.sourceTypeName == query.type
        val matchFormat = mapping.sourceTypeFormat == query.format
        val match = (matchType && matchFormat) || (query.allowObject && matchObject)

        log.trace("${if (match) "" else "not "}matched: {}", mapping)
        return match
    }

    override fun toString(): String {
        return query.toString()
    }
}
