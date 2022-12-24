/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

/**
 * OpenAPI schema mapped to a java type.
 */
open class MappedDataType(
//    private val name: DataTypeName
    private val type: String,
    private val pkg: String,
    val genericTypes: List<DataTypeName> = emptyList(),
    override val constraints: DataTypeConstraints? = null,
    override val deprecated: Boolean = false,
    @Deprecated(message = "obsolete/unused")
    val simpleDataType: Boolean = false,
    override val sourceDataType: DataType? = null
): DataType, MappedSourceDataType {

    override fun getName(): String {
        return if (genericTypes.isEmpty()) {
            type
        } else {
            "${type}<${genericIds.joinToString(", ")}>"
        }
    }

    override fun getTypeName(): String {
        return if (genericTypes.isEmpty()) {
            type
        } else {
            "${type}<${genericTypeNames.joinToString(", ")}>"
        }
    }

    override fun getSourceName(): String {
        return sourceDataType?.getName() ?: super.getSourceName()
    }

    override fun getPackageName(): String {
        return pkg
    }

    override fun getImports(): Set<String> {
        return setOf("${getPackageName()}.$type") + genericImports
    }

    private val genericImports: Set<String>
        get() {
            return genericTypes
                .map { it.type }
                .filter { it != "?" }
                .toSet()
        }

    private val genericIds: List<String>
        get() {
            return genericTypes.map {
                getClassName(it.id)
            }
        }

    private val genericTypeNames: List<String>
        get() {
            return genericTypes.map {
                getClassName(it.type)
            }
        }

    private fun getClassName(source: String): String {
        return source.substringAfterLast('.')
    }

}
