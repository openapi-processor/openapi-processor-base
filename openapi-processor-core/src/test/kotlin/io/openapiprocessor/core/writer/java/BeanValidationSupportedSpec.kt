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

    data class TestCase(val targetType: TargetType)

    withData(
         nameFn = { "target supports @DecimalMin/Max - ${it.targetType.typeName}" },
        listOf(
            TestCase(TargetType("java.math.BigDecimal")),
            TestCase(TargetType("java.math.BigInteger")),
            TestCase(TargetType("java.lang.CharSequence")),
            TestCase(TargetType("java.lang.String")),        // implements CharSequence
            TestCase(TargetType("java.lang.StringBuilder")), // implements CharSequence
            TestCase(TargetType("java.lang.Byte")),
            TestCase(TargetType("java.lang.Short")),
            TestCase(TargetType("java.lang.Integer")),
            TestCase(TargetType("java.lang.Long")),
            TestCase(TargetType("byte")),
            TestCase(TargetType("short")),
            TestCase(TargetType("int")),
            TestCase(TargetType("long")),
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
            TestCase(TargetType("java.time.Year")),
            TestCase(TargetType("java.util.List")),
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


    withData(
         nameFn = { "target supports @Size - ${it.targetType.typeName}" },
        listOf(
            TestCase(TargetType("java.lang.CharSequence")),
            TestCase(TargetType("java.lang.String")),        // implements CharSequence
            TestCase(TargetType("java.lang.StringBuilder")), // implements CharSequence
            TestCase(TargetType("java.util.Collection")),
            TestCase(TargetType("java.util.ArrayList")),     // implements Collection
            TestCase(TargetType("java.util.ArrayDeque")),    // implements Collection
            TestCase(TargetType("java.util.HashSet")),       // implements Collection
            TestCase(TargetType("java.util.Map")),
            TestCase(TargetType("java.util.HashMap")),       // implements Map
            TestCase(TargetType("java.util.TreeMap")),       // implements Map
            TestCase(TargetType("java.lang.Long[]")),
            TestCase(TargetType("long[]"))
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
            TestCase(TargetType("java.lang.Long")),
            TestCase(TargetType("long")),
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

    withData(
         nameFn = { "target supports @Pattern - ${it.targetType.typeName}" },
        listOf(
            TestCase(TargetType("java.lang.CharSequence")),
            TestCase(TargetType("java.lang.String")),        // implements CharSequence
            TestCase(TargetType("java.lang.StringBuilder")), // implements CharSequence
        )
    ) { (targetType) ->
        val dataType = MappedDataType(
            targetType.getName(),
            targetType.getPkg(),
            emptyList(),
            null,
            false,
            StringDataType(constraints = DataTypeConstraints(pattern = ".*"))
        )

        val info = validation.validate(dataType, false)

        val prop = info.prop
        prop.dataTypeValue shouldBe targetType.getName()
        prop.imports  shouldBe setOf(validations.PATTERN)
        prop.annotations shouldContainExactly listOf("""@Pattern(regexp = ".*")""")

        val io = info.inout
        io.dataTypeValue shouldBe """@Pattern(regexp = ".*") ${targetType.getName()}"""
        io.imports shouldBe setOf(validations.PATTERN)
        io.annotations.shouldBeEmpty()
    }

    withData(
         nameFn = { "target does not support @Pattern - ${it.targetType.typeName}" },
        listOf(
            TestCase(TargetType("java.lang.Long")),
            TestCase(TargetType("long")),
        )
    ) { (targetType) ->
        val dataType = MappedDataType(
            targetType.getName(),
            targetType.getPkg(),
            emptyList(),
            null,
            false,
            StringDataType(constraints = DataTypeConstraints(pattern = ".*"))
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

      withData(
           nameFn = { "target supports @Email - ${it.targetType.typeName}" },
          listOf(
              TestCase(TargetType("java.lang.CharSequence")),
              TestCase(TargetType("java.lang.String")),        // implements CharSequence
              TestCase(TargetType("java.lang.StringBuilder")), // implements CharSequence
          )
      ) { (targetType) ->
          val dataType = MappedDataType(
              targetType.getName(),
              targetType.getPkg(),
              emptyList(),
              null,
              false,
              StringDataType(constraints = DataTypeConstraints(format = "email"))
          )

          val info = validation.validate(dataType, false)

          val prop = info.prop
          prop.dataTypeValue shouldBe targetType.getName()
          prop.imports  shouldBe setOf(validations.EMAIL)
          prop.annotations shouldContainExactly listOf("""@Email""")

          val io = info.inout
          io.dataTypeValue shouldBe """@Email ${targetType.getName()}"""
          io.imports shouldBe setOf(validations.EMAIL)
          io.annotations.shouldBeEmpty()
      }

      withData(
           nameFn = { "target does not support @Email - ${it.targetType.typeName}" },
          listOf(
              TestCase(TargetType("java.lang.Long")),
              TestCase(TargetType("long")),
          )
      ) { (targetType) ->
          val dataType = MappedDataType(
              targetType.getName(),
              targetType.getPkg(),
              emptyList(),
              null,
              false,
              StringDataType(constraints = DataTypeConstraints(format = "email"))
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


