/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.wrapper

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.MappingFinder
import io.openapiprocessor.core.converter.MappingQuery
import io.openapiprocessor.core.converter.SchemaInfo
import io.openapiprocessor.core.converter.mapping.*
import io.openapiprocessor.core.model.datatypes.CollectionDataType
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.MappedCollectionDataType

/**
 * replaces a collection wrapper with the 'multi' data mapping.
 *
 * Used to replace the collection wrapper at Responses or RequestBody's with  {@code Flux<>} or
 * similar types.
 */
class MultiDataTypeWrapper(
    private val options: ApiOptions,
    private val finder: MappingFinder = MappingFinder(options)
) {

    /**
     * replaces a converted array data type with a multi data type (like {@code Flux< >})
     * wrapping the collection item.
     *
     * If the configuration for the result type is 'plain' or not defined the source data type
     * is not changed.
     *
     * @param dataType the data type to wrap
     * @param schemaInfo the open api type with context information
     * @return the resulting java data type
     */
    fun wrap(dataType: DataType, schemaInfo: SchemaInfo): DataType {
        if (!schemaInfo.isArray()) {
            return dataType
        }

        val targetType = getMultiDataType(schemaInfo)
        if (targetType == null) {
            return dataType
        }

        if (targetType.typeName == "plain") {
            return dataType
        }

        return MappedCollectionDataType(
            targetType.getName(),
            targetType.getPkg(),
            (dataType as CollectionDataType).item,
            null,
            false,
            multi = true
        )
    }

    private fun getMultiDataType(info: SchemaInfo): TargetType? {
        val match = finder.getMultiTypeMapping(MappingQuery(info))
        if (match != null) {
            return match.getTargetType()
        }

        return null
    }
}
