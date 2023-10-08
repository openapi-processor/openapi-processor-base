/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.mockk
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.parser.RefResolver
import io.openapiprocessor.core.support.Schema

class SchemaInfoSpec : StringSpec({

    "recognizes object by type" {
        val info = SchemaInfo(
            SchemaInfo.Endpoint("/any", HttpMethod.GET),
            "any",
            "",
            Schema(schemaType = "object"),
            mockk<RefResolver>()
        )

        info.isObject().shouldBeTrue()
    }

    "recognizes object without type but properties" {
        val info = SchemaInfo(
            SchemaInfo.Endpoint("/any", HttpMethod.GET),
            "any",
            "",
            Schema(schemaType = null, schemaProperties = mapOf(
                "foo" to Schema()
            )),
            mockk<RefResolver>()
        )

        info.isObject().shouldBeTrue()
    }
})
