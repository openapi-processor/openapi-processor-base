/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.wrapper

import io.openapiprocessor.core.converter.*
import io.openapiprocessor.core.converter.MappingFinderQuery
import io.openapiprocessor.core.converter.mapping.*
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.GenericDataType
import io.openapiprocessor.core.model.datatypes.NoneDataType
import io.openapiprocessor.core.model.datatypes.ResultDataType
import io.openapiprocessor.core.writer.Identifier

/**
 * wraps the result data type with the mapped result type.
 */
class ResultDataTypeWrapper(
    private val options: ApiOptions,
    private val identifier: Identifier,
    private val finder: MappingFinder = MappingFinder(options)
) {
    /**
     * wraps a (converted) result data type with the configured result java data type like
     * {@code ResponseEntity}.
     *
     * If the configuration for the result type is 'plain' the source data type is not wrapped.
     *
     * @param dataType the data type to wrap
     * @param schemaInfo the open api type with context information
     * @return the resulting java data type
     */
    fun wrap(dataType: DataType, schemaInfo: SchemaInfo): DataType {
        val targetType = getMappedResultDataType(schemaInfo)
        if (targetType == null) {
            return dataType
        }

        return when (targetType.typeName) {
            "plain" -> {
                dataType
            }
            else -> {
                ResultDataType (
                    targetType.getName(),
                    targetType.getPkg(),
                    checkNone (dataType),
                    convertGenerics(targetType)
                )
            }
        }
    }

    private fun checkNone(dataType: DataType): DataType {
        if (dataType is NoneDataType) {
            return dataType.wrappedInResult ()
        }

        return dataType
    }

    private fun getMappedResultDataType(info: SchemaInfo): TargetType? {
        val match = finder.getResultTypeMapping(MappingFinderQuery(info))
        if (match != null) {
            return match.getTargetType()
        }

        return null
    }

    private fun convertGenerics(targetType: TargetType): List<GenericDataType> {
        return GenericDataTypeConverter(options, identifier).convertGenerics(targetType)
    }
}
