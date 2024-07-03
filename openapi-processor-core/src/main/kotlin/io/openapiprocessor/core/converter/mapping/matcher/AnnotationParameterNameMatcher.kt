/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.matcher

import io.openapiprocessor.core.converter.mapping.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AnnotationParameterNameMatcher(private val query: MappingQuery): MappingMatcher {
    val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun match(mapping: Mapping): Boolean {
        if (mapping !is AnnotationNameMapping) {
            log.trace("not matched: {}", mapping)
            return false
        }

        val match = mapping.name == query.name
        log.trace("${if (match) "" else "not "}matched: {}", mapping)
        return match
    }
}
