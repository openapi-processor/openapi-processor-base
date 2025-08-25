/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser

import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class RefNameSpec : FreeSpec({

    data class TestCase(val ref: String, val expected: String)

    withData(
        nameFn = { "${it.ref} -> ${it.expected}" },
        listOf(
            TestCase("foo.yaml#/Foo", "Foo"),
            TestCase("components/schemas/foo.yaml#/Foo", "Foo"),
            TestCase("Foo.yaml", "Foo"),
            TestCase("components/schemas/Foo.yaml", "Foo"),
            TestCase("Foo.yml", "Foo"),
            TestCase("components/schemas/Foo.yml", "Foo"),
            TestCase("Foo", "Foo"),
            TestCase("components/schemas/Foo", "Foo")
        )
    ) { (ref, expected) ->
        getRefName(ref) shouldBe expected
    }
})
