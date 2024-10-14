/*
 * Copyright 2024 https://github.com/openapi-processor-base/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.steps

import io.openapiprocessor.core.converter.mapping.MappingQuery
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class EndpointsStep(val query: MappingQuery): ItemsStep() {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun add(step: MappingStep): MappingStep {
        val found = steps.find { it.isEqual(step) }
        if(found != null) {
            return found
        }

        steps.add(step)
        return step
    }

    override fun isEqual(step: MappingStep): Boolean {
        if (step !is EndpointsStep) {
            return false
        }

        return query.path == step.query.path
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

        log.trace(prefix, query.path)
        steps.forEach {
            it.log("$indent  ")
        }
    }
}
