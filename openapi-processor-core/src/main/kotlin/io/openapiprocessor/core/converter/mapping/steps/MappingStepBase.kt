/*
 * Copyright 2024 https://github.com/openapi-processor-base/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.steps

import org.slf4j.LoggerFactory

abstract class MappingStepBase : MappingStep {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun log(message: String, vararg args: Any?) {
        if (!enabled())
            return

        if (useLogger()) {
            log.info(message, *args)
        } else {
            var resolved = message
            args.forEach {
                resolved = resolved.replaceFirst("{}", it.toString())
            }
            println(resolved)
        }
    }

    private fun enabled(): Boolean {
        return options.get().mapping
    }

    private fun useLogger(): Boolean {
        return options.get().mappingTarget == Target.LOGGER
    }

    companion object {
        val options: ThreadLocal<LoggingOptions> = ThreadLocal.withInitial {
            object : LoggingOptions {
                override var mapping = false
                override var mappingTarget = Target.LOGGER
            }
        }
    }
}
