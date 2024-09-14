/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.converter.mapping.MappingSchema
import io.openapiprocessor.core.model.datatypes.DataTypeConstraints
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.parser.Schema
import io.openapiprocessor.core.support.capitalizeFirstChar
import io.openapiprocessor.core.parser.RefResolver as ParserRefResolver

/**
 * Helper for [DataTypeConverter]. Holds an OpenAPI schema with context information, e.g. name and
 * if this is an inline type with a generated name.
 */
open class SchemaInfo(
    /**
     * Endpoint path.
     */
    private val endpoint: Endpoint,

    /**
     * name of the type/schema or parameter name.
     */
    private val name: String,

    /**
     * response content type.
     */
    private val contentType: String = "",

    /**
     * the OpenAPI schema
     */
    private val schema: Schema,

    /**
     * resolver of $ref'erences
     */
    private val resolver: ParserRefResolver

): MappingSchema {

    class Endpoint(val path: String, val method: HttpMethod)

    /**
     * if this is a $ref it indicates that the name of this SchemaInfo should be propagated to its
     * resolved $ref.
     *
     * if the schema is the start of a $ref-chain, its original name should be used for the resolved
     * schema.
     *
     * The swagger parser (resolve option) creates schemas for intermediate $refs where the name is
     * based on the filename. It breaks code generation because the original/public name of the
     * schema is lost.
     */
    private var refName: Boolean = false

    override fun getPath(): String {
        return endpoint.path
    }

    override fun getMethod(): HttpMethod {
        return endpoint.method
    }

    override fun getName(): String {
        return name
    }

    override fun getContentType(): String {
        return contentType
    }

    /**
     * get type of OpenAPI schema.
     *
     * @return schema type
     */
    override fun getType(): String? {
       return schema.getType()
    }

    /**
     * get type format from OpenAPI schema.
     *
     * @return schema type format
     */
    override fun getFormat(): String? {
        return schema.getFormat()
    }

    fun getTypeFormat(): String {
        var typeFormat = getType()!!
        if (getFormat() != null) {
            typeFormat += ":" + getFormat()
        }
        return typeFormat
    }

    /**
     * get $ref from OpenAPI schema.
     *
     * @return schema $ref
     */
    fun getRef(): String? {
        return schema.getRef()
    }

    /**
     * get default value.
     *
     * @return default value or null
     */
    fun getDefaultValue(): Any? {
        return schema.getDefault()
    }

    /**
     * get description.
     *
     * @return description or null
     */
    val description: String? = schema.description

    /**
     * get deprecated value
     *
     * @return true or false
     */
    fun getDeprecated(): Boolean {
        return schema.isDeprecated()
    }

    /**
     * get required value
     *
     * @return true or false
     */
    fun getRequired(): List<String> {
        return schema.getRequired()
    }

    /**
     * get nullable value.
     *
     * @return nullable, true or false
     */
    fun getNullable(): Boolean {
        return schema.getNullable()
    }

    /**
     * get minLength value.
     *
     * @return minLength value >= 0
     */
    fun getMinLength(): Int {
        return schema.getMinLength() ?: 0
    }

    /**
     * get maxLength value.
     *
     * @return maxLength value or null
     */
    fun getMaxLength(): Int? {
        return schema.getMaxLength()
    }

    /**
     * get minItems value.
     *
     * @return minItems value or null
     */
    fun getMinItems(): Int? {
        return schema.getMinItems()
    }

    /**
     * get maxItems value.
     *
     * @return maxItems value or null
     */
    fun getMaxItems(): Int? {
        return schema.getMaxItems()
    }

    /**
     * get maximum value.
     *
     * @return maximum value or null
     */
    fun getMaximum(): Number? {
        return schema.getMaximum()
    }

    /**
     * maximum is exclusiveMaximum value.
     *
     * @return true or false
     */
    fun getExclusiveMaximum(): Boolean {
        return schema.isExclusiveMaximum()
    }

    /**
     * get minimum value.
     *
     * @return minimum value or null
     */
    fun getMinimum(): Number? {
        return schema.getMinimum()
    }

    /**
     * minimum is exclusiveMinimum.
     *
     * @return exclusiveMinimum value or null
     */
    fun getExclusiveMinimum(): Boolean {
        return schema.isExclusiveMinimum()
    }

    val pattern: String?
        get() = schema.pattern

    val readOnly: Boolean
        get() = schema.readOnly

    val writeOnly: Boolean
        get() = schema.writeOnly

    /**
     * iterate over properties
     */
    fun eachProperty(action: (name: String, info: SchemaInfo) -> Unit) {
        schema.getProperties().forEach { (name, schema) ->
            action(name, buildForNestedType(name, schema))
        }
    }

    /**
     * iterate over composed items
     */
    fun eachItemOf(action: (info: SchemaInfo) -> Unit) {
        if (schema.getProperties().isNotEmpty()) {
            action(SchemaInfoAllOf(
                endpoint = endpoint,
                name = "${name}_${itemOf()!!.capitalizeFirstChar()}",
                schema = schema,
                resolver = resolver
            ))
        }

        schema.getItems().forEachIndexed { index, schema ->
            action(SchemaInfo(
                endpoint = endpoint,
                name = "${name}_${itemOf()!!.capitalizeFirstChar()}_${index}",
                schema = schema,
                resolver = resolver
            ))
        }
    }

    /**
     * allOf, oneOf, anyOf.
     */
    fun itemOf(): String? {
        return schema.itemsOf()
    }

    fun getExtensions(): Map<String, *> {
        return schema.extensions
    }

    /**
     * Factory method to create a {@link SchemaInfo} of the $ref'erenced schema.
     *
     * @return a new {@link SchemaInfo}
     */
    fun buildForRef(): SchemaInfo {
        val resolved = resolver.resolve(schema)
        val resolvedName = if (refName || resolved.hasNoName) {
            name // propagate "parent" name
        } else {
            resolved.name
        }!!

        val info = SchemaInfo(
            endpoint = endpoint,
            name = resolvedName,
            schema = resolved.schema,
            resolver = resolver)

        info.refName = true

        return info
    }

    /**
     * Factory method to create an inline {@link SchemaInfo} with (property) name and (property)
     * schema.
     *
     * @param nestedName the property name
     * @param nestedSchema the property schema
     * @return a new {@link SchemaInfo}
     */
    private fun buildForNestedType(nestedName: String, nestedSchema: Schema): SchemaInfo {
        return SchemaInfo(
            endpoint = endpoint,
            name = getNestedTypeName(nestedName),
            schema = nestedSchema,
            resolver = resolver)
    }

    /**
     * Factory method to create an {@link SchemaInfo} of the item type of array schema.
     *
     * @return a new {@link SchemaInfo}
     */
    fun buildForItem(): SchemaInfo {
        val item = schema.getItem()

        return SchemaInfo(
            endpoint = endpoint,
            name = getArrayItemName(),
            schema = item,
            resolver = resolver
        )
    }

    /**
     * Factory method to create an {@link SchemaInfo} of additionalProperties.
     *
     * @return a new {@link SchemaInfo}
     */
    fun buildForAdditionalProperties(): SchemaInfo? {
        val additionalProperties = schema.getAdditionalProperties() ?: return null

        return SchemaInfo(
            endpoint = endpoint,
            name = getNestedTypeName("additionalProperties"),
            schema = additionalProperties,
            resolver = resolver)
    }

    /**
     * all constraints
     */
    @Suppress("UNCHECKED_CAST")
    val constraints: DataTypeConstraints
        get() = DataTypeConstraints(
            defaultValue = getDefaultValue(),
            nullable = getNullable(),
            minLength = getMinLength(),
            maxLength = getMaxLength(),
            minimum = getMinimum(),
            exclusiveMinimum = getExclusiveMinimum(),
            maximum = getMaximum(),
            exclusiveMaximum = getExclusiveMaximum(),
            pattern = pattern,
            format = getFormat(),
            values = getEnumValues() as List<String>
        )

    override fun isPrimitive(): Boolean {
        return listOf("boolean", "integer", "number", "string").contains(schema.getType())
    }

    override fun isArray(): Boolean {
        return getType().equals("array")
    }

    fun isObject(): Boolean {
        if (getType().equals("object"))
            return true

        val properties = schema.getProperties()
        return properties.isNotEmpty()
    }

    fun isComposedObject(): Boolean {
        return getType().equals("composed")
    }

    fun isComposedAllOf(): Boolean {
        return itemOf().equals("allOf")
    }

    fun isComposedOneOf(): Boolean {
        return itemOf().equals("oneOf")
    }

    fun isTypeLess(): Boolean {
        return schema.getType() == null
    }

    fun isRefObject(): Boolean {
        return schema.getRef() != null
    }

    fun isEnum(): Boolean {
        return schema.getEnum().isNotEmpty()
    }

    fun getEnumValues(): List<*> {
        return schema.getEnum()
    }

    private fun getArrayItemName(): String {
        return name + "ArrayItem"
    }

    private fun getNestedTypeName(nestedName: String): String {
        return name + nestedName.capitalizeFirstChar()
    }
}
