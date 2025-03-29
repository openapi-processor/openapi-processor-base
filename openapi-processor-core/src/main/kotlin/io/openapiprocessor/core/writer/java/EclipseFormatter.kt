/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.writer.SourceFormatter
import org.eclipse.jdt.core.ToolFactory
import org.eclipse.jdt.core.formatter.CodeFormatter
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants
import org.eclipse.jface.text.Document
import java.io.StringReader
import java.util.*


private const val LINE_SEPARATOR = "\n"

class EclipseFormatter : SourceFormatter {
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
                LINE_SEPARATOR
            )

            val document = Document(raw)
            textEdit.apply(document)

            val text =  document.get()
            val trimmed = trimEnd(text)
            return trimmed

        } catch (e: Exception) {
            throw FormattingException("failed to format the generated source: \n>>\n$raw\n<<", e)
        }
    }

    fun trimEnd(source: String): String {
        return source.lines()
            .joinToString(LINE_SEPARATOR) {
                it.trimEnd()
            }
    }

    private fun initFormatter() {
        formatter = ToolFactory.createCodeFormatter(loadStyleOptions())
    }

    private fun loadStyleOptions(): Map<String, String> {
        val conventions = DefaultCodeFormatterConstants.getJavaConventionsSettings()
        val content = this.javaClass.getResource("/formatter.properties")!!.readText()
        val reader = StringReader(content)
        val properties = Properties()
        properties.load(reader)

        @Suppress("UNCHECKED_CAST")
        conventions.putAll(properties as Map<out String, String>)

        return conventions
    }
}
