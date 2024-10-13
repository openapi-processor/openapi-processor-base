/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor

import io.openapiprocessor.core.converter.mapping.steps.EndpointsStep
import io.openapiprocessor.core.converter.mapping.steps.GlobalsStep
import spock.lang.Specification
import spock.lang.Subject

import static io.openapiprocessor.core.support.MappingQueryKt.query
import static io.openapiprocessor.core.support.MatcherKt.responseTypeMatcher

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
        def mappingData = converter.convert (reader.read (yaml))

        then:
        def response = mappingData.globalMappings.findContentTypeMapping(
                responseTypeMatcher(
                        null,
                        null,
                        null, null, null,
                        "application/vnd.array",
                        false,
                        false),
                new GlobalsStep())

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
        def mappingData = converter.convert (reader.read (yaml))

        then:
        def endpoint = mappingData.endpointMappings["/foo"]
        def excluded = endpoint.isExcluded(query(
                "/foo",
                null,
                null,
                null,
                null,
                null,
                false,
                false))

        excluded == exclude

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
        def mappingData = converter.convert (reader.read (yaml))

        then:
        def query = query(
                "/foo",
                null,
                "foo",
                null,
                null,
                null,
                false,
                false)
        def endpoint = mappingData.endpointMappings["/foo"]
        def parameter = endpoint.findParameterNameTypeMapping(query, new EndpointsStep(query))

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
        def mappingData = converter.convert (reader.read (yaml))

        then:
        def query = query(
                        "/foo",
                        null,
                        null,
                        null,
                        null,
                        null,
                        false,
                        false)
        def endpoint = mappingData.endpointMappings["/foo"]
        def parameters = endpoint.findAddParameterTypeMappings(
                query, new EndpointsStep(query))

        def parameter = parameters[0]
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
        def mappingData = converter.convert (reader.read (yaml))

        then:
        def query = query(
                        "/foo",
                        null,
                        null,
                        null,
                        null,
                        null,
                        false,
                        false)
        def endpoint = mappingData.endpointMappings["/foo"]
        def parameters = endpoint.findAddParameterTypeMappings(
                query, new EndpointsStep(query))

        def parameter = parameters[0]
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
        def mappingData = converter.convert (reader.read (yaml))

        then:
        def query = query(
                        "/foo",
                        null,
                        null,
                        "application/vnd.array",
                        null,
                        null,
                        false,
                        false)
        def endpoint = mappingData.endpointMappings["/foo"]
        def response = endpoint.findContentTypeMapping(query, new EndpointsStep(query))

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
        def mappingData = converter.convert (reader.read (yaml))

        then:
        def endpoint = mappingData.endpointMappings["/foo"]
        def type = endpoint.getResultTypeMapping(query(
                "/foo",
                null,
                null,
                null,
                null,
                null,
                false,
                false
        ))

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
        def mappingData = converter.convert (reader.read (yaml))
        def endpoint = mappingData.endpointMappings["/foo"]

        then:
        def typeSingle = endpoint.getSingleTypeMapping(query(
                "/foo",
                null,
                null,
                null,
                null,
                null,
                false,
                false
        ))
        typeSingle.sourceTypeName == 'single'
        typeSingle.targetTypeName == single

        def typeMulti = endpoint.getMultiTypeMapping(query(
                "/foo",
                null,
                null,
                null,
                null,
                null,
                false,
                false
        ))
        typeMulti.sourceTypeName == 'multi'
        typeMulti.targetTypeName == multi

        where:
        single << ['reactor.core.publisher.Mono']
        multi << ['reactor.core.publisher.Flux']
    }
}
