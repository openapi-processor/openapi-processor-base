/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.model.datatypes.*
import io.openapiprocessor.core.support.datatypes.ListDataType
import io.openapiprocessor.core.support.datatypes.ObjectDataType
import io.openapiprocessor.core.support.datatypes.propertyDataType
import io.openapiprocessor.core.support.datatypes.propertyDataTypeString

class BeanValidationFactorySpec : StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val validation = BeanValidationFactory()
    val validations = validation.validations

    "applies @Valid to 'array' with object items" {
        val dataType = ArrayDataType(
            ObjectDataType(
                "Foo", "pkg", linkedMapOf(
                    "foo" to propertyDataTypeString()
                )
            )
        )
        val info = validation.validate(dataType)

        val prop = info.prop
        prop.dataTypeValue shouldBe "Foo[]"
        prop.imports shouldBe setOf(validations.VALID)
        prop.annotations shouldBe setOf("@Valid")

        val io = info.inout
        io.dataTypeValue shouldBe "@Valid Foo[]"
        io.imports shouldBe setOf(validations.VALID)
        io.annotations.shouldBeEmpty()
    }

    "does not apply @Valid to 'array' with simple items" {
        val dataType = ArrayDataType(StringDataType())
        val info = validation.validate(dataType)

        val prop = info.prop
        prop.dataTypeValue shouldBe "String[]"
        prop.imports.shouldBeEmpty()
        prop.annotations.shouldBeEmpty()

        val io = info.inout
        io.dataTypeValue shouldBe "String[]"
        io.imports.shouldBeEmpty()
        io.annotations.shouldBeEmpty()
    }

    "applies @Pattern to String" {
        val dataType = StringDataType(constraints = DataTypeConstraints(pattern = "regex"))
        val info = validation.validate(dataType)
        info.annotations.size shouldBe 1

        val prop = info.prop
        prop.dataTypeValue shouldBe "String"
        prop.imports shouldBe setOf(validations.PATTERN)
        prop.annotations shouldBe listOf("""@Pattern(regexp = "regex")""")

        val io = info.inout
        io.dataTypeValue shouldBe """@Pattern(regexp = "regex") String"""
        io.imports shouldBe setOf(validations.PATTERN)
        io.annotations.shouldBeEmpty()
    }

    "applies @Pattern to String with escaping" {
        val dataType = StringDataType(constraints = DataTypeConstraints(pattern = """\.\\"""))
        val info = validation.validate(dataType)

        val prop = info.prop
        prop.dataTypeValue shouldBe "String"
        prop.imports shouldBe setOf(validations.PATTERN)
        prop.annotations shouldBe setOf("""@Pattern(regexp = "\\.\\\\")""")

        val io = info.inout
        io.dataTypeValue shouldBe """@Pattern(regexp = "\\.\\\\") String"""
        io.imports shouldBe setOf(validations.PATTERN)
        io.annotations.shouldBeEmpty()
    }

    "does apply validation annotations to 'collection' item" {
        val dataType = ListDataType(
            StringDataType(constraints = DataTypeConstraints(minLength = 2, maxLength = 3)),
            constraints = DataTypeConstraints()
        )
        val info = validation.validate(dataType, true)

        val prop = info.prop
        prop.dataTypeValue shouldBe "List<@Size(min = 2, max = 3) String>"
        prop.imports shouldBe setOf(validations.NOT_NULL, validations.SIZE)
        prop.annotations shouldBe setOf("@NotNull")

        val io = info.inout
        io.dataTypeValue shouldBe "@NotNull List<@Size(min = 2, max = 3) String>"
        io.imports shouldBe setOf(validations.NOT_NULL, validations.SIZE)
        io.annotations.shouldBeEmpty()
    }

    "does apply validation annotations to 'collection' model items" {
        val dataType = ListDataType(
            ObjectDataType("Foo", "pkg",
                linkedMapOf("foo" to propertyDataType(StringDataType(
                    constraints = DataTypeConstraints(minLength = 2, maxLength = 3))
                ))))
        val info = validation.validate(dataType, true)

        val prop = info.prop
        prop.dataTypeValue shouldBe "List<@Valid Foo>"
        prop.imports shouldBe setOf(validations.NOT_NULL, validations.VALID)
        prop.annotations shouldBe setOf("@NotNull")

        val io = info.inout
        io.dataTypeValue shouldBe "@NotNull List<@Valid Foo>"
        io.imports shouldBe setOf(validations.NOT_NULL, validations.VALID)
        io.annotations.shouldBeEmpty()
    }

    "does apply validation annotations to 'array' item" {
        val dataType = ArrayDataType(StringDataType(
            constraints = DataTypeConstraints(minLength = 2, maxLength = 3)),
            constraints = DataTypeConstraints())
        val info = validation.validate(dataType, true)

        val prop = info.prop
        prop.dataTypeValue shouldBe "String[]"
        prop.imports shouldBe setOf(validations.NOT_NULL)
        prop.annotations shouldBe setOf("@NotNull")

        val io = info.inout
        io.dataTypeValue shouldBe "@NotNull String[]"
        io.imports shouldBe setOf(validations.NOT_NULL)
        io.annotations.shouldBeEmpty()
    }

    "does apply validation annotations to 'array' model items" {
        val dataType = ArrayDataType(
            ObjectDataType("Foo", "pkg",
                linkedMapOf("foo" to propertyDataType(StringDataType(
                    constraints = DataTypeConstraints(minLength = 2, maxLength = 3))
                ))))
        val info = validation.validate(dataType, true)

        val prop = info.prop
        prop.dataTypeValue shouldBe "Foo[]"
        prop.imports shouldBe setOf(validations.NOT_NULL, validations.VALID)
        prop.annotations shouldBe listOf("@Valid", "@NotNull")

        val io = info.inout
        io.dataTypeValue shouldBe "@Valid @NotNull Foo[]"
        io.imports shouldBe setOf(validations.NOT_NULL, validations.VALID)
        io.annotations.shouldBeEmpty()
    }

    "applies @Email to String" {
        val dataType = StringDataType(constraints = DataTypeConstraints(format = "email"))
        val info = validation.validate(dataType)

        val prop = info.prop
        prop.dataTypeValue shouldBe "String"
        prop.imports shouldBe setOf(validations.EMAIL)
        prop.annotations shouldBe setOf("@Email")

        val io = info.inout
        io.dataTypeValue shouldBe "@Email String"
        io.imports shouldBe setOf(validations.EMAIL)
        io.annotations.shouldBeEmpty()
    }
})
