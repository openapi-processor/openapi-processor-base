/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

import io.openapiprocessor.core.model.Documentation

/**
 * OpenAPI schema collection mapped to a java type. The java type is expected to have a single
 * generic parameter.
 */
open class MappedCollectionDataType(
    private val name: String,
    private val pkg: String,
    override val item: DataType,
    override val constraints: DataTypeConstraints? = null,
    override val deprecated: Boolean = false,
    override val sourceDataType: DataType? = null,
    val multi: Boolean = false
): DataType, CollectionDataType, SourceDataType {

    override fun getName(): String {
        return "${name}<${item.getName()}>"
    }

    override fun getTypeName(): String {
        return "${name}<${item.getTypeName()}>"
    }

    override fun getSourceName(): String {
        return sourceDataType?.getName() ?: super.getSourceName()
    }

    override fun getTypeName(annotations: Set<String>, itemAnnotations: Set<String>): String {
        val sb = StringBuilder()

        if (annotations.isNotEmpty()) {
            sb.append(annotations.joinToString(" ", "", " "))
        }
        sb.append("$name<")

        if (itemAnnotations.isNotEmpty()) {
            sb.append(itemAnnotations.joinToString(" ", "", " "))
        }
        sb.append("${item.getTypeName()}>")

        return sb.toString()
    }

    override fun getPackageName(): String {
        return pkg
    }

    override fun getImports(): Set<String> {
        return setOf("${getPackageName()}.${name}") + item.getImports()
    }

    override val documentation: Documentation?
        get() = sourceDataType?.documentation
}
