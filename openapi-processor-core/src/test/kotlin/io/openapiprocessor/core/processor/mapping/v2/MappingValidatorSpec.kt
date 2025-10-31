/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.Row1
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.processor.MappingValidator

private fun String.fromResource(): String {
    return MappingValidator::class.java
        .getResourceAsStream(this)!!
        .readAllBytes()
        .decodeToString()
}

const val latestMapping: Int = 15

private fun createMappingRows(): Array<Row1<String>> {
    return 2.rangeTo(latestMapping).map {
        row("v$it")
    }.toTypedArray()
}

class MappingValidatorSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val validator = MappingValidator()

    "validates mapping.yaml with matching schema version" {
        forAll(*createMappingRows()) { v ->
            val yaml = """
                |openapi-processor-mapping: $v
                |
                |options:
                |  package-name: io.openapiprocessor.somewhere
            """.trimMargin()

            // when:
            val output = validator.validate (yaml, v)

            // then:
            output.isValid.shouldBeTrue()
        }
    }

    "validates package-name option" {
        val yaml = """
                   |openapi-processor-mapping: v2
                   |
                   |options:
                   |  package-name: io.openapiprocessor.somewhere
                   """.trimMargin()

        // when:
        val output = validator.validate (yaml, "v2")

        // then:
        output.isValid.shouldBeTrue()
    }

    "detects unknown top level property" {
        val yaml = """
                   |openapi-processor-mapping: v2
                   |
                   |options: {}
                   |
                   |bad:
                   |
                   """.trimMargin()

        // when:
        val output = validator.validate (yaml, "v2")

        // then:
        output.isValid.shouldBeFalse()
        output.errors?.shouldHaveSize(1)
        val error = output.errors!!.first()
        error.instanceLocation.shouldBe("/bad")
        error.error shouldBe "the value does not validate against the 'false' schema"
    }

    "validates example mapping" {
        forAll(*createMappingRows()) { v ->
            val output = validator.validate("/mapping/$v/mapping.example.yaml".fromResource(), v)

            val error = output.error
            if(error != null) {
                println(error)
            }

            output.errors?.forEach { ou ->
                println("'${ou.error}': at instance ${ou.instanceLocation} (schema ${ou.absoluteKeywordLocation.substringAfter("#")})")
            }

            output.isValid.shouldBeTrue()
        }
    }

    "validates mapping with result key on multiple levels" {
        val output = validator.validate("/mapping/v4/mapping-result.yaml".fromResource(), "v4")
        output.isValid.shouldBeTrue()
    }
})