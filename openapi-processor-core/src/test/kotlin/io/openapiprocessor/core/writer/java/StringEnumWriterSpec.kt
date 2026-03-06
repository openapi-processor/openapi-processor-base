/*
 * Copyright 2016 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.style.StringSpec
import io.mockk.mockk
import io.mockk.verify
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.Documentation
import io.openapiprocessor.core.model.datatypes.DataTypeName
import io.openapiprocessor.core.model.datatypes.StringEnumDataType
import java.io.StringWriter

class StringEnumWriterSpec : StringSpec({
    val options = ApiOptions()

    "looks up jackson annotations" {
        val jackson = mockk<JacksonAnnotations>(relaxed = true)

        val writer = StringEnumWriter(
            options,
            JavaIdentifier(),
            SimpleGeneratedWriter(options),
            jacksonAnnotations = jackson)
        val target = StringWriter()

        val dataType = StringEnumDataType(
            DataTypeName("Foo"),
            "pkg",
            listOf(),
            null,
            false,
            Documentation(null, "description"))

        writer.write(target, dataType)

        verify { jackson.getJsonCreator() }
        verify { jackson.getJsonValue() }
    }
})
