/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.openapiprocessor.core.converter.SchemaInfo
import io.openapiprocessor.core.model.DataTypes
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.parser.NullSchema
import io.openapiprocessor.core.parser.RefResolver

class LazyDataTypeSpec : StringSpec({
    val resolver = mockk<RefResolver>()
    val any = SchemaInfo.Endpoint("/any", HttpMethod.GET)

    "uses id name and type name of item" {
        val dataTypes = DataTypes()
        dataTypes.add(
            ObjectDataType(DataTypeName("Foo", "FooX"), "pkg", linkedMapOf())
        )

        val info = SchemaInfo(any, "Foo", "", NullSchema, resolver)
        val dt = dataTypes.lazy(info.getName())

        dt.getName() shouldBe "Foo"
        dt.getTypeName() shouldBe "FooX"
    }

    "should create import with type name" {
        forAll(row("Foo", "Foo"), row("Fooo", "FoooX")) { id, type ->

            val dataTypes = DataTypes()
            dataTypes.add(
                ObjectDataType(DataTypeName(id, type), "pkg", linkedMapOf())
            )

            val info = SchemaInfo(any, id, "", NullSchema, resolver)
            val dt = dataTypes.lazy(info.getName())

            dt.getImports() shouldBe setOf(
                "pkg.$type"
            )
        }
    }

})
