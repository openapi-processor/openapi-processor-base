/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.parser.Schema
import java.net.URI
import io.openapiprocessor.core.parser.Schema as ParserSchema

/**
 * simple Schema implementation for testing
 */

class Schema(
    private val schemaType: String? = null,
    private val schemaProperties: Map<String, Schema> = mapOf()

) : ParserSchema {

    override fun getType(): String? {
        return schemaType
    }

    override fun getFormat(): String? {
        TODO("Not yet implemented")
    }

    override fun getRef(): String? {
        TODO("Not yet implemented")
    }

    override fun getItem(): Schema {
        TODO("Not yet implemented")
    }

    override fun getProperties(): Map<String, Schema> {
        return schemaProperties
    }

    override fun getAdditionalProperties(): Schema? {
        TODO("Not yet implemented")
    }

    override fun getItems(): List<Schema> {
        TODO("Not yet implemented")
    }

    override fun itemsOf(): String? {
        TODO("Not yet implemented")
    }

    override fun getEnum(): List<*> {
        TODO("Not yet implemented")
    }

    override fun getDefault(): Any? {
        TODO("Not yet implemented")
    }

    override val description: String?
        get() = null

    override fun isDeprecated(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getRequired(): List<String> {
        TODO("Not yet implemented")
    }

    override fun getNullable(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getMinLength(): Int? {
        TODO("Not yet implemented")
    }

    override fun getMaxLength(): Int? {
        TODO("Not yet implemented")
    }

    override fun getMinItems(): Int? {
        TODO("Not yet implemented")
    }

    override fun getMaxItems(): Int? {
        TODO("Not yet implemented")
    }

    override fun getMaximum(): Number? {
        TODO("Not yet implemented")
    }

    override fun isExclusiveMaximum(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getMinimum(): Number? {
        TODO("Not yet implemented")
    }

    override fun isExclusiveMinimum(): Boolean {
        TODO("Not yet implemented")
    }

    override val pattern: String?
        get() = TODO("Not yet implemented")
    override val readOnly: Boolean
        get() = TODO("Not yet implemented")
    override val writeOnly: Boolean
        get() = TODO("Not yet implemented")

    override val extensions: Map<String, *>
        get() = TODO("Not yet implemented")

    override val title: String?
        get() = TODO("Not yet implemented")

    override val documentUri: URI
        get() = TODO("Not yet implemented")
}
