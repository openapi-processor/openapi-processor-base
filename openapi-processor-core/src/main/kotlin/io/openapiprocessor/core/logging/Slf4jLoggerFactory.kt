/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.logging

class Slf4jLoggerFactory: LoggerFactory {

    override fun getLogger(name: String): Logger {
        return Slf4jLogger(org.slf4j.LoggerFactory.getLogger(name))
    }
}
