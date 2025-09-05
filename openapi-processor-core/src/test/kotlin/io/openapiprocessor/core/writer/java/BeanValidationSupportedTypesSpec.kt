/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.engine.names.WithDataTestName
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.model.datatypes.*

class BeanValidationSupportedTypesSpec : FreeSpec({
    val annotations = BeanValidations(BeanValidationFormat.JAKARTA)
    val types = BeanValidationSupportedTypes()

    data class TestCase(val source: String, val target: DataType, val supported: Boolean = true): WithDataTestName {
        override fun dataTestName(): String {
            return "$source -> ${target.getTypeName()}"
        }
    }

    withData(
        // match generic type
        TestCase(annotations.SIZE,
            GenericDataType(
                DataTypeName("List"), "java.util", listOf(
                    GenericDataType(DataTypeName("String"), "java.lang")
                )
            )),

        TestCase(annotations.SIZE,
            GenericDataType(
                DataTypeName("Map"), "java.util", listOf(
                    GenericDataType(DataTypeName("String"), "java.lang"),
                    GenericDataType(DataTypeName("String"), "java.lang")
                )
            )),

        // match array type
        TestCase(annotations.SIZE, ArrayDataType(LongDataType())),

        // match target class
        TestCase(annotations.DECIMAL_MIN, MappedDataType("BigDecimal", "java.math")),
        TestCase(annotations.DECIMAL_MIN, MappedDataType("BigInteger", "java.math")),
        TestCase(annotations.DECIMAL_MIN, MappedDataType("Byte", "java.lang")),
        TestCase(annotations.DECIMAL_MIN, MappedDataType("Short", "java.lang")),
        TestCase(annotations.DECIMAL_MIN, IntegerDataType()),
        TestCase(annotations.DECIMAL_MIN, LongDataType()),
        TestCase(annotations.DECIMAL_MIN, MappedDataTypePrimitive("byte")),
        TestCase(annotations.DECIMAL_MIN, MappedDataTypePrimitive("short")),
        TestCase(annotations.DECIMAL_MIN, MappedDataTypePrimitive("int")),
        TestCase(annotations.DECIMAL_MIN, MappedDataTypePrimitive("long")),

        // match target implements interface (CharSequence)
        TestCase(annotations.DECIMAL_MIN, MappedDataType("String", "java.lang"))

    ) { (source, target, supported) ->
        types.supports(source, target) shouldBe supported
    }

    "add additional supported types" {
        val bvSupportedTypes = BeanValidationSupportedTypes(mapOf(
            "jakarta.validation.constraints.Size" to setOf("org.openapitools.jackson.nullable.JsonNullable")
        ))

        bvSupportedTypes.supports(
            annotations.SIZE,
            MappedDataType("JsonNullable", "org.openapitools.jackson.nullable"))
            .shouldBeTrue()
    }
})
