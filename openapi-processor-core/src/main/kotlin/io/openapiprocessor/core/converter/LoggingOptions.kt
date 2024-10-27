/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.converter.mapping.steps.Target
import io.openapiprocessor.core.converter.mapping.steps.LoggingOptions


class LoggingOptions(
    /**
     * log mapping lookups
     */
    override var mapping: Boolean = false,

    /**
     * log mapping lookups with logging library (slf4j) or to stdout
     *
     * this is a workaround for gradle.
     * Unfortunately gradle can only globally enable/disable log levels, i.e. enabling info will enable info logging
     * for everything which would hide the mapping lookup logging in a lot of noise. To avoid that we just log to
     * stdout.
     */
    override var mappingTarget: Target = Target.LOGGER
): LoggingOptions {
    val logger = mappingTarget == Target.LOGGER
    val stdout = mappingTarget == Target.STDOUT
}
