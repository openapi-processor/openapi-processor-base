/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.support.TestMappingAnnotationWriter
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.mapping.SimpleParameterValue
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.model.datatypes.*
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.model.parameters.ParameterBase
import io.openapiprocessor.core.support.TestParameterAnnotationWriter
import io.openapiprocessor.core.support.TestStatusAnnotationWriter
import io.openapiprocessor.core.support.datatypes.ObjectDataType
import spock.lang.Specification
import spock.lang.Unroll

import static io.openapiprocessor.core.builder.api.EndpointBuilderKt.endpoint

class MethodWriterGSpec extends Specification {
    def apiOptions = new ApiOptions()
    def identifier = new JavaIdentifier()

    def writer = new MethodWriter (
        apiOptions,
        identifier,
        new TestStatusAnnotationWriter(),
        new TestMappingAnnotationWriter(),
        new TestParameterAnnotationWriter(),
        Stub (BeanValidationFactory),
        Stub (JavaDocWriter))
    def target = new StringWriter ()

    void "writes mapping annotation" () {
        def endpoint = endpoint('/foo', HttpMethod.GET) { e ->
            e.responses { r ->
                r.status ('204') {it.empty () }
            }
        }

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @Status
    @CoreMapping
    void getFoo();
"""
    }

    void "writes @Deprecated annotation" () {
        def endpoint = endpoint ('/foo', HttpMethod.GET) {e ->
            e.deprecated ()
            e.responses {r ->
                r.status ('204') {it.empty () }
            }
        }

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @Deprecated
    @Status
    @CoreMapping
    void getFoo();
"""
    }

    @Unroll
    void "writes simple data type response (#type)" () {
        def endpoint = endpoint ("/$type", HttpMethod.GET) {e ->
            e.responses { rs ->
                rs.status ('200') { r ->
                    r.response ('text/plain', responseType)
                }
            }
        }

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
        def endpoint = endpoint ("/foo", HttpMethod.GET) {e ->
            e.responses { rs ->
                rs.status ('200') { r ->
                    r.response ('application/json',
                        new ObjectDataType (
                            'InlineObjectResponse', '', [
                            foo1: new StringDataType (),
                            foo2: new StringDataType ()
                        ] as LinkedHashMap,
                        null, false, null)) {}
                }
            }
        }

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    InlineObjectResponse getFoo();
"""
    }

    void "writes method with Collection response type" () {
        def endpoint = endpoint ("/foo", HttpMethod.GET) {e ->
            e.responses { rs ->
                rs.status ('200') { r ->
                    r.response ('application/json', collection)
                }
            }
        }

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    ${response} getFoo();
"""

        where:
        collection                                                                          | response
        new ArrayDataType (new StringDataType (), null, false)                              | 'String[]'
        new MappedCollectionDataType ('List', '', new StringDataType (), null, false, null, false) | 'List<String>'
        new MappedCollectionDataType ('Set', '', new StringDataType (), null, false, null, false)  | 'Set<String>'
    }

    void "writes parameter annotation" () {
        def endpoint = endpoint ("/foo", HttpMethod.GET) {e ->
            e.responses { rs ->
                rs.status ('204') { r -> r.empty () }
            }
            e.parameters { ps ->
                ps.any (new ParameterBase (
                    'foo', new StringDataType ("string", null, false, null),
                    true,
                    false,
                    null) {})
            }
        }

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @Status
    @CoreMapping
    void getFoo(@Parameter String foo);
"""
    }

    void "writes no parameter annotation if the annotation writer skips it" () {
        def writer = new MethodWriter (
            apiOptions,
            identifier,
            new TestStatusAnnotationWriter(),
            new TestMappingAnnotationWriter(),
            Stub (ParameterAnnotationWriter) {},
            Stub (BeanValidationFactory),
            Stub (JavaDocWriter))

        def endpoint = endpoint ("/foo", HttpMethod.GET) {e ->
            e.responses { rs ->
                rs.status ('204') { r -> r.empty () }
            }
            e.parameters { ps -> ps.any (
                Stub (Parameter) {
                    getName () >> 'foo'
                    getDataType () >> new StringDataType ()
                })
            }
        }

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @Status
    @CoreMapping
    void getFoo(String foo);
"""
    }

    void "writes additional parameter annotation" () {
        def endpoint = endpoint ("/foo", HttpMethod.GET) {e ->
            e.responses { rs ->
                rs.status ('204') { r -> r.empty () }
            }
            e.parameters { ps ->
                ps.add ('foo', new StringDataType()) { a ->
                    a.annotation = new AnnotationDataType ('Foo', 'oap', [:])
                }
            }
        }

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @Status
    @CoreMapping
    void getFoo(@Parameter @Foo String foo);
"""
    }

    void "writes additional parameter annotation with default parameter" () {
        def endpoint = endpoint ("/foo", HttpMethod.GET) {e ->
            e.responses { rs ->
                rs.status ('204') { r -> r.empty () }
            }
            e.parameters { ps ->
                ps.add ('foo', new StringDataType()) { a ->
                    a.annotation = new AnnotationDataType ('Foo', 'oap', [
                        "": new SimpleParameterValue('"bar"', null)
                    ])
                }
            }
        }

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @Status
    @CoreMapping
    void getFoo(@Parameter @Foo("bar") String foo);
"""
    }

    void "writes additional parameter annotation with named parameter" () {
        def endpoint = endpoint ("/foo", HttpMethod.GET) {e ->
            e.responses { rs ->
                rs.status ('204') { r -> r.empty () }
            }
            e.parameters { ps ->
                ps.add ('foo', new StringDataType()) { a ->
                    a.annotation = new AnnotationDataType ('Foo', 'oap', [
                        foo: new SimpleParameterValue('"bar"', null),
                        oof: new SimpleParameterValue('"rab"', null)
                    ])
                }
            }
        }

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @Status
    @CoreMapping
    void getFoo(@Parameter @Foo(foo = "bar", oof = "rab") String foo);
"""
    }

    void "writes method name from path with valid java identifiers" () {
        def endpoint = endpoint ('/f_o-ooo/b_a-rrr', HttpMethod.GET) {e ->
            e.responses {r ->
                r.status ('204') {it.empty () }
            }
        }

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @Status
    @CoreMapping
    void getFOOooBARrr();
"""
    }

    void "writes method name from operation id with valid java identifiers" () {
        def endpoint = endpoint ('/foo', HttpMethod.GET) {e ->
            e.operationId = 'get-bar'
            e.responses {r ->
                r.status ('204') {it.empty () }
            }
        }

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @Status
    @CoreMapping
    void getBar();
"""
    }

    void "writes method parameter with valid java identifiers" () {
        def endpoint = endpoint ("/foo", HttpMethod.GET) {e ->
            e.responses { rs ->
                rs.status ('204') { r -> r.empty () }
            }
            e.parameters { ps ->
                ps.query ('_fo-o', new StringDataType()) { q ->
                    q.required = true
                    q.deprecated = false
                }
            }
        }

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @Status
    @CoreMapping
    void getFoo(@Parameter String foO);
"""
    }

    void "writes method with void response type wrapped by result wrapper" () {
        def endpoint = endpoint ("/foo", HttpMethod.GET) {e ->
            e.responses { rs ->
                rs.status ('204') { r ->
                    r.response ('', new ResultDataType (
                        'ResultWrapper',
                        'http',
                        new NoneDataType ().wrappedInResult (),
                        []
                    ))
                }
            }
        }

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @Status
    @CoreMapping
    ResultWrapper<Void> getFoo();
"""
    }

}
