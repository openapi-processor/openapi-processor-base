/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeTrue
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

    "validates mapping.yaml with current schema version" {
        val yaml = """
            |openapi-processor-mapping: current
            |
            |options:
            |  package-name: io.openapiprocessor.somewhere
        """.trimMargin()

        // when:
        val output = validator.validate (yaml)

        // then:
        output.isValid.shouldBeTrue()
    }

    "validates package-name option" {
        val yaml = """
           |openapi-processor-mapping: current
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           """.trimMargin()

        // when:
        val output = validator.validate (yaml)

        // then:
        output.isValid.shouldBeTrue()
    }

    /*
    "detects unknown top level property" {
        val yaml = """
       |openapi-processor-mapping: current
       |
       |options:
       |  package-name: io.openapiprocessor.somewhere
       |
       |bad:
       |
       """.trimMargin()

        // when:
        val output = validator.validate (yaml)

        // then:
        output.isValid.shouldBeFalse()
        output.errors?.shouldHaveSize(1)
        val error = output.errors!!.first()
        error.instanceLocation.shouldBe("/bad")
        error.error shouldBe "the value does not validate against the 'false' schema"
    }
    */

    "validates example mapping" {
        val output = validator.validate("/mapping/v18/mapping.example.yaml".fromResource())

        val error = output.error
        if(error != null) {
            println(error)
        }

        output.errors?.forEach { ou ->
            println("'${ou.error}': at instance ${ou.instanceLocation} (schema ${ou.absoluteKeywordLocation.substringAfter("#")})")
        }

        output.isValid.shouldBeTrue()
    }

    "validates mapping with result key on multiple levels" {
        val output = validator.validate("/mapping/v4/mapping-result.yaml".fromResource())
        output.isValid.shouldBeTrue()
    }
})