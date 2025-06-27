/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

/**
 * backward compatibility
 */
data class Compatibility(
    /**
     * add bean validation @Valid annotation on the reactive type and not on the wrapped type.
     */
    val beanValidationValidOnReactive: Boolean = true,

    /**
     * split identifier when switching from digits to letters.
     */
    val identifierWordBreakFromDigitToLetter: Boolean = true,

    /**
     * prefix enum identifier if it starts with an invalid character.
     */
    val identifierPrefixInvalidEnumStart: Boolean = true
)
