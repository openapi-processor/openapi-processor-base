/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.model.datatypes.ModelDataType
import java.io.Writer

fun interface DataTypeWriter {
    fun write(target: Writer, dataType: ModelDataType)
}
