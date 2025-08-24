/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.datatypes.*
import io.openapiprocessor.core.support.datatypes.ObjectDataType
import io.openapiprocessor.core.support.datatypes.propertyDataTypeString

class BeanValidationMappingSpec: StringSpec({
    val apiOptions = ApiOptions()
    val validation = BeanValidationFactory(apiOptions)
    val validations = validation.validations

    "applies @Valid to mapped object items" {
        val dataType = MappedDataType(
            "Foo", "pkg",
            sourceDataType = ObjectDataType(
                "Bar", "pkg", linkedMapOf("foo" to propertyDataTypeString())
            )
        )
        val info = validation.validate(dataType)

        val prop = info.prop
        prop.dataTypeValue shouldBe "Foo"
        prop.imports shouldBe setOf(validations.VALID)
        prop.annotations shouldBe listOf("@Valid")

        val io = info.inout
        io.dataTypeValue shouldBe "@Valid Foo"
        io.imports shouldBe setOf(validations.VALID)
        io.annotations.shouldBeEmpty()
    }

    "applies @Valid to mapped collection with object items" {
        val dataType = MappedCollectionDataType(
            "List", "pkg",
            ObjectDataType(
                "Foo", "pkg", linkedMapOf("foo" to propertyDataTypeString())
            ),
            sourceDataType = ArrayDataType(
                ObjectDataType(
                    "Foo", "pkg", linkedMapOf("foo" to propertyDataTypeString())
                )
            )
        )
        val info = validation.validate(dataType)

        val prop = info.prop
        prop.dataTypeValue shouldBe "List<@Valid Foo>"
        prop.imports shouldBe setOf(validations.VALID)
        prop.annotations.shouldBeEmpty()

        val io = info.inout
        io.dataTypeValue shouldBe "List<@Valid Foo>"
        io.imports shouldBe setOf(validations.VALID)
        io.annotations.shouldBeEmpty()
    }

    "does not apply @Valid to mapped collection with simple items" {
        val dataType = MappedCollectionDataType(
            "List", "pkg", StringDataType(),
            sourceDataType = ArrayDataType(StringDataType())
        )
        val info = validation.validate(dataType)

        val prop = info.prop
        prop.dataTypeValue shouldBe "List<String>"
        prop.imports.shouldBeEmpty()
        prop.annotations.shouldBeEmpty()

        val io = info.inout
        io.dataTypeValue shouldBe "List<String>"
        io.imports.shouldBeEmpty()
        io.annotations.shouldBeEmpty()
    }

    "does not apply @Valid to mapped collection" {
        val dataType = MappedCollectionDataType(
            "List", "pkg",
            ObjectDataType(
                "Foo", "pkg", linkedMapOf("foo" to propertyDataTypeString())
            ),
            sourceDataType = ArrayDataType(
                ObjectDataType(
                    "Foo", "pkg", linkedMapOf("foo" to propertyDataTypeString())
                )
            )
        )

        val info = validation.validate(dataType)

        val prop = info.prop
        prop.dataTypeValue shouldBe "List<@Valid Foo>"
        prop.imports shouldBe setOf(validations.VALID)
        prop.annotations.shouldBeEmpty()

        val io = info.inout
        io.dataTypeValue shouldBe "List<@Valid Foo>"
        io.imports shouldBe setOf(validations.VALID)
        io.annotations.shouldBeEmpty()
    }

    "does not add @Valid to data type without source type (additional parameter)" {
        val dataType = MappedDataType(
            "Foo",
            "foo",
            emptyList(),
            null,
            false,
            null
        )

        val info = validation.validate(dataType, false)

        val prop = info.prop
        prop.dataTypeValue shouldBe "Foo"
        prop.imports.shouldBeEmpty()
        prop.annotations.shouldBeEmpty()

        val io = info.inout
        io.dataTypeValue shouldBe "Foo"
        io.imports.shouldBeEmpty()
        io.annotations.shouldBeEmpty()
    }
})
