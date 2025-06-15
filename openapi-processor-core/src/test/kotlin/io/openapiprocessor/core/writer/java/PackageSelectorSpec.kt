/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.converter.options.PackageOptions
import java.net.URI

class PackageSelectorSpec : StringSpec({

    "crates package name from base" {
        val options = PackageOptions().apply {
            base = "io.openapiprocessor.base"
            location = null
        }

        val calc = PackageSelector(options)
        val pkg = calc.getPackageName(URI("project/src/api/openapi.yaml"), "api")

        pkg.shouldBe("io.openapiprocessor.base.api")
    }

    "creates package name from location" {
        val options = PackageOptions().apply {
            base = "io.openapiprocessor.base"
            location = "io.openapiprocessor.location"
        }

        val calc = PackageSelector(options)
        val pkg = calc.getPackageName(URI("project/src/java/io.openapiprocessor/location/somewhere/resource.yaml"), "api")

        pkg.shouldBe("io.openapiprocessor.location.somewhere")
    }
})
