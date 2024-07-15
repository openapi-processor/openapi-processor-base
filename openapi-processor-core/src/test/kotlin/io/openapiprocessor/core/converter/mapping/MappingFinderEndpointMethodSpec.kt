/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.parser.HttpMethod.GET
import io.openapiprocessor.core.parser.HttpMethod.PATCH
import io.openapiprocessor.core.support.mappingFinder
import io.openapiprocessor.core.support.parseOptions
import io.openapiprocessor.core.support.query

class MappingFinderEndpointMethodSpec: StringSpec({

    "endpoint/method type mapping matches single mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            | paths:
            |   /foo:
            |     get:
            |       types:
            |         - type: Foo => io.openapiprocessor.Foo
            |         - type: Far => io.openapiprocessor.Far
            |         - type: Bar => io.openapiprocessor.Bar
            """)

        val finder = mappingFinder(options)
        val result = finder.findTypeMapping(query(path = "/foo", method = GET, name = "Foo"))

        result.shouldNotBeNull()
        result.sourceTypeName.shouldBe("Foo")
        result.targetTypeName.shouldBe("io.openapiprocessor.Foo")
    }

    "endpoint/method parameter mapping matches single mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            | paths:
            |   /foo:
            |     get:
            |       parameters:
            |         - name: fooParam => io.openapiprocessor.Foo
            |         - name: farParam => io.openapiprocessor.Far
            |         - name: barParam => io.openapiprocessor.Bar
            """)

        val finder = mappingFinder(options)
        val result = finder.findParameterNameTypeMapping(query(path = "/foo", method = GET, name = "farParam"))

        result.shouldNotBeNull()
        result.parameterName.shouldBe("farParam")
        result.mapping.sourceTypeName.shouldBeNull()
        result.mapping.targetTypeName.shouldBe("io.openapiprocessor.Far")
    }

    "endpoint/method response mapping matches single mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            | paths:
            |   /foo:
            |     get:
            |       responses:
            |         - content: application/json => io.openapiprocessor.Foo
            |         - content: application/json-2 => io.openapiprocessor.Far
            |         - content: application/json-3 => io.openapiprocessor.Bar
            """)

        val finder = mappingFinder(options)
        val result = finder.findContentTypeMapping(query(path = "/foo", method = GET, contentType = "application/json"))

        result.shouldNotBeNull()
        result.contentType.shouldBe("application/json")
        result.mapping.sourceTypeName.shouldBeNull()
        result.mapping.targetTypeName.shouldBe("io.openapiprocessor.Foo")
    }

    "endpoint type mapping matches null mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            | paths:
            |   /foo:
            |     patch:
            |       null: org.openapitools.jackson.nullable.JsonNullable
            |       types:
            |         - type: Far => io.openapiprocessor.Foo
            |         - type: Bar => io.openapiprocessor.Far
            """)

        val finder = mappingFinder(options)
        val result = finder.findNullTypeMapping(query(path = "/foo", method = PATCH))

        result.shouldNotBeNull()
        result.sourceTypeName.shouldBe("null")
        result.targetTypeName.shouldBe("org.openapitools.jackson.nullable.JsonNullable")
    }

    "endpoint/method add parameter mapping matches" {
        val options = parseOptions(mapping =
            """
            |map:
            | paths:
            |   /foo:
            |     get:
            |       parameters:
            |         - add: fooParam => io.openapiprocessor.Foo
            |         - add: barParam => io.openapiprocessor.Foo
            """)

        val finder = mappingFinder(options)
        val result = finder.findAddParameterTypeMappings(query(path = "/foo", method = GET))

        result.shouldNotBeEmpty()
        result[0].parameterName.shouldBe("fooParam")
        result[1].parameterName.shouldBe("barParam")
    }

    "endpoint/method result mapping matches" {
        val options = parseOptions(mapping =
            """
            |map:
            | paths:
            |   /foo:
            |     get:
            |       result: io.openapiprocessor.ResultWrapper
            """)

        val finder = mappingFinder(options)
        val result = finder.getResultTypeMapping(query(path = "/foo", method = GET))

        result.shouldNotBeNull()
        result.targetTypeName.shouldBe("io.openapiprocessor.ResultWrapper")
    }

    "endpoint/method single mapping matches" {
        val options = parseOptions(mapping =
            """
            |map:
            | paths:
            |   /foo:
            |     get:
            |       single: io.openapiprocessor.SingleWrapper
            """)

        val finder = mappingFinder(options)
        val result = finder.getSingleTypeMapping(query(path = "/foo", method = GET))

        result.shouldNotBeNull()
        result.targetTypeName.shouldBe("io.openapiprocessor.SingleWrapper")
    }

    "endpoint single mapping matches" {
        val options = parseOptions(mapping =
            """
            |map:
            | paths:
            |   /foo:
            |     single: io.openapiprocessor.SingleWrapper
            """)

        val finder = mappingFinder(options)
        val result = finder.getSingleTypeMapping(query(path = "/foo", method = GET))

        result.shouldNotBeNull()
        result.targetTypeName.shouldBe("io.openapiprocessor.SingleWrapper")
    }

    "endpoint/method multi mapping matches" {
        val options = parseOptions(mapping =
            """
            |map:
            | paths:
            |   /foo:
            |     get:
            |       multi: io.openapiprocessor.MultiWrapper
            """)

        val finder = mappingFinder(options)
        val result = finder.getMultiTypeMapping(query(path = "/foo", method = GET))

        result.shouldNotBeNull()
        result.targetTypeName.shouldBe("io.openapiprocessor.MultiWrapper")
    }

    "endpoint multi mapping matches" {
        val options = parseOptions(mapping =
            """
            |map:
            | paths:
            |   /foo:
            |     multi: io.openapiprocessor.MultiWrapper
            """)

        val finder = mappingFinder(options)
        val result = finder.getMultiTypeMapping(query(path = "/foo", method = GET))

        result.shouldNotBeNull()
        result.targetTypeName.shouldBe("io.openapiprocessor.MultiWrapper")
    }

    "endpoint/method exclude" {
        val options = parseOptions(mapping =
            """
            |map:
            | paths:
            |   /foo:
            |     get:
            |       exclude: true
            """)

        val finder = mappingFinder(options)
        val result = finder.isEndpointExcluded(query(path = "/foo", method = GET))

        result.shouldBeTrue()
    }

    "endpoint exclude" {
        val options = parseOptions(mapping =
            """
            |map:
            | paths:
            |   /foo:
            |     exclude: true
            """)

        val finder = mappingFinder(options)
        val result = finder.isEndpointExcluded(query(path = "/foo", method = GET))

        result.shouldBeTrue()
    }

    "endpoint & endpoint/method exclude if any is true" {
        val options = parseOptions(mapping =
            """
            |map:
            | paths:
            |   /foo:
            |     exclude: true
            |     get:
            |       exclude: false
            """)

        val finder = mappingFinder(options)
        val result = finder.isEndpointExcluded(query(path = "/foo", method = GET))

        result.shouldBeTrue()
    }

})
