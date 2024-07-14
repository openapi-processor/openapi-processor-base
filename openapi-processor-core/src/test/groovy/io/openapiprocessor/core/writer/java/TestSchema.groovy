/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.parser.Schema

/**
 * simple Schema implementation for testing
 */
class TestSchema implements Schema {
    String type
    String format

    String ref

    @Override
    List<Schema> getItems () {
        return null
    }

    @Override
    String itemsOf () {
        return null
    }

    @Override
    List<?> getEnum () {
        return enumValues
    }

    def defaultValue
    boolean deprecated = false
    boolean nullable = false
    Integer minLength
    Integer maxLength
    Integer minItems
    Integer maxItems
    BigDecimal maximum
    boolean exclusiveMaximum = false
    BigDecimal minimum
    boolean exclusiveMinimum = false
    String pattern

    Schema item
    Map<String, Schema> properties = [:]
    List<?> enumValues = []

    String description

    def getDefault() {
        defaultValue
    }

    @Override
    List<String> getRequired () {
        []
    }

    @Override
    boolean getReadOnly () {
        return false
    }

    @Override
    boolean getWriteOnly () {
        return false
    }

    @Override
    Schema getAdditionalProperties() {
        return null
    }

    @Override
    Map<String, ?> getExtensions() {
        return [:]
    }
}
