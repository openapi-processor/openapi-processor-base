/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor

import io.openapiprocessor.core.converter.mapping.MappingData
import io.openapiprocessor.core.processor.mapping.Mapping
import io.openapiprocessor.core.processor.mapping.MappingConverter

/**
 *  Converter for the type mapping from the mapping YAML. It converts the type mapping information
 *  into the format used by [io.openapiprocessor.core.converter.DataTypeConverter].
 */
class MappingConverter {

    fun convert(source: Mapping?): MappingData {
        if (source == null) {
            return MappingData()
        }

        return MappingConverter(source).convert()
    }
}
