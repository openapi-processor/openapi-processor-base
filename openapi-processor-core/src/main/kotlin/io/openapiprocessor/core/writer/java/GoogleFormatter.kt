/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import com.google.googlejavaformat.java.Formatter
import com.google.googlejavaformat.java.JavaFormatterOptions
import io.openapiprocessor.core.writer.SourceFormatter

class GoogleFormatter: SourceFormatter {
    private lateinit var formatter: Formatter

    init {
        initFormatter()
    }

    override fun format(raw: String): String {
        try {
            return correctLineFeed(formatter.formatSource(raw))
        } catch (e: Exception) {
            throw FormattingException(raw, e)
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
