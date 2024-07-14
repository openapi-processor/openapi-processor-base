/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.model.Api

trait ModelAsserts {
    Api api

    void assertInterfaces(String... expected) {
        def actual = api.interfaces
        assert actual.size () == expected.size ()
        assert actual.collect { it.name } == expected.collect ()
    }

    void assertEndpoints(String interfaceName, String... endpoints) {
        def itf = api.getInterface (interfaceName)
        endpoints.each { ep ->
            assert itf.getEndpoint (ep)
        }
    }

    def methodMissing(String name, args) {
        switch (name) {
            case ~/assert(.+?)Endpoints/:
                def match = name =~ /assert(.+?)Endpoints/
                def itfName = match[0][1]
                assertEndpoints (itfName as String, args as String[])
                return void
            default:
                throw new MissingMethodException(name, delegate, args)
        }
    }
}
