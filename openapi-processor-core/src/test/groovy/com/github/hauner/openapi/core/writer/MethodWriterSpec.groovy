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

package com.github.hauner.openapi.core.writer

import com.github.hauner.openapi.core.model.parameters.ParameterBase
import com.github.hauner.openapi.core.support.TestMappingAnnotationWriter
import com.github.hauner.openapi.core.support.TestParameterAnnotationWriter
import com.github.hauner.openapi.spring.converter.ApiOptions
import com.github.hauner.openapi.spring.model.Endpoint
import com.github.hauner.openapi.spring.model.HttpMethod
import com.github.hauner.openapi.spring.model.Response
import com.github.hauner.openapi.spring.model.datatypes.ArrayDataType
import com.github.hauner.openapi.spring.model.datatypes.BooleanDataType
import com.github.hauner.openapi.spring.model.datatypes.DataType
import com.github.hauner.openapi.spring.model.datatypes.DoubleDataType
import com.github.hauner.openapi.spring.model.datatypes.FloatDataType
import com.github.hauner.openapi.spring.model.datatypes.IntegerDataType
import com.github.hauner.openapi.spring.model.datatypes.LongDataType
import com.github.hauner.openapi.spring.model.datatypes.MappedCollectionDataType
import com.github.hauner.openapi.spring.model.datatypes.NoneDataType
import com.github.hauner.openapi.spring.model.datatypes.ObjectDataType
import com.github.hauner.openapi.spring.model.datatypes.StringDataType
import spock.lang.Specification
import spock.lang.Unroll

class MethodWriterSpec extends Specification {
    def apiOptions = new ApiOptions()

    def writer = new MethodWriter (
        apiOptions: apiOptions,
        mappingAnnotationWriter: new TestMappingAnnotationWriter(),
        parameterAnnotationWriter: new TestParameterAnnotationWriter())
    def target = new StringWriter ()

    private Endpoint createEndpoint (Map properties) {
        new Endpoint(properties).initEndpointResponses ()
    }

    void "writes mapping annotation" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '204': [new Response(responseType: new NoneDataType())]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    void getFoo();
"""
    }

    @Unroll
    void "writes simple data type response (#type)" () {
        def endpoint = createEndpoint (path: "/$type", method: HttpMethod.GET, responses: [
            '200': [new Response(contentType: 'text/plain', responseType: responseType)]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    ${type.capitalize ()} get${type.capitalize ()}();
"""

        where:
        type      | responseType
        'string'  | new StringDataType ()
        'integer' | new IntegerDataType ()
        'long'    | new LongDataType ()
        'float'   | new FloatDataType ()
        'double'  | new DoubleDataType ()
        'boolean' | new BooleanDataType ()
    }

    void "writes inline object data type response" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '200': [
                new Response (contentType: 'application/json',
                    responseType: new ObjectDataType (
                        type: 'InlineObjectResponse', properties: [
                        foo1: new StringDataType (),
                        foo2: new StringDataType ()
                    ]))
            ]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    InlineObjectResponse getFoo();
"""
    }

    void "writes method with Collection response type" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '200': [
                new Response (contentType: 'application/json', responseType: collection)
            ]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    ${response} getFoo();
"""

        where:
        collection                                                               | response
        new ArrayDataType (item: new StringDataType ())                          | 'String[]'
        new MappedCollectionDataType (type: 'List', item: new StringDataType ()) | 'List<String>'
        new MappedCollectionDataType (type: 'Set', item: new StringDataType ())  | 'Set<String>'
    }

    void "writes parameter annotation" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '204': [new Response (responseType: new NoneDataType ())]
        ], parameters: [
            new ParameterBase () {
                String getName () {
                    "foo"
                }

                DataType getDataType () {
                    new StringDataType ()
                }

                @Override
                boolean isRequired () {
                    true
                }
            }
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    void getFoo(@Parameter String foo);
"""
    }

    void "does not write parameter annotation if not wanted" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '204': [new Response (responseType: new NoneDataType ())]
        ], parameters: [
            new ParameterBase () {
                String getName () {
                    "foo"
                }

                DataType getDataType () {
                    new StringDataType ()
                }

                boolean withAnnotation () {
                    false
                }
            }
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    void getFoo(String foo);
"""
    }

}