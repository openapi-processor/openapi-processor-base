/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.framework.FrameworkBase
import io.openapiprocessor.core.model.Api
import io.openapiprocessor.core.support.parseApiBody
import io.openapiprocessor.core.support.parseOptionsMapping
import io.openapiprocessor.core.writer.java.JavaIdentifier

class ApiConverterServerUriSpec: StringSpec({

    "add server uri as prefix to api path" {

        val options = parseOptionsMapping(
            """
            |options:
            |  package-name: pkg
            |  server-url: 0
            """)

         val openApi = parseApiBody(
             """
             |servers:
             |  - url: "{schema}://{host}:{port}/{path1}/{path2}/v{version}"
             |    variables:
             |      schema:
             |        default: https
             |        enum:
             |          - https
             |          - http
             |      host:
             |        default: openapiprocessor.io
             |      port:
             |        default: "443"
             |      path1:
             |        default: foo
             |      path2:
             |        default: bar
             |      version:
             |        default: "1"
             |
             |paths:
             |  /foo:
             |    get:
             |      responses:
             |        '204':
             |          description: ...
             |
             """)

        val api: Api = ApiConverter (options, JavaIdentifier(), FrameworkBase())
            .convert(openApi)

        api.getInterfaces().size shouldBe 1
        val iface = api.getInterfaces().first()
        iface.hasPathPrefix() shouldBe true
        iface.getPathPrefix () shouldBe "/foo/bar/v1"
     }
})
