/*
 * Copyright 2020 the original authors
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

package io.openapiprocessor.core.processor

import io.openapiprocessor.core.converter.mapping.*
import io.openapiprocessor.core.processor.MappingConverter
import io.openapiprocessor.core.processor.MappingReader
import spock.lang.Specification
import spock.lang.Subject

class MappingConverterV2Spec extends Specification {

    def reader = new MappingReader()

    @Subject
    def converter = new MappingConverter()

    void "reads global response type mapping" () {
        String yaml = """\
openapi-processor-mapping: v2
options: {}
map:
  responses:
    - content: application/vnd.array => java.util.List
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size() == 1

        def response = mappings.first () as ContentTypeMapping
        response.contentType == 'application/vnd.array'
        response.mapping.sourceTypeName == null
        response.mapping.sourceTypeFormat == null
        response.mapping.targetTypeName == 'java.util.List'
        response.mapping.genericTypes == []
    }

    void "reads endpoint exclude flag" () {
        String yaml = """\
openapi-processor-mapping: v2
options: {}
map:
  paths:
    /foo:
      exclude: ${exclude.toString ()}
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size() == 1

        def endpoint = mappings.first () as EndpointTypeMapping
        endpoint.path == '/foo'
        endpoint.exclude == exclude
        endpoint.typeMappings.empty

        where:
        exclude << [true, false]
    }

    void "reads endpoint parameter type mapping" () {
        String yaml = """\
openapi-processor-mapping: v2
options: {}
map:
  paths:
    /foo:
      parameters:
        - name: foo => mapping.Foo
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size() == 1

        def endpoint = mappings.first () as EndpointTypeMapping
        endpoint.path == '/foo'
        endpoint.typeMappings.size () == 1
        def parameter = endpoint.typeMappings.first () as NameTypeMapping
        parameter.parameterName == 'foo'
        parameter.mapping.sourceTypeName == null
        parameter.mapping.sourceTypeFormat == null
        parameter.mapping.targetTypeName == 'mapping.Foo'
        parameter.mapping.genericTypes == []
    }

    void "reads endpoint add mapping" () {
        String yaml = """\
openapi-processor-mapping: v2
options: {}
map:
  paths:
    /foo:
      parameters:
        - add: request => javax.servlet.http.HttpServletRequest
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size () == 1

        def endpoint = mappings.first () as EndpointTypeMapping
        endpoint.path == '/foo'
        endpoint.typeMappings.size () == 1

        def parameter = endpoint.typeMappings.first () as AddParameterTypeMapping
        parameter.parameterName == 'request'
        parameter.mapping.sourceTypeName == null
        parameter.mapping.sourceTypeFormat == null
        parameter.mapping.targetTypeName == 'javax.servlet.http.HttpServletRequest'
        parameter.mapping.genericTypes == []
    }

    void "reads endpoint add mapping with annotation" () {
        String yaml = """\
openapi-processor-mapping: v2
options: {}
map:
  paths:
    /foo:
      parameters:
        - add: foo => io.micronaut.http.annotation.RequestAttribute(ANY) java.lang.String
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size () == 1

        def endpoint = mappings.first () as EndpointTypeMapping
        endpoint.path == '/foo'
        endpoint.typeMappings.size () == 1

        def parameter = endpoint.typeMappings.first () as AddParameterTypeMapping
        parameter.parameterName == 'foo'
        parameter.mapping.sourceTypeName == null
        parameter.mapping.sourceTypeFormat == null
        parameter.mapping.targetTypeName == 'java.lang.String'
        parameter.mapping.genericTypes == []
        parameter.annotation.type == 'io.micronaut.http.annotation.RequestAttribute'
        parameter.annotation.parameters.size () == 1
        parameter.annotation.parameters[""].value == "ANY"
    }

    void "reads endpoint response type mapping" () {
        String yaml = """\
openapi-processor-mapping: v2
options: {}
map:
  paths:
    /foo:
      responses:
        - content: application/vnd.array => java.util.List
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size() == 1

        def endpoint = mappings.first () as EndpointTypeMapping
        endpoint.path == '/foo'
        endpoint.typeMappings.size () == 1

        def response = endpoint.typeMappings.first () as ContentTypeMapping
        response.contentType == 'application/vnd.array'
        response.mapping.sourceTypeName == null
        response.mapping.sourceTypeFormat == null
        response.mapping.targetTypeName == 'java.util.List'
        response.mapping.genericTypes == []
    }

    void "reads endpoint result mapping #result" () {
        String yaml = """\
openapi-processor-mapping: v2
options: {}
map:
  paths:
    /foo:
      result: $result
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size() == 1

        def endpoint = mappings.first () as EndpointTypeMapping
        endpoint.path == '/foo'
        endpoint.typeMappings.size () == 1

        def type = endpoint.typeMappings.first () as ResultTypeMapping
        type.targetTypeName == expected

        where:
        result                                    | expected
        'plain'                                   | 'plain'
        'org.springframework.http.ResponseEntity' | 'org.springframework.http.ResponseEntity'
    }


    void "reads endpoint single & multi mapping" () {
        String yaml = """\
openapi-processor-mapping: v2
options: {}
map:
  paths:
    /foo:
      single: $single
      multi: $multi
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size() == 1

        def endpoint = mappings.first () as EndpointTypeMapping
        endpoint.path == '/foo'
        endpoint.typeMappings.size () == 2

        def typeSingle = endpoint.typeMappings.first () as TypeMapping
        typeSingle.sourceTypeName == 'single'
        typeSingle.targetTypeName == single
        def typeMulti = endpoint.typeMappings[1] as TypeMapping
        typeMulti.sourceTypeName == 'multi'
        typeMulti.targetTypeName == multi

        where:
        single << ['reactor.core.publisher.Mono']
        multi << ['reactor.core.publisher.Flux']
    }

}
