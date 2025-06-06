/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support.datatypes

import io.openapiprocessor.core.model.Documentation
import io.openapiprocessor.core.model.datatypes.*

fun propertyDataType(dataType: DataType): PropertyDataType {
    return PropertyDataType(readOnly = false, writeOnly = false, dataType = dataType, documentation = Documentation())
}

fun propertyDataType(dataType: DataType, extensions: Map<String, *>): PropertyDataType {
    return PropertyDataType(readOnly = false, writeOnly = false, dataType = dataType, documentation = Documentation(), extensions)
}

fun propertyDataTypeString(readOnly: Boolean = false, writeOnly: Boolean = false): PropertyDataType {
    return PropertyDataType(readOnly = readOnly, writeOnly = writeOnly, dataType = StringDataType(), documentation = Documentation())
}
