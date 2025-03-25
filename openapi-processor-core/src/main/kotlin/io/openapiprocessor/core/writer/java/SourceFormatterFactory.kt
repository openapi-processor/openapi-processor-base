/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.writer.SourceFormatter

class SourceFormatterFactory {

    fun getFormatter(options: ApiOptions): SourceFormatter {
        return if (options.formatCode) {
            when (options.formatCodeFormatter) {
                "google" -> GoogleFormatter()
                "eclipse" -> EclipseFormatter()
                else -> GoogleFormatter()
            }
        } else {
            GoogleFormatter()
        }
    }
}
