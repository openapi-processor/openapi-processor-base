/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */
package io.openapiprocessor.core.writer.java


data class IdentifierOptions(
    /**
     * recognize switch from digits to letters as word break.
     */
    val wordBreakFromDigitToLetter: Boolean = true,

    /**
     * prefix OpenAPI enum value if it starts with an invalid start character.
     */
    val prefixInvalidEnumStart: Boolean = true
)
