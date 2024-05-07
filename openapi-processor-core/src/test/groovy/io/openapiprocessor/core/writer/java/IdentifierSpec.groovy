/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import spock.lang.Specification
import spock.lang.Unroll

class IdentifierSpec extends Specification {

    void "add prefix to single invalid identifier"() {
        def convert = new JavaIdentifier(new IdentifierOptions(wbfdtl))

        expect:
        convert.toCamelCase(src) == camelCase
        convert.toIdentifier(src) == identifier
        convert.toClass(src) == clazz
        convert.toEnum(src) == enumn

        where:
        src  | camelCase | identifier | clazz | enumn | wbfdtl
        "1"  | "v1"      | "v1"       | "V1"  | "V1"  | true
        "11" | "v11"     | "v11"      | "V11" | "V11" | true
        "1"  | "v1"      | "v1"       | "V1"  | "V1"  | false
        "11" | "v11"     | "v11"      | "V11" | "V11" | false
    }

    void "recognize word break if a digit is followed by a letter" () {
        def convert = new JavaIdentifier(new IdentifierOptions(true))

        expect:
        convert.toCamelCase(src) == camelCase
        convert.toIdentifier(src) == identifier
        convert.toClass(src) == clazz
        convert.toEnum(src) == enumn

        where:
        src       | camelCase | identifier | clazz     | enumn
        "foo2Bar" | "foo2Bar" | "foo2Bar"  | "Foo2Bar" | "FOO2_BAR"
    }

    void "ignore word break if a digit is followed by a letter" () {
        def convert = new JavaIdentifier(new IdentifierOptions(false))

        expect:
        convert.toCamelCase(src) == camelCase
        convert.toIdentifier(src) == identifier
        convert.toClass(src) == clazz
        convert.toEnum(src) == enumn

        where:
        src       | camelCase | identifier | clazz     | enumn
        "foo2Bar" | "foo2bar" | "foo2bar"  | "Foo2bar" | "FOO2BAR"
    }

    @Unroll
    void "convert source string '#src' to valid identifiers: #identifier/#clazz/#enumn" () {
        def convert = new JavaIdentifier(new IdentifierOptions(true))

        expect:
        convert.toCamelCase (src) == camelCase
        convert.toIdentifier (src) == identifier
        convert.toClass (src) == clazz
        convert.toEnum (src) == enumn

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

        // word break at underscore, it is valid but unwanted except for enums
        "_ab"            | "ab"           | "ab"           | "Ab"           | "AB"
        "a_b"            | "aB"           | "aB"           | "AB"           | "A_B"
        "a_foo"          | "aFoo"         | "aFoo"         | "AFoo"         | "A_FOO"
        "A_A"            | "aA"           | "aA"           | "AA"           | "A_A"
        "FOO_FOO"        | "fooFoo"       | "fooFoo"       | "FooFoo"       | "FOO_FOO"

        // word break at case change: lower to upper, preserve camel case
        "fooBar"         | "fooBar"       | "fooBar"       | "FooBar"       | "FOO_BAR"
        "fooBAr"         | "fooBar"       | "fooBar"       | "FooBar"       | "FOO_BAR"
        "fooBAR"         | "fooBar"       | "fooBar"       | "FooBar"       | "FOO_BAR"

        // final result is empty, i.e. it gets replaced by the "invalid" prefix
        " "              | "v"            | "v"            | "V"            | "V"
        "_"              | "v"            | "v"            | "V"            | "V"
        "-"              | "v"            | "v"            | "V"            | "V"

        // identifier handles keywords
        "class"          | "class"        | "aClass"       | "Class"        | "CLASS"
        "public"         | "public"       | "aPublic"      | "Public"       | "PUBLIC"
        "final"          | "final"        | "aFinal"       | "Final"        | "FINAL"

        // word break on last digit when the next character is a letter
        "foo2bar"        | "foo2Bar"         | "foo2Bar"   | "Foo2Bar"      | "FOO2_BAR"
        "foo2Bar"        | "foo2Bar"         | "foo2Bar"   | "Foo2Bar"      | "FOO2_BAR"
        "foo22Bar"       | "foo22Bar"        | "foo22Bar"  | "Foo22Bar"     | "FOO22_BAR"
    }

    void "adds suffix to class name" () {
        def convert = new JavaIdentifier(new IdentifierOptions(true))

        expect:
        convert.toClass("foo", "X") == "FooX"
    }

    void "ignores suffix if class name already ends with the suffix"() {
        def convert = new JavaIdentifier(new IdentifierOptions(true))

        expect:
        convert.toClass("FooX", "X") == "FooX"
    }
}
