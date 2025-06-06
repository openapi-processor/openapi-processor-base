/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.swagger

import io.swagger.v3.oas.models.SpecVersion
import java.net.URI
import io.openapiprocessor.core.parser.Schema as ParserSchema
import io.swagger.v3.oas.models.media.ComposedSchema as SwaggerComposedSchema
import io.swagger.v3.oas.models.media.Schema as SwaggerSchema

/**
 * Swagger Schema abstraction.
 */
class Schema(private val schema: SwaggerSchema<*>): ParserSchema {

    override fun getType(): String? {
        if (itemsOf () != null) {
            return "composed"
        }

        if (getRef() != null) {
            return null
        }

        return when (schema.specVersion) {
            SpecVersion.V30 -> schema.type
            SpecVersion.V31 -> schema.types?.first { it != "null" }
        }
    }

    override fun getFormat(): String? = schema.format

    override fun getRef(): String? = schema.`$ref`

    override fun getEnum(): List<*> {
        if (schema.enum == null) {
            return emptyList<Any>()
        }
        return schema.enum
    }

    override fun getItem(): ParserSchema {
        return Schema(schema.items)
    }

    override fun getProperties(): Map<String, ParserSchema> {
        val props = LinkedHashMap<String, ParserSchema> ()

        schema.properties?.forEach { (key: String, value: SwaggerSchema<Any>) ->
            props[key] = Schema (value)
        }

        return props
    }

    override fun getAdditionalProperties(): ParserSchema? {
        val additional = schema.additionalProperties

        // null, boolean, schema
        if(additional is SwaggerSchema<*>) {
            return Schema(additional)
        }

        return null
    }

    override fun getItems(): List<ParserSchema> {
        val result: MutableList<ParserSchema> = mutableListOf()

        if(schema !is SwaggerComposedSchema) {
            return result
        }

        schema.allOf?.forEach {
            result.add(Schema(it))
        }

        schema.anyOf?.forEach {
            result.add(Schema(it))
        }

        schema.oneOf?.forEach {
            result.add(Schema(it))
        }

        return result
    }

    override fun itemsOf(): String? {
        if(schema !is SwaggerComposedSchema) {
            return null
        }

        if (schema.allOf != null) {
            return "allOf"
        }

        if (schema.anyOf != null) {
            return "anyOf"
        }

        if (schema.oneOf != null) {
            return "oneOf"
        }

        return null
    }

    override fun getDefault(): Any? = schema.default

    override val description: String? = schema.description

    override fun isDeprecated(): Boolean = schema.deprecated ?: false

    override fun getRequired(): List<String> = schema.required ?: emptyList()

    override fun getNullable(): Boolean {
        return when (schema.specVersion) {
            SpecVersion.V30 -> schema.nullable ?: false
            SpecVersion.V31 -> schema.types?.contains("null") ?: false
        }
    }

    override fun getMinLength(): Int? = schema.minLength ?: 0

    override fun getMaxLength(): Int? = schema.maxLength

    override fun getMinItems(): Int? = schema.minItems ?: 0

    override fun getMaxItems(): Int? = schema.maxItems

    override fun getMaximum(): Number? = schema.maximum

    override fun isExclusiveMaximum(): Boolean = schema.exclusiveMaximum ?: false

    override fun getMinimum(): Number? = schema.minimum

    override fun isExclusiveMinimum(): Boolean = schema.exclusiveMinimum ?: false

    override val pattern: String?
        get() = schema.pattern

    override val readOnly: Boolean
        get() = schema.readOnly ?: false

    override val writeOnly: Boolean
        get() = schema.writeOnly ?: false

    override val extensions: Map<String, *>
        get() = schema.extensions ?: emptyMap<String, Any>()

    override val title: String?
        get() = schema.title

    override val documentUri: URI
        get() = TODO("deriving the package name from the document location is not supported with the swagger parser.")
}
