/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

class QualifiedType(private val qualifiedType: String) {
    val import: String?
    val type: String

    init {
        import = extractImport()
        type = extractType()
    }

    private fun extractImport(): String? {
        val parts = qualifiedType.split(".")
        val index = parts.indexOfFirst { it.isNotEmpty() && it[0].isUpperCase() }
        if (index <= 0) {
            return null
        }

        return parts.subList(0, index + 1).joinToString(".")
    }

    private fun extractType(): String {
        val imp = import ?: return qualifiedType
        val last = imp.lastIndexOf(".")
        return qualifiedType.substring(last + 1)
    }

    override fun toString(): String {
        return qualifiedType
    }
}
