/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.openapiprocessor.test.stream.Memory
import java.net.URI

class ServerSpec: StringSpec({

    "get server from openapi 3.0" {
        val parser = Parser()

        Memory.add("openapi.yaml", """
            openapi: 3.0.5
            info:
              title: OpenAPI
              version: "1"
            paths: {}
            servers:
              - url: "{schema}://{host}:{port}/{path1}/{path2}/v{version}"
                variables:
                  schema:
                    default: https
                    enum:
                      - https
                      - http
                  host:
                    default: openapiprocessor.io
                  port:
                    default: "443"
                  path1:
                    default: foo
                  path2:
                    default: bar
                  version:
                    default: "1"
        """.trimIndent())

        val api = parser.parse("memory:openapi.yaml")

        val servers = api.getServers()
        servers shouldHaveSize 1
        servers[0].getUri() shouldBe URI.create("https://openapiprocessor.io:443/foo/bar/v1")
    }

    "get server from openapi 3.1" {
        val parser = Parser()

        Memory.add("openapi.yaml", """
            openapi: 3.1.0
            info:
              title: OpenAPI
              version: "1"
            paths: {}            
            servers:
              - url: "{schema}://{host}:{port}/{path1}/{path2}/v{version}"
                variables:
                  schema:
                    default: https
                    enum:
                      - https
                      - http
                  host:
                    default: openapiprocessor.io
                  port:
                    default: "443"
                  path1:
                    default: foo
                  path2:
                    default: bar
                  version:
                    default: "1"
        """.trimIndent())

        val api = parser.parse("memory:openapi.yaml")

        val servers = api.getServers()
        servers shouldHaveSize 1
        servers[0].getUri() shouldBe URI.create("https://openapiprocessor.io:443/foo/bar/v1")
    }
})
