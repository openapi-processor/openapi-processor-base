/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer

interface Identifier {
    /**
     * converts a source string to a syntactically valid (camel case) identifier. One way, i.e.
     * it is not reversible. It does not check if the identifier is a keyword.
     *
     * conversion rules:
     * create camel case from word breaks. A word break is any invalid character (i.e. it is not
     * allowed in an identifier), an underscore or an upper case letter. Invalid characters are
     * dropped.
     *
     * All words are converted to lowercase and are capitalized and joined except the first word
     * that is no capitalized. It does not handle keywords.
     *
     * @param src the source "string"
     * @return a valid camel case java identifier
     */
    fun toCamelCase(src: String): String

    /**
     * converts a source string to a valid (camel case) identifier. One way, i.e. it is not
     * reversible. It adds an "a" prefix if the identifier is a keyword (e.g. aClass instead
     * of class).
     *
     * @param src the source "string"
     * @return a valid camel case java identifier
     */
    fun toIdentifier(src: String): String

    /**
     * converts a source string to a valid (camel case) *class* identifier. One way, i.e. it is
     * not reversible.
     *
     * conversion rules:
     * create camel case from word breaks. A word break is any invalid character (i.e. it is not
     * allowed in an identifier), an underscore or an upper case letter. Invalid characters are
     * dropped.
     *
     * All words are converted to lowercase and are capitalized and joined.
     *
     * @param src the source string
     *
     * @return a valid camel case java class identifier
     */
    fun toClass(src: String): String

    /**
     * converts a source string to a valid (camel case) *class* identifier with additional suffix.
     * One way, i.e. it is not reversible.
     *
     * it delegates to [toClass] without suffix.
     */
    fun toClass(src: String, suffix: String): String

    /**
     * converts a source string to a valid (all upper case) java enum identifier. One way, ie it is
     * not reversible.
     *
     * conversion rules:
     * create camel case from word breaks. A word break is any invalid character (i.e. it is not
     * allowed in a java identifier), an underscore or an upper case letter. Invalid characters
     * are dropped.
     *
     * All words are converted to uppercase and joined by an underscore.
     *
     * @param src the source "string"
     * @return a valid upper case enum java identifier
     */
    fun toEnum(src: String): String

    /**
     * convert an invalid property accessor to a valid property accessor by adding an "A" prefix.
     * This is used to avoid generating a method name that conflicts with an existing language
     * specific method name (e.g. in java avoid a getClass() method that conflicts with
     * Object::getClass()).
     *
     * @param src the source property name without get/set prefix
     * @return a valid accessor name  without get/set prefix
     */
    fun toMethodTail(src: String): String
}
