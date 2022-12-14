/*
 * Copyright 2019 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    "provides empty list when no imports are left" {
        val filter = DefaultImportFilter()
        val result = filter.filter("any", setOf(
            "java.lang.String"
        ))

        result.isEmpty() shouldBe true
    }

})
