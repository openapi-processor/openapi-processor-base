/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser


class NullSchema private constructor(): Schema {
    companion object {
        val nullSchema: NullSchema = NullSchema()
    }

    override fun getType(): String? {
        return null
    }

    override fun getFormat(): String? {
        return null
    }

    override fun getRef(): String? {
        return null
    }

    override fun getItem(): Schema {
        TODO("should not be called")
    }

    override fun getProperties(): Map<String, Schema> {
        return emptyMap()
    }

    override fun getAdditionalProperties(): Schema? {
        return null
    }

    override fun getItems(): List<Schema> {
        return emptyList()
    }

    override fun itemsOf(): String? {
        return null
    }

    override fun getEnum(): List<*> {
        return emptyList<Any>()
    }

    override fun getDefault(): Any? {
        return null
    }

    override val description: String?
        get() = null

    override fun isDeprecated(): Boolean {
        return false
    }

    override fun getRequired(): List<String> {
        return emptyList()
    }

    override fun getNullable(): Boolean {
        return false
    }

    override fun getMinLength(): Int? {
        return null
    }

    override fun getMaxLength(): Int? {
        return null
    }

    override fun getMinItems(): Int? {
        return null
    }

    override fun getMaxItems(): Int? {
        return null
    }

    override fun getMaximum(): Number? {
        return null
    }

    override fun isExclusiveMaximum(): Boolean {
        return false
    }

    override fun getMinimum(): Number? {
        return null
    }

    override fun isExclusiveMinimum(): Boolean {
        return false
    }

    override val pattern: String?
        get() = null

    override val readOnly: Boolean
        get() = false

    override val writeOnly: Boolean
        get() = false

    override val extensions: Map<String, *>
        get() = emptyMap<String, Any>()
}
