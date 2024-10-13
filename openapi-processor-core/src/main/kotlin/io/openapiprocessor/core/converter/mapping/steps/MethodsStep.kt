/*
 * Copyright 2024 https://github.com/openapi-processor-base/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.steps

import io.openapiprocessor.core.converter.mapping.MappingQuery
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MethodsStep(val query: MappingQuery): MappingStep {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    private val steps: MutableCollection<MappingStep> = ArrayList()

    override fun isMatch(): Boolean {
        return steps.any { it.isMatch() }
    }

    override fun hasMappings(): Boolean {
        return steps.any { it.hasMappings() }
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
        if (step !is MethodsStep) {
            return false
        }

        return query.method == step.query.method
    }

    override fun log(indent: String) {
        if (!hasMappings()) {
            return
        }

        val prefix = if (isMatch()) {
            "$indent$MATCH"
        } else {
            "$indent$NO_MATCH"
        }

        log.trace(prefix, "methods")
        steps.forEach {
            it.log("$indent  ")
        }
    }
}
