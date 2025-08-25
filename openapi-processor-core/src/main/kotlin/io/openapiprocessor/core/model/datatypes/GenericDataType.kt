/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

class GenericDataType(
    private val name: DataTypeName,
    private val pkg: String,
    val generics: List<GenericDataType> = emptyList()
): DataType {
    override fun getName(): String {
        return if (generics.isEmpty()) {
            name.id
        } else {
            val genericTypes = generics.map { it.name.id }
            "${name.id}<${genericTypes.joinToString()}>"
        }
    }

    override fun getTypeName(): String {
        return if (generics.isEmpty()) {
            name.type
        } else {
            val genericTypes = generics.map { it.getTypeName() }
            "${name.type}<${genericTypes.joinToString()}>"
        }
    }

    override val rawTypeName: String
        get() = name.type

    override fun getPackageName(): String {
        return pkg
    }

    override fun getImports(): Set<String> {
        if (name.id == "?") {
            return emptySet()
        }

        val genericImports = generics
            .map { it.getImports() }
            .flatten()

        return setOf("${getPackageName()}.${name.type}") + genericImports
    }
}
