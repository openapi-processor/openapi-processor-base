/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.openapiprocessor.core.processor.MappingReader

class MappingReaderAnnotationTargetsSpec: StringSpec ({
    isolationMode = IsolationMode.InstancePerTest

    val reader = MappingReader()

    "read annotation target types" {
        val yaml = """
           |openapi-processor-mapping: v15
           |annotation-targets:
           |  lombok.Builder: ["type", "method"]
           |
           """.trimMargin()

        val mapping = reader.read(yaml) as Mapping
        val targets = mapping.annotationTargets

        // then:
        targets["lombok.Builder"].shouldContainExactly(setOf("type", "method"))
    }
})
