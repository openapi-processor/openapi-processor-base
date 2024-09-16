/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer

import java.io.Writer

interface WriterFactory {
    // todo rename to createSourceWriter()
    fun createWriter(packageName: String, className: String): Writer
    fun createResourceWriter(resourceName: String): Writer
}
