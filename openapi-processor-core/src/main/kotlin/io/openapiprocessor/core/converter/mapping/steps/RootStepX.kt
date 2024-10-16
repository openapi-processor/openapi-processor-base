/*
 * Copyright 2024 https://github.com/openapi-processor-base/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.steps

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RootStepX(val message: String = "", val extension: String) : ItemsStep() {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun log(indent: String) {
        log.trace("{} '{}'", message, extension)
        if (!hasMappings()) {
            log.trace("$indent  $NO_MATCH", "no mappings")
            return
        }

        steps.filter { it.hasMappings() }
            .forEach { it.log("$indent  ") }
    }

    override fun isEqual(step: MappingStep): Boolean {
        return false
    }
}
