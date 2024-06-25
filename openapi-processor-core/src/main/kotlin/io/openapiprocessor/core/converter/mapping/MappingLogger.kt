/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory

fun enableTraceMapping() {
    val log = LoggerFactory.getLogger("io.openapiprocessor.core.converter.mapping") as Logger
    log.level = Level.TRACE
}
