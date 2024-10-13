/*
 * Copyright 2024 https://github.com/openapi-processor-base/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.steps

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TypesStep(): MappingStep {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    private val steps: MutableCollection<MappingStep> = ArrayList()

    override fun isMatch(): Boolean {
        return steps.any { it.isMatch() }
    }

    override fun add(step: MappingStep): MappingStep {
        val found = steps.find { it.isEqual(step) }
        if(found != null) {
            return found
        }

        steps.add(step)
        return step
    }

    override fun isEqual(step: MappingStep): Boolean {
        return step is TypesStep
    }

    override fun log(indent: String) {
        val prefix = if (isMatch()) {
            "$indent$MATCH"
        } else {
            "$indent$NO_MATCH"
        }

        log.trace(prefix, "types")
        steps.forEach {
            it.log("$indent  ")
        }
    }
}
