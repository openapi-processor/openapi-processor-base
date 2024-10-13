/*
 * Copyright 2024 https://github.com/openapi-processor-base/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.steps

import io.openapiprocessor.core.converter.mapping.Mapping
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MatcherStep(val mapping: Mapping, val match: Boolean): MappingStep {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun isMatch(): Boolean {
        return match
    }

    override fun hasMappings(): Boolean {
        return true
    }

    override fun add(step: MappingStep): MappingStep {
        TODO("never called")
    }

    override fun isEqual(step: MappingStep): Boolean {
        return false
    }

    override fun log(indent: String) {
        if (isMatch()) {
            log.trace("$indent$MATCH", mapping)
        } else {
            log.trace("$indent$NO_MATCH", mapping)
        }
    }
}
