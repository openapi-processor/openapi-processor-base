/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory

fun enableMappingLookupTracing() {
    val log = LoggerFactory.getLogger(Mapping::class.java.packageName) as Logger
    log.level = Level.TRACE
}
