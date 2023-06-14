/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import spock.lang.Specification
import spock.lang.Unroll

class IdentifierSpec extends Specification {

    @Unroll
    void "convert source string '#src' to valid identifiers: #identifier/#clazz/#enumn" () {
        expect:
        Identifier.toCamelCase (src) == camelCase
        Identifier.toIdentifier (src) == identifier
        Identifier.toClass (src) == clazz
        Identifier.toEnum (src) == enumn

        where:
        src              | camelCase      | identifier     | clazz          | enumn

        // first char should be lowercase
        "a"              | "a"            | "a"            | "A"            | "A"
        "A"              | "a"            | "a"            | "A"            | "A"
        "AA"             | "aa"           | "aa"           | "Aa"           | "AA"

        // invalid chars are stripped
        "1a"             | "a"            | "a"            | "A"            | "A"
        "2345a"          | "a"            | "a"            | "A"            | "A"

        // word break at invalid character
        "a foo"          | "aFoo"         | "aFoo"         | "AFoo"         | "A_FOO"
        "a-foo"          | "aFoo"         | "aFoo"         | "AFoo"         | "A_FOO"
        "FOO-bar"        | "fooBar"       | "fooBar"       | "FooBar"       | "FOO_BAR"
        "a foo bar"      | "aFooBar"      | "aFooBar"      | "AFooBar"      | "A_FOO_BAR"
        "a-foo-bar"      | "aFooBar"      | "aFooBar"      | "AFooBar"      | "A_FOO_BAR"
        "a foo-bar"      | "aFooBar"      | "aFooBar"      | "AFooBar"      | "A_FOO_BAR"
        'api/some/thing' | 'apiSomeThing' | 'apiSomeThing' | "ApiSomeThing" | "API_SOME_THING"
        "_fo-o"          | 'foO'          | 'foO'          | 'FoO'          | "FO_O"

        // word break at underscore, it is||valid but unwanted except for enums
        "_ab"            | "ab"           | "ab"           | "Ab"           | "AB"
        "a_b"            | "aB"           | "aB"           | "AB"           | "A_B"
        "a_foo"          | "aFoo"         | "aFoo"         | "AFoo"         | "A_FOO"
        "A_A"            | "aA"           | "aA"           | "AA"           | "A_A"
        "FOO_FOO"        | "fooFoo"       | "fooFoo"       | "FooFoo"       | "FOO_FOO"

        // word break at case change: lowe|| to upper, preserve camel case
        "fooBar"         | "fooBar"       | "fooBar"       | "FooBar"       | "FOO_BAR"
        "fooBAr"         | "fooBar"       | "fooBar"       | "FooBar"       | "FOO_BAR"
        "fooBAR"         | "fooBar"       | "fooBar"       | "FooBar"       | "FOO_BAR"

        // final result is empty
        " "              | "invalid"      | "invalid"      | "Invalid"      | "INVALID"
        "_"              | "invalid"      | "invalid"      | "Invalid"      | "INVALID"
        "-"              | "invalid"      | "invalid"      | "Invalid"      | "INVALID"

        // identifier handles keywords
        "class"          | "class"        | "aClass"       | "Class"        | "CLASS"
        "public"         | "public"       | "aPublic"      | "Public"       | "PUBLIC"
        "final"          | "final"        | "aFinal"       | "Final"        | "FINAL"
    }
}
