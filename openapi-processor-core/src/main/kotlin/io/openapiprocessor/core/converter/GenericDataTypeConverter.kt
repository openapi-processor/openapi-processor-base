/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.converter.mapping.TargetType
import io.openapiprocessor.core.model.datatypes.DataTypeName
import io.openapiprocessor.core.model.datatypes.GenericDataType
import io.openapiprocessor.core.writer.Identifier

class GenericDataTypeConverter (
    private val options: ApiOptions,
    private val identifier: Identifier
) {
    /**
     * convert the generic type of [targetType] to [GenericDataType]s.
     */
    fun convertGenerics(targetType: TargetType): List<GenericDataType> {
        val genericDataTypes = mutableListOf<GenericDataType>()

        targetType.genericTypes.forEach {
            val typeName = it.getName()
            val dataTypeName = when {
                it.typeName.startsWith(options.packageName) -> {
                    DataTypeName(typeName, getTypeNameWithSuffix(typeName))
                }
                else -> {
                    DataTypeName(typeName)
                }
            }

            genericDataTypes.add(GenericDataType(dataTypeName, it.getPkg(), convertGenerics(it)))
        }

        return genericDataTypes
    }

    private fun getTypeNameWithSuffix(name: String): String {
        return identifier.toClass(name, options.modelNameSuffix)
    }
}
