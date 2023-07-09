/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi4j

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs.newFileSystem
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.parser.HttpMethod
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.writeText

class ResolvesPathsSpec: StringSpec({
    val ref = "\$ref"
    val fs = newFileSystem(Configuration.unix())

    "resolves ref path on custom filesystem" {
        val main = """
           openapi: 3.0.2
           info:
             title: API
             version: 1.0.0
           
           paths:
             /foo:
               $ref: ref.yaml
        """.trimIndent()

        val referenced = """
            get:
              responses:
                '200':
                  description: none
                  content:
                    application/json:
                      schema:
                        type: string
            """.trimIndent()

        val root = Files.createDirectory(fs.getPath("source"))

        val mainYaml = root.resolve("openapi.yaml")
        val refYaml = root.resolve("ref.yaml")
        copy(main, mainYaml)
        copy(referenced, refYaml)

        // when:
        val openapi4j = Parser()
        val api = openapi4j.parse (mainYaml.toUri ().toString ())

        // then:
        val foo = api.getPaths()["/foo"]!!
        foo.getOperations()[0].getMethod() shouldBe HttpMethod.GET
    }
})

// write source to file system
fun copy (source: String, target: Path) {
    Files.createDirectories (target.parent)
    target.writeText(source)
}
