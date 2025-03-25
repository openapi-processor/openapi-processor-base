/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.writer.SourceFormatter
import org.eclipse.jdt.core.formatter.CodeFormatter
import org.eclipse.jdt.core.ToolFactory
import org.eclipse.jface.text.Document
import java.io.StringReader
import java.util.*
import java.util.stream.Collectors


class EclipseFormatter: SourceFormatter {
    private lateinit var formatter: CodeFormatter

    init {
        initFormatter()
    }

    override fun format(raw: String): String {
        try {
            val textEdit = formatter.format(
                CodeFormatter.K_COMPILATION_UNIT + CodeFormatter.F_INCLUDE_COMMENTS,
                raw,
                0,
                raw.length,
                0,
                "\n"
            )

            val document = Document(raw)
            textEdit.apply(document)

            return correctEndOfFile(document.get())
        } catch (e: Exception) {
            throw FormattingException("failed to format the generated source: \n>>\n$raw\n<<", e)
        }
    }

    private fun correctEndOfFile(formatted: String): String {
        return StringBuilder()
            .append(formatted)
            .append("\n")
            .toString()
    }

    private fun initFormatter() {
        formatter = ToolFactory.createCodeFormatter(convertOptions(loadStyleOptions()))
    }

    private fun loadStyleOptions(): Properties {
        val content = this.javaClass.getResource("/formatter.properties")!!.readText()
        val reader = StringReader(content)
        val properties = Properties()
        properties.load(reader)
        return properties
    }

    private fun convertOptions(prop: Properties): HashMap<String, String> {
        return prop.entries
            .stream()
            .collect(
                Collectors.toMap(
                    { e -> e.toString() },
                    { v -> v.toString() },
                    { _, next -> next })
                { HashMap() }
            )
    }
}
