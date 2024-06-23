/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.openapiprocessor.core.processor.mapping.v2.ResultStyle
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Mappings(
    private val resultTypeMapping: ResultTypeMapping?,
    private val resultStyle: ResultStyle?,
    private val singleTypeMapping: TypeMapping?,
    private val multiTypeMapping: TypeMapping?,
    private val typeMappings: TypeMappings
) {
    val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    fun getGlobalResultTypeMapping(): ResultTypeMapping? {
        return resultTypeMapping
    }

    fun getGlobalResultStyle(): ResultStyle? {
        return resultStyle
    }

    fun getGlobalSingleTypeMapping(): TypeMapping? {
        return singleTypeMapping
    }

    fun getGlobalMultiTypeMapping(): TypeMapping? {
        return multiTypeMapping
    }

    fun findGlobalTypeMapping(filter: MappingMatcher): TypeMapping? {
        log.trace("looking for global type mapping of {}", filter)

        val mappings = typeMappings.filter(filter)
        if (mappings.isEmpty()) {
            return null
        }

        if (mappings.size > 1) {
            throw AmbiguousTypeMappingException(mappings.toTypeMapping())
        }

        return mappings.first() as TypeMapping
    }
}
