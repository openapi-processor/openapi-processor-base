/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.mapping.TargetType
import io.openapiprocessor.core.model.datatypes.DataTypeConstraints
import io.openapiprocessor.core.model.datatypes.IntegerDataType
import io.openapiprocessor.core.model.datatypes.MappedDataType
import io.openapiprocessor.core.model.datatypes.StringDataType


class BeanValidationSupportedSpec : FreeSpec({
    val apiOptions = ApiOptions()
    val validation = BeanValidationFactory(apiOptions)
    val validations = validation.validations

    data class DecimalMinMax(val targetType: TargetType)

    withData(
         nameFn = { "target supports @DecimalMin/Max - ${it.targetType.typeName}" },
        listOf(
            DecimalMinMax(TargetType("java.math.BigDecimal")),
            DecimalMinMax(TargetType("java.math.BigInteger")),
            DecimalMinMax(TargetType("java.lang.CharSequence")),
            DecimalMinMax(TargetType("java.lang.String")),        // implements CharSequence
            DecimalMinMax(TargetType("java.lang.StringBuilder")), // implements CharSequence
            DecimalMinMax(TargetType("java.lang.Byte")),
            DecimalMinMax(TargetType("java.lang.Short")),
            DecimalMinMax(TargetType("java.lang.Integer")),
            DecimalMinMax(TargetType("java.lang.Long")),
            DecimalMinMax(TargetType("byte")),
            DecimalMinMax(TargetType("short")),
            DecimalMinMax(TargetType("int")),
            DecimalMinMax(TargetType("long")),
        )
    ) { (targetType) ->
        val dataType = MappedDataType(
            targetType.getName(),
            targetType.getPkg(),
            emptyList(),
            null,
            false,
            IntegerDataType(constraints = DataTypeConstraints(minimum = 1970, maximum = 3000))
        )

        val info = validation.validate(dataType, false)

        val prop = info.prop
        prop.dataTypeValue shouldBe targetType.getName()
        prop.imports  shouldBe setOf(validations.DECIMAL_MIN, validations.DECIMAL_MAX)
        prop.annotations shouldContainExactly listOf("""@DecimalMin(value = "1970")""", """@DecimalMax(value = "3000")""")

        val io = info.inout
        io.dataTypeValue shouldBe """@DecimalMin(value = "1970") @DecimalMax(value = "3000") ${targetType.getName()}"""
        io.imports shouldBe setOf(validations.DECIMAL_MIN, validations.DECIMAL_MAX)
        io.annotations.shouldBeEmpty()
    }

    withData(
         nameFn = { "target does not support @DecimalMin/Max - ${it.targetType.typeName}" },
        listOf(
            DecimalMinMax(TargetType("java.time.Year")),
            DecimalMinMax(TargetType("java.util.List")),
        )
    ) { (targetType) ->
        val dataType = MappedDataType(
            targetType.getName(),
            targetType.getPkg(),
            emptyList(),
            null,
            false,
            IntegerDataType(constraints = DataTypeConstraints(minimum = 1970, maximum = 3000))
        )

        val info = validation.validate(dataType, false)

        val prop = info.prop
        prop.dataTypeValue shouldBe targetType.getName()
        prop.imports.shouldBeEmpty()
        prop.annotations.shouldBeEmpty()

        val io = info.inout
        io.dataTypeValue shouldBe targetType.getName()
        io.imports.shouldBeEmpty()
        io.annotations.shouldBeEmpty()
    }


    data class Size(val targetType: TargetType)

    withData(
         nameFn = { "target supports @Size - ${it.targetType.typeName}" },
        listOf(
            Size(TargetType("java.lang.CharSequence")),
            Size(TargetType("java.lang.String")),        // implements CharSequence
            Size(TargetType("java.lang.StringBuilder")), // implements CharSequence
            Size(TargetType("java.util.Collection")),
            Size(TargetType("java.util.ArrayList")),     // implements Collection
            Size(TargetType("java.util.ArrayDeque")),    // implements Collection
            Size(TargetType("java.util.HashSet")),       // implements Collection
            Size(TargetType("java.util.Map")),
            Size(TargetType("java.util.HashMap")),       // implements Map
            Size(TargetType("java.util.TreeMap")),       // implements Map
            Size(TargetType("java.lang.Long[]")),
            Size(TargetType("long[]"))
        )
    ) { (targetType) ->
        val dataType = MappedDataType(
            targetType.getName(),
            targetType.getPkg(),
            emptyList(),
            null,
            false,
            StringDataType(constraints = DataTypeConstraints(
                minLength = 1, maxLength = 10, minItems = 1, maxItems = 10
            ))
        )

        val info = validation.validate(dataType, false)

        val prop = info.prop
        prop.dataTypeValue shouldBe targetType.getName()
        prop.imports  shouldBe setOf(validations.SIZE)
        prop.annotations shouldContainExactly listOf("""@Size(min = 1, max = 10)""")

        val io = info.inout
        io.dataTypeValue shouldBe """@Size(min = 1, max = 10) ${targetType.getName()}"""
        io.imports shouldBe setOf(validations.SIZE)
        io.annotations.shouldBeEmpty()
    }

    withData(
         nameFn = { "target does not support @Size - ${it.targetType.typeName}" },
        listOf(
            DecimalMinMax(TargetType("java.lang.Long")),
            DecimalMinMax(TargetType("long")),
        )
    ) { (targetType) ->
        val dataType = MappedDataType(
            targetType.getName(),
            targetType.getPkg(),
            emptyList(),
            null,
            false,
            StringDataType(constraints = DataTypeConstraints(
                minLength = 1, maxLength = 10, minItems = 1, maxItems = 10
            ))
        )

        val info = validation.validate(dataType, false)

        val prop = info.prop
        prop.dataTypeValue shouldBe targetType.getName()
        prop.imports.shouldBeEmpty()
        prop.annotations.shouldBeEmpty()

        val io = info.inout
        io.dataTypeValue shouldBe targetType.getName()
        io.imports.shouldBeEmpty()
        io.annotations.shouldBeEmpty()
    }

})


