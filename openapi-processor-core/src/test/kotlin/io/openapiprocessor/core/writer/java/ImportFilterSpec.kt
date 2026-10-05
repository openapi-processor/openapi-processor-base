/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

class ImportFilterSpec: StringSpec({

    "drops imports from same package" {
        val filter = DefaultImportFilter()
        val result = filter.filter("same", setOf(
            "other.Foo",
            "same.Bar",
            "same.nested.Bar"
        ))

        result.size shouldBe 2
        result shouldContain "other.Foo"
        result shouldContain "same.nested.Bar"
    }

    "drops java.lang imports" {
        val filter = DefaultImportFilter()
        val result = filter.filter("any", setOf(
            "java.lang.String",
            "java.lang.Long",
            "other.Foo"
        ))

        result.size shouldBe 1
        result.first() shouldBe "other.Foo"
    }

    "does not drops java.lang.<some pkg>.<some class> imports" {
        val filter = DefaultImportFilter()
        val result = filter.filter("any", setOf(
            "java.lang.annotation.Target"
        ))

        result.size shouldBe 1
        result.first() shouldBe "java.lang.annotation.Target"
    }

    "provides empty list when no imports are left" {
        val filter = DefaultImportFilter()
        val result = filter.filter("any", setOf(
            "java.lang.String"
        ))

        result.isEmpty() shouldBe true
    }

})
