/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.swagger

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.net.URI

class ServerSpec: StringSpec({

    "get server from openapi 3.0" {
        val parser = Parser()

        val api = parser.parseString("""
            openapi: 3.0.5
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

        val servers = api.getServers()
        servers shouldHaveSize 1
        servers[0].getUri() shouldBe URI.create("https://openapiprocessor.io:443/foo/bar/v1")
    }

    "get server from openapi 3.1" {
        val parser = Parser()

        val api = parser.parseString("""
            openapi: 3.1.0
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

        val servers = api.getServers()
        servers shouldHaveSize 1
        servers[0].getUri() shouldBe URI.create("https://openapiprocessor.io:443/foo/bar/v1")
    }
})
