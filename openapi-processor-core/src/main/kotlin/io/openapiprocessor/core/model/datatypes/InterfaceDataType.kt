/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

import io.openapiprocessor.core.model.Documentation

/**
 * represents an OpenAPI object that is generated as interface.
 *
 * only used with onOf elements which are all objects. It will represent the common interface that
 * all items implement.
 */
class InterfaceDataType(
    private val name: DataTypeName,
    private val pkg: String,
    items: List<DataType> = emptyList(),
    override val constraints: DataTypeConstraints? = null,
    override val deprecated: Boolean = false,
    override val documentation: Documentation? = null
): DataType {
    private val _items: MutableCollection<DataType> = mutableListOf()

    init {
        _items.addAll(items)
    }

    val items: Collection<DataType>
        get() = _items.toList()

    fun addItem(item: DataType) {
        _items.add(item)
    }

    override fun getName(): String {
        return name.id
    }

    override fun getTypeName(): String {
        return name.type
    }

    override fun getPackageName(): String {
        return pkg
    }

    override fun getImports(): Set<String> {
        return setOf("${getPackageName()}.${getTypeName()}")
    }
}
