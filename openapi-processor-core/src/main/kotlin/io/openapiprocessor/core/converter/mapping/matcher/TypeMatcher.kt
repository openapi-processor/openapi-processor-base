/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.matcher

import io.openapiprocessor.core.converter.mapping.*
import io.openapiprocessor.core.converter.mapping.steps.MappingStep
import io.openapiprocessor.core.converter.mapping.steps.MatcherStep
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * [io.openapiprocessor.core.converter.MappingFinder] matcher for type mappings.
 */
class TypeMatcher(private val query: MappingQuery): MappingMatcher, (TypeMapping) -> Boolean {
    val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    @Deprecated("replace", replaceWith = ReplaceWith("match(mapping, step)"))
    override fun match(mapping: Mapping): Boolean {
        if (mapping !is TypeMapping) {
            log.trace("not matched: {}", mapping)
            return false
        }

        val match = this.invoke(mapping)
        log.trace("${if (match) "" else "not "}matched: {}", mapping)
        return match
    }

    override fun match(mapping: Mapping, step: MappingStep): Boolean {
        if (mapping !is TypeMapping) {
            step.add(MatcherStep(mapping, false))
            return false
        }

        val match = this.invoke(mapping)
        step.add(MatcherStep(mapping, match))
        return match
    }

    override fun invoke(mapping: TypeMapping): Boolean {
        // try to match by OpenAPI name first, type is the OpenAPI type, i.e. string, object etc.

        // the format must match to avoid matching primitive and primitive with format, e.g.
        // string should not match string:binary
        if (matchesName(mapping) && matchesFormat(mapping)) {
            return true
        }

        return when {
            query.primitive -> {
                matchesType(mapping) && matchesFormat(mapping)
            }
            query.array -> {
                matchesArray(mapping)
            }
            else -> {
                false // nop
            }
        }
    }

    private fun matchesName(m: TypeMapping): Boolean = m.sourceTypeName == query.name
    private fun matchesFormat(m: TypeMapping): Boolean = m.sourceTypeFormat == query.format
    private fun matchesType(m: TypeMapping): Boolean = m.sourceTypeName == query.type
    private fun matchesArray(m: TypeMapping): Boolean = m.sourceTypeName == "array"
}
