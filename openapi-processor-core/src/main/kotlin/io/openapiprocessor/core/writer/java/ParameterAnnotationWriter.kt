/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.model.parameters.Parameter
import java.io.Writer

/**
 * parameter annotation writer interface.
 */
fun interface ParameterAnnotationWriter {
    fun write (target: Writer, parameter: Parameter)
}
