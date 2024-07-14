/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package com.github.hauner.openapi.core.converter

import com.github.hauner.openapi.core.test.ModelAsserts
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.framework.Framework
import io.openapiprocessor.core.support.TestFrameworkAnnotations
import io.openapiprocessor.core.writer.java.*
import spock.lang.Specification
import spock.lang.Unroll

import static com.github.hauner.openapi.core.test.FactoryHelper.apiConverter
import static com.github.hauner.openapi.core.test.OpenApiParser.parse

class ApiConverterSpec extends Specification implements ModelAsserts {

    void "groups endpoints into interfaces by first operation tag" () {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /a:
    get:
      tags:
        - ping
      responses:
        '204':
          description: none
  /b:
    get:
      tags:
        - pong
      responses:
        '204':
          description: none
  /c:
    get:
      tags:
        - ping
        - pong
      responses:
        '204':
          description: none
""")

        when:
        api = apiConverter (Stub (Framework))
            .convert (openApi)

        then:
        assertInterfaces ('Ping', 'Pong')
        assertPingEndpoints ('/a', '/c')
        assertPongEndpoints ('/b')
    }

    void "groups endpoints into interfaces by valid tag identifier" () {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /a:
    get:
      tags:
        - test_api
      responses:
        '204':
          description: none
  /b:
    get:
      tags:
        - test-api
      responses:
        '204':
          description: none

""")

        when:
        api = apiConverter (Stub (Framework))
            .convert (openApi)

        then:
        def actual = api.interfaces
        assert actual.size () == 1
        assert actual.first ().name == "TestApi"
    }

    @Unroll
    void "groups endpoints with method #method into interfaces" () {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /a:
    ${method}:
      tags:
        - ${method}
      responses:
        '204':
          description: no content
""")

        when:
        def options = new ApiOptions()
        def identifier = new JavaIdentifier()
        api = apiConverter (options, Stub (Framework))
            .convert (openApi)

        def w = new InterfaceWriter (
            options,
            Stub (GeneratedWriter),
            new MethodWriter(
                options,
                identifier,
                Stub (MappingAnnotationWriter),
                Stub (ParameterAnnotationWriter),
                Stub (BeanValidationFactory),
                Stub (JavaDocWriter)),
            new TestFrameworkAnnotations(),
            new BeanValidationFactory(options),
            new DefaultImportFilter())
        def writer = new StringWriter()
        w.write (writer, api.interfaces.get (0))

        then:
        assertInterfaces (method.capitalize ())
        assertEndpoints (method,'/a')

        where:
        method << ['get', 'put', 'post', 'delete', 'options', 'head', 'patch', 'trace']
    }

    void "sets interface package from processor options with 'api' sub package" () {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /foo:
    get:
      responses:
        '204':
          description: no content
""")

        def options = new ApiOptions(
            packageName: 'a.package.name'
        )

        when:
        api = apiConverter (options, Stub(Framework))
            .convert (openApi)

        then:
        api.interfaces.first ().packageName == [options.packageName, 'api'].join ('.')
    }

    void "sets empty interface name when no interface name tag was provided" () {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /foo:
    get:
      responses:
        '204':
          description: no content
""")

        when:
        api = apiConverter (Stub (Framework))
            .convert (openApi)

        then:
        api.interfaces.first ().interfaceName == 'Api'
    }
}
