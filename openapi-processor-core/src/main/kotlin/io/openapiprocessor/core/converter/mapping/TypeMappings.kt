/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.openapiprocessor.core.converter.mapping.steps.MappingStep
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TypeMappings(private val mappings: List<Mapping>): MappingBucket {
    val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    constructor(vararg mapping: Mapping): this(mapping.toList())

    override fun filter(filter: MappingMatcher, step: MappingStep): List<Mapping> {
        return mappings
            .filter { filter.match(it, step) }
    }
}
