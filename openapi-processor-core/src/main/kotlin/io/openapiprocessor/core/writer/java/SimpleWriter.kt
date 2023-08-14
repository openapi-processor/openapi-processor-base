/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import java.io.Writer

/**
 * basic writer interface.
 */
fun interface SimpleWriter {
    fun write (target: Writer)
}
