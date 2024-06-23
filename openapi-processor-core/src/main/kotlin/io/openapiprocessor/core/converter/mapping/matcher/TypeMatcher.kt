/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.matcher

import io.openapiprocessor.core.converter.mapping.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * [io.openapiprocessor.core.converter.mapping.MappingFinder] matcher for type mappings.
 */
class TypeMatcher(private val schema: MappingSchema): MappingMatcher, (TypeMapping) -> Boolean {
    val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun match(mapping: Mapping): Boolean {
        if (mapping !is TypeMapping) {
            log.trace("not matched: {}", mapping)
            return false
        }

        val match = this.invoke(mapping)
        log.trace("${if (match) "" else "not "}matched: {}", mapping)
        return match
    }

    override fun toString(): String {
        return schema.toStringSchema()
    }

    override fun invoke(mapping: TypeMapping): Boolean {
        // try to match by name first
        // the format must match to avoid matching primitive and primitive with format, e.g.
        // string should not match string:binary
        if (matchesName(mapping) && matchesFormat(mapping)) {
            return true
        }

        return when {
            schema.isPrimitive() -> {
                matchesType(mapping) && matchesFormat(mapping)
            }
            schema.isArray() -> {
                matchesArray(mapping)
            }
            else -> {
                false // nop
            }
        }
    }

    private fun matchesName(m: TypeMapping): Boolean = m.sourceTypeName == schema.getName()
    private fun matchesFormat(m: TypeMapping): Boolean = m.sourceTypeFormat == schema.getFormat()
    private fun matchesType(m: TypeMapping): Boolean = m.sourceTypeName == schema.getType()
    private fun matchesArray(m: TypeMapping): Boolean = m.sourceTypeName == "array"
}
