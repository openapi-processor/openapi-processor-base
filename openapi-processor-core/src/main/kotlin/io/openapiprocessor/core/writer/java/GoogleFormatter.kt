/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import com.google.googlejavaformat.java.Formatter
import com.google.googlejavaformat.java.JavaFormatterOptions
import io.openapiprocessor.core.writer.SourceFormatter

private const val addExportsLink: String = "https://openapiprocessor.io/oap/home/jdk.html"

open class GoogleFormatter: SourceFormatter {
    protected lateinit var formatter: Formatter

    init {
        initFormatter()
    }

    override fun format(raw: String): String {
        try {
            return correctLineFeed(formatter.formatSource(raw))

        } catch (e: IllegalAccessError) {
            // since java 16 the jdk.compiler module does not export com.sun.tools.javac.parser
            throw FormattingException("looks like you may need $addExportsLink to make formatting work.", e)

        } catch (e: Exception) {
            throw FormattingException("failed to format the generated source: \n>>\n$raw\n<<", e)
        }
    }

    // put line feed before last closing }
    private fun correctLineFeed(formatted: String): String {
        val index = formatted.lastIndexOf("}")

        return StringBuilder()
            .append(formatted.substring(0, index))
            .append("\n}\n")
            .toString()
    }

    private fun initFormatter() {
        formatter = Formatter(
            JavaFormatterOptions
                .builder()
                .style(JavaFormatterOptions.Style.GOOGLE)
                .build()
        )
    }
}
