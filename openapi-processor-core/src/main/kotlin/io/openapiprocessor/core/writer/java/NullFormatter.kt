/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.writer.SourceFormatter

class NullFormatter: SourceFormatter {
    override fun format(raw: String): String {
        return raw
    }
}
