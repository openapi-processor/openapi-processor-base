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

package com.github.hauner.openapi.core.converter

import io.openapiprocessor.core.converter.mapping.AmbiguousTypeMappingException
import io.openapiprocessor.core.framework.Framework
import io.openapiprocessor.core.model.Api
import io.openapiprocessor.core.parser.ParserType
import spock.lang.Specification
import spock.lang.Unroll

import static com.github.hauner.openapi.core.test.FactoryHelper.apiConverter
import static io.openapiprocessor.core.support.ApiOptionsKt.parseOptionsMapping
import static io.openapiprocessor.core.support.OpenApiParserKt.parseApiBody

class DataTypeConverterPrimitiveTypeMappingSpec extends Specification {

    void "converts basic types with format to java type via global type mapping" () {
        def options = parseOptionsMapping("""
            |map:
            |  types:
            |    - type: string:date-time => java.time.ZonedDateTime
            """)

        def openApi = parseApiBody ("""
            |paths:
            |  /page:
            |    get:
            |      parameters:
            |        - in: query
            |          name: date
            |          required: false
            |          schema:
            |            type: string
            |            format: date-time
            |      responses:
            |        '204':
            |          description: none
            """)

        when:
        Api api = apiConverter (options).convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def parameter = ep.parameters.first ()
        parameter.dataType.packageName == 'java.time'
        parameter.dataType.name == 'ZonedDateTime'
    }

    void "primitive type dose not match primitive global type mapping with format" () {
        def options = parseOptionsMapping("""
            |map:
            |  types:
            |    - type: string:binary => io.openapiprocessor.Bar
            """)

        def openApi = parseApiBody ("""
            |paths:
            |  /foo:
            |    get:
            |      parameters:
            |        - in: query
            |          name: foo
            |          schema:
            |            type: array
            |            items:
            |              type: string
            |      responses:
            |        200:
            |          description: response
            |          content:
            |            application/*:
            |              schema:
            |                type: string
            |                format: binary
            """)

        when:
        Api api = apiConverter (options).convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def parameter = ep.parameters.first ()
        parameter.dataType.packageName == 'java.lang'
        parameter.dataType.typeName == 'String[]'
    }

    void "converts named primitive type to java type via global type mapping" () {
        def options = parseOptionsMapping("""
            |map:
            |  types:
            |    - type: UUID => java.util.UUID
            """)

        def openApi = parseApiBody ("""
            |paths:
            |  /uuid:
            |    get:
            |      parameters:
            |        - in: query
            |          name:  uuid
            |          schema:
            |            \$ref: '#/components/schemas/UUID'
            |      responses:
            |        '204':
            |          description: none
            |
            |components:
            |  schemas:          
            |    UUID:
            |      type: string
            """)

        when:
        Api api = apiConverter (options).convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def parameter = ep.parameters.first ()
        parameter.dataType.packageName == 'java.util'
        parameter.dataType.name == 'UUID'

        where:
        parser << [ParserType.SWAGGER, ParserType.OPENAPI4J]
    }

    void "throws when there are multiple global mappings for a simple type" () {
        def options = parseOptionsMapping("""
            |map:
            |  types:
            |    - type: string:date-time => java.time.ZonedDateTime
            |    - type: string:date-time => java.time.ZonedDateTime
            """)

        def openApi = parseApiBody ("""
            |paths:
            |  /page:
            |    get:
            |      parameters:
            |        - in: query
            |          name: date
            |          required: false
            |          schema:
            |            type: string
            |            format: date-time
            |      responses:
            |        '204':
            |          description: none
        """)

        when:
        apiConverter (options, Stub (Framework)).convert (openApi)

        then:
        def e = thrown (AmbiguousTypeMappingException)
        e.typeMappings.size() == 2
    }

    @Unroll
    void "converts primitive parameter schema to java type via #type" () {
        def options = parseOptionsMapping(mappings)

        def openApi = parseApiBody ("""
            |paths:
            |  /foo:
            |    get:
            |      parameters:
            |        - in: query
            |          name: bar
            |          required: false
            |          schema:
            |            type: string
            |            format: date-time
            |      responses:
            |        '204':
            |          description: none
            """)

        when:
        Api api = apiConverter (options).convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def parameter = ep.parameters.first ()
        parameter.dataType.packageName == 'java.time'
        parameter.dataType.name == 'ZonedDateTime'

        where:
        type << [
            'endpoint parameter mapping',
            'global parameter mapping',
            'global type mapping'
        ]

        mappings << [
            """
            |map:
            |  paths:
            |    /foo:
            |      parameters:
            |        - name: bar => java.time.ZonedDateTime
            """,
            """
            |map:
            |  parameters:
            |    - name: bar => java.time.ZonedDateTime
            """,
            """
            |map:
            |  types:
            |    - type: string:date-time => java.time.ZonedDateTime
            """
        ]
    }
}
