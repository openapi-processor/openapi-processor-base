/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.openapiprocessor.core.processor.MappingReader

class MappingReaderBeanValidationSpec: StringSpec ({
    isolationMode = IsolationMode.InstancePerTest

    val reader = MappingReader()

    "read additional supported bean-validation types" {
        val yaml = """
           |openapi-processor-mapping: v14
           |bean-validation:
           |  javax.validation.constraints.Size:
           |   - org.openapitools.jackson.nullable.JsonNullable
           |
           """.trimMargin()

        val mapping = reader.read(yaml) as Mapping
        val validation = mapping.beanValidation

        // then:
        validation["javax.validation.constraints.Size"].shouldContainExactly(setOf(
            "org.openapitools.jackson.nullable.JsonNullable"
        ))
    }
})
