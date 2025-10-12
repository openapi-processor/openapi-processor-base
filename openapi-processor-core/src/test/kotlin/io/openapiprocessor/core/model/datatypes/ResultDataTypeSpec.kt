/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class ResultDataTypeSpec : StringSpec({

    "uses id name and type name of item" {
        val ndt = ResultDataType("Result", "wrap",
            ObjectDataType(DataTypeName("Foo", "FooX"), "pkg", linkedMapOf())
        )

        ndt.getName() shouldBe "Result<Foo>"
        ndt.getTypeName() shouldBe "Result<FooX>"
    }

    "should create import with type name" {
        forAll(row("Foo", "Foo"), row("Fooo", "FoooX")) { id, type ->
            val ndt = ResultDataType("Result", "wrap",
                ObjectDataType(DataTypeName(id, type), "pkg")
            )
            ndt.getImports() shouldBe setOf(
                "wrap.Result",
                "pkg.$type"
            )
        }
    }

    "creates result wrapper" {
        val rdt = ResultDataType("Response", "pkg",
            ObjectDataType(DataTypeName("Foo", "Foo"), "pkg", linkedMapOf())
        )

        rdt.getName() shouldBe "Response<Foo>"
        rdt.getTypeName() shouldBe "Response<Foo>"
    }

    "creates result wrapper with nested generic type" {
        val rdt = ResultDataType("OuterWrapper", "pkg",
            ObjectDataType(DataTypeName("Foo", "Foo"), "pkg", linkedMapOf()),
            listOf(
                GenericDataType(DataTypeName("Response"), "pkg")
            )
        )

        rdt.getName() shouldBe "OuterWrapper<Response<Foo>>"
        rdt.getTypeName() shouldBe "OuterWrapper<Response<Foo>>"
    }
})
