/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
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

class MappingValidatorSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val validator = MappingValidator()

    "validates mapping.yaml with matching schema version" {
        forAll(
            row("v2"),
            row("v2.1"),
            row("v3"),
            row("v4")
        ) { v ->
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

    "validates example mapping v2" {
        val output = validator.validate("/mapping/v2/mapping.example.yaml".fromResource(), "v2")

        val error = output.error
        if(error != null) {
            println(error)
        }

        output.errors?.forEach {
            println("'${it.error}': at instance ${it.instanceLocation} (schema ${it.absoluteKeywordLocation.substringAfter("#")})")
        }

        output.isValid.shouldBeTrue()
    }

    "validates example mapping v2.1" {
        validator.validate("/mapping/v2.1/mapping.example.yaml".fromResource(), "v2.1").isValid.shouldBeTrue()
    }

    "validates example mapping v3" {
        validator.validate("/mapping/v3/mapping.example.yaml".fromResource(), "v3").isValid.shouldBeTrue()
    }

    "validates example mapping v4" {
        validator.validate("/mapping/v4/mapping.example.yaml".fromResource(), "v4").isValid.shouldBeTrue()
    }
})
