/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

/**
 * represents a code snippet, i.e. text to insert when generating the code and te required imports.
 * this could be a single annotation with its import or parameter with all its annotations and imports.
 */
data class CodeSnippet(val text: String, val imports: Set<String>)
