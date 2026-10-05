/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */
package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.support.LF
import java.io.StringWriter
import java.io.Writer

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

fun replaceImportsPlaceholder(target: Writer, imports: String): String {
    return target.toString().replace("<IMPORTS>\n", imports)
}

/** migration helper */
fun extractImports(content: Writer): Pair<String, List<String>> {
    val (lines, imports) = content.toString().lines().partition { line ->
        !line.startsWith("import ")
    }

    val packages = imports.map {
        it.substringAfter("import ").substringBefore(";")
    }

    return Pair(collapseEmptyLines(lines).joinToString(LF), packages)
}

private fun collapseEmptyLines(lines: List<String>): List<String> {
     val result = mutableListOf<String>()

     var lastLineIsEmpty = false
     for (line in lines) {
         if (line.isNotEmpty()) {
             result.add(line)
             lastLineIsEmpty = false
         } else {
             if (!lastLineIsEmpty) {
                 result.add(line)
                 lastLineIsEmpty = true
             }
         }
     }
     return result
 }