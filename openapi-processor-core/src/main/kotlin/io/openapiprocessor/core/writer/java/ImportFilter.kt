/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */
package io.openapiprocessor.core.writer.java

/**
 * Remove imports from the list of import that are not needed.
 */
fun interface ImportFilter {
    fun filter(currentPackageName: String, imports: Set<String>): Set<String>
}
