/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class AnnotationSpec: StringSpec({

    "simple parameter value & type" {
      val parameter = SimpleParameterValue("2023")

      parameter.value shouldBe "2023"
      parameter.import.shouldBeNull()
    }

    "class parameter value & type" {
      val parameter = ClassParameterValue("io.oap.Foo.class")

      parameter.value shouldBe "Foo.class"
      parameter.import shouldBe "io.oap.Foo"
    }
})
