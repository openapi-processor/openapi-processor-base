/*
 * Copyright 2019-2020 the original authors
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

import io.openapiprocessor.core.converter.mapping.AddParameterTypeMapping
import io.openapiprocessor.core.converter.mapping.EndpointTypeMapping
import io.openapiprocessor.core.converter.mapping.NameTypeMapping
import io.openapiprocessor.core.converter.mapping.ContentTypeMapping
import io.openapiprocessor.core.converter.mapping.ResultTypeMapping
import io.openapiprocessor.core.converter.mapping.TypeMapping
import io.openapiprocessor.core.processor.MappingConverter
import io.openapiprocessor.core.processor.MappingReader
import spock.lang.Specification
import spock.lang.Subject

/**
 * obsolete
 */
class MappingConverterSpec extends Specification {

    def reader = new MappingReader()

    @Subject
    def converter = new MappingConverter()

    void "reads global type mapping" () {
        String yaml = """\
openapi-processor-mapping: v1.0
    
map:
  types:
    - from: array
      to: java.util.Collection
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size () == 1
        def type = mappings.first () as TypeMapping
        type.sourceTypeName == 'array'
        type.sourceTypeFormat == null
        type.targetTypeName == 'java.util.Collection'
        type.genericTypes == []
    }

    void "reads global type mapping with generic types" () {
        String yaml = """\
openapi-processor-mapping: v1.0
    
map:
  types:
    # inline format
    - from: Foo
      to: mapping.Bar<java.lang.String, java.lang.Boolean>

    # long format
    - from: Foo2
      to: mapping.Bar2
      generics:
        - java.lang.String2
        - java.lang.Boolean2
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size () == 2

        def shortFormat = mappings.first () as TypeMapping
        shortFormat.sourceTypeName == 'Foo'
        shortFormat.sourceTypeFormat == null
        shortFormat.targetTypeName == 'mapping.Bar'
        shortFormat.genericTypes[0].typeName == 'java.lang.String'
        shortFormat.genericTypes[1].typeName == 'java.lang.Boolean'

        def longFormat = mappings[1] as TypeMapping
        longFormat.sourceTypeName == 'Foo2'
        longFormat.sourceTypeFormat == null
        longFormat.targetTypeName == 'mapping.Bar2'
        longFormat.genericTypes[0].typeName == 'java.lang.String2'
        longFormat.genericTypes[1].typeName == 'java.lang.Boolean2'
    }

    void "reads global type mapping with format" () {
        String yaml = """\
openapi-processor-mapping: v1.0
    
map:
  types:
    - from: string:date-time
      to: java.time.ZonedDateTime
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size () == 1
        def type = mappings.first () as TypeMapping
        type.sourceTypeName == 'string'
        type.sourceTypeFormat == 'date-time'
        type.targetTypeName == 'java.time.ZonedDateTime'
        type.genericTypes == []
    }

    void "reads global response type mapping" () {
        String yaml = """\
openapi-processor-mapping: v1.0
    
map:
  responses:
    - content: application/vnd.array
      to: java.util.List
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

    void "reads endpoint response type mapping" () {
        String yaml = """\
openapi-processor-mapping: v1.0
    
map:
  paths:
    /foo:
      responses:
        - content: application/vnd.array
          to: java.util.List
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

    void "reads global parameter type mapping" () {
        String yaml = """\
openapi-processor-mapping: v1.0
    
map:
  parameters:
    - name: foo
      to: mapping.Foo
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size() == 1

        def parameter = mappings.first () as NameTypeMapping
        parameter.parameterName == 'foo'
        parameter.mapping.sourceTypeName == null
        parameter.mapping.sourceTypeFormat == null
        parameter.mapping.targetTypeName == 'mapping.Foo'
        parameter.mapping.genericTypes == []
    }

    void "reads endpoint parameter type mapping" () {
        String yaml = """\
openapi-processor-mapping: v1.0
    
map:
  paths:
    /foo:
      parameters:
        - name: foo
          to: mapping.Foo
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

    void "reads endpoint type mapping" () {
        String yaml = """\
openapi-processor-mapping: v1.0
    
map:
  paths:
    /foo:
      types:
        - from: array
          to: java.util.Collection
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size () == 1

        def endpoint = mappings.first () as EndpointTypeMapping
        endpoint.path == '/foo'
        endpoint.typeMappings.size () == 1

        def type = endpoint.typeMappings.first () as TypeMapping
        type.sourceTypeName == 'array'
        type.sourceTypeFormat == null
        type.targetTypeName == 'java.util.Collection'
        type.genericTypes == []
    }

    void "reads endpoint add mapping" () {
        String yaml = """\
openapi-processor-mapping: v1.0
    
map:
  paths:
    /foo:
      parameters:
        - add: request
          as: javax.servlet.http.HttpServletRequest
          
        - name: bar
          to: Bar
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size () == 1

        def endpoint = mappings.first () as EndpointTypeMapping
        endpoint.path == '/foo'
        endpoint.typeMappings.size () == 2

        def parameter = endpoint.typeMappings.first () as AddParameterTypeMapping
        parameter.parameterName == 'request'
        parameter.mapping.sourceTypeName == null
        parameter.mapping.sourceTypeFormat == null
        parameter.mapping.targetTypeName == 'javax.servlet.http.HttpServletRequest'
        parameter.mapping.genericTypes == []
    }

    void "reads endpoint exclude flag" () {
        String yaml = """\
openapi-processor-mapping: v1.0
    
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

    void "handles empty mapping" () {
        String yaml = ""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size() == 0
    }

    void "reads global result mapping 'plain'" () {
        String yaml = """\
openapi-processor-mapping: v1.0
    
map:
  result:
    to: plain
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size () == 1
        def type = mappings.first () as ResultTypeMapping
        type.targetTypeName == 'plain'
    }

    void "reads global result mapping" () {
        String yaml = """\
openapi-processor-mapping: v1.0
    
map:
  result:
    to: http.ResultWrapper
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size () == 1
        def type = mappings.first () as ResultTypeMapping
        type.targetTypeName == 'http.ResultWrapper'
    }

}
