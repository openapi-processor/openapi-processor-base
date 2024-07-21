/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor

import io.openapiprocessor.core.converter.mapping.MappingData
import io.openapiprocessor.core.processor.mapping.MappingVersion
import io.openapiprocessor.core.processor.mapping.v2.Mapping as MappingV2
import io.openapiprocessor.core.processor.mapping.v2.MappingConverter as MappingConverterV2

/**
 *  Converter for the type mapping from the mapping yaml. It converts the type mapping information
 *  into the format used by [io.openapiprocessor.core.converter.DataTypeConverter].
 */
class MappingConverter {

    fun convertX(source: MappingVersion?): MappingData {
        if (source != null && source.v2) {
            return MappingConverterV2(source as MappingV2).convertX2()
        }

        return MappingData()
    }
}
