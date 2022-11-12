/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.logging

import javax.annotation.processing.Messager

class MessagerLoggerFactory(private val messager: Messager): LoggerFactory {
    override fun getLogger(name: String): Logger {
        return MessagerLogger(messager, name)
    }
}
