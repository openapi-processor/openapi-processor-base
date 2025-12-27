/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.openapiprocessor.core.processor.MappingReader

class MappingReaderInterfacesSpec: StringSpec ({
    isolationMode = IsolationMode.InstancePerTest

    val reader = MappingReader()

    "read global interfaces type mapping" {
        val yaml = """
           |openapi-processor-mapping: v16
           |map:
           |  types:
           |   - type: Foo =+ java.io.Serializable
           |   - type: Foo =+ some.other.Interface
           |
           """.trimMargin()

        val mapping = reader.read(yaml) as Mapping
        val types = mapping.map.types

        types[0].type shouldBeEqual "Foo =+ java.io.Serializable"
        types[1].type shouldBeEqual "Foo =+ some.other.Interface"
    }

    "read path interface type mapping" {
        val yaml = """
           |openapi-processor-mapping: v16
           |map:
           |  paths:
           |    /foo:
           |      types:
           |        - type: Foo =+ java.io.Serializable
           |        - type: Foo =+ some.other.Interface
           |
           """.trimMargin()

        val mapping = reader.read(yaml) as Mapping
        val types = mapping.map.paths["/foo"]!!.types

        types[0].type shouldBeEqual "Foo =+ java.io.Serializable"
        types[1].type shouldBeEqual "Foo =+ some.other.Interface"
    }

    "read path/method interface type mapping" {
        val yaml = """
           |openapi-processor-mapping: v16
           |map:
           |  paths:
           |    /foo:
           |      get:
           |        types:
           |          - type: Foo =+ java.io.Serializable
           |          - type: Foo =+ some.other.Interface
           |
           """.trimMargin()

        val mapping = reader.read(yaml) as Mapping
        val types = mapping.map.paths["/foo"]!!.get!!.types

        types[0].type shouldBeEqual "Foo =+ java.io.Serializable"
        types[1].type shouldBeEqual "Foo =+ some.other.Interface"
    }
})
