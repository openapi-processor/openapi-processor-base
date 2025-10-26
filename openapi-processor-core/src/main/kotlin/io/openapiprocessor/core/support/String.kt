/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

val String.Companion.Empty: String
    get() = ""

fun String.capitalizeFirstChar() = this.replaceFirstChar {
    it.uppercase()
}

fun String.indent(): String = prependIndent("    ")

const val LF =  "\n"
