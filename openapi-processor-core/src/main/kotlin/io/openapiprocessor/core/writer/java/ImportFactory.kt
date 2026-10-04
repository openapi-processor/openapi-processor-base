/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */
package io.openapiprocessor.core.writer.java

import java.io.StringWriter

class ImportFactory {

    fun create(currentPackage: String, imports: Set<String>): String {
        val content = StringWriter()

        DefaultImportFilter()
            .filter(currentPackage, imports)
            .sorted()
            .forEach {
                content.write("import ${it};\n")
            }

        return content.toString()
    }
}


fun replaceImportsPlaceholder(target: String, imports: String): String {
    return target.replace("<IMPORTS>\n", imports)
}
