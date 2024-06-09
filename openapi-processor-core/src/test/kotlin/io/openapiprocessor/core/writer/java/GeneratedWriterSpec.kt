/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.string.shouldBeEmpty
import io.kotest.matchers.string.shouldContain
import io.openapiprocessor.core.converter.ApiOptions
import java.io.StringWriter

class GeneratedWriterSpec: StringSpec({

    "writes @Generated if enabled" {
        val info = GeneratedInfo("openapi-processor-core", "test")
        val options = ApiOptions()
        options.generatedAnnotation = true

        val generated = GeneratedWriterImpl(info, options)

        val imports = generated.getImports()

        val usage = StringWriter()
        generated.writeUse(usage)

        val source = StringWriter()
        generated.writeSource(source)

        imports.shouldNotBeEmpty()
        usage.toString() shouldContain "@Generated"
        source.toString() shouldContain "public @interface Generated"
    }

    "does not write @Generated if disabled" {
        val info = GeneratedInfo("openapi-processor-core", "test")
        val options = ApiOptions()
        options.generatedAnnotation = false

        val generated = GeneratedWriterImpl(info, options)

        val imports = generated.getImports()

        val usage = StringWriter()
        generated.writeUse(usage)

        val source = StringWriter()
        generated.writeSource(source)

        imports.shouldBeEmpty()
        usage.toString().shouldBeEmpty()
        source.toString().shouldBeEmpty()
    }
})
