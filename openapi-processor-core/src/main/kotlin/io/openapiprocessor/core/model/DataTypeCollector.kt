/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model

import io.openapiprocessor.core.model.datatypes.*

/**
 * ref counts used model data types honoring data type mappings to generate only required (used)
 * classes.
 */
class DataTypeCollector(
    private val dataTypes: DataTypes, private val generatedPackageName: String) {

    fun collect(dataType: DataType) {
        when (dataType) {
            is ArrayDataType -> {
                collect(dataType.item)
            }
            is MappedCollectionDataType -> {
                collect(dataType.item)
            }
            is ObjectDataType -> {
                dataTypes.addRef(dataType.getName())
                dataType.forEach { _, propDataType ->
                    collect(propDataType)
                }
            }
            is MappedDataType -> {
                dataType.genericTypes
                    .forEach {
                        collect(it)
                    }
            }
            is GenericDataType -> {
                val name = dataType.getName()
                val found = dataTypes.find(name)
                val generated = dataType.getPackageName().startsWith(generatedPackageName)
                if (generated && found != null) {
                    dataTypes.addRef(name)
                }

                dataType.generics.forEach {
                    collect(it)
                }
            }
            is AllOfObjectDataType -> {
                dataTypes.addRef(dataType.getName())
                dataType.forEach { _, propDataType ->
                    collect(propDataType)
                }
            }
            is AnyOneOfObjectDataType -> {
                dataType.forEach { ofDataType ->
                    collect(ofDataType)
                }
            }
            is StringEnumDataType -> {
                dataTypes.addRef(dataType.getName())
            }
            is PropertyDataType -> {
                collect(dataType.dataType)
            }
            is InterfaceDataType -> {
                dataTypes.addRef(dataType.getName())
                dataType.items.forEach {
                    collect(it)
                }
            }
        }
    }

}
