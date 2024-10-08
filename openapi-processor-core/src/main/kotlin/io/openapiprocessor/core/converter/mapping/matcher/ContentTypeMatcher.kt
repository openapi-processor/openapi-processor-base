/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.matcher

import io.openapiprocessor.core.converter.mapping.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ContentTypeMatcher(private val query: MappingQuery): MappingMatcher, (ContentTypeMapping) -> Boolean {
    val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun match(mapping: Mapping): Boolean {
        if (mapping !is ContentTypeMapping) {
            log.trace("not matched: {}", mapping)
            return false
        }

        val match = this.invoke(mapping)
        log.trace("${if (match) "" else "not "}matched: {}", mapping)
        return match
    }

    override fun invoke(mapping: ContentTypeMapping): Boolean {
        return mapping.contentType == query.contentType
    }
}
