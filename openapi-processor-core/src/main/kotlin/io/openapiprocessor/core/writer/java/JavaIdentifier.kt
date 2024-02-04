/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

@file:JvmName("Identifier")

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.support.capitalizeFirstChar
import io.openapiprocessor.core.writer.Identifier
import java.lang.Character.isJavaIdentifierPart
import java.lang.Character.isJavaIdentifierStart
import java.util.*
import javax.lang.model.SourceVersion
import kotlin.collections.ArrayList

class JavaIdentifier(val options: IdentifierOptions = IdentifierOptions()): Identifier {
    /**
     * converts a source string to a syntactically valid (camel case) java identifier. One way,
     * i.e. it is not reversible. It does not check if the identifier is a java keyword.
     *
     * conversion rules:
     * create camel case from word breaks. A word break is any invalid character (i.e. it is not
     * allowed in a java identifier), an underscore or an upper case letter. Invalid characters
     * are dropped.
     *
     * All words are converted to lowercase and are capitalized and joined except the first word
     * that is no capitalized. It does not handle java keywords.
     *
     * @param src the source "string"
     * @return a valid camel case java identifier
     */
    override fun toCamelCase(src: String): String {
        return joinCamelCase(splitAtWordBreaks(src))
    }

    /**
     * converts a source string to a valid (camel case) java identifier. One way, i.e. it is not
     * reversible. It adds an "a" prefix if the identifier is a java keyword (e.g. aClass instead
     * of class).
     *
     * @param src the source "string"
     * @return a valid camel case java identifier
     */
    override fun toIdentifier(src: String): String {
        val identifier = joinCamelCase(splitAtWordBreaks(src))

        if (SourceVersion.isKeyword(identifier)) {
            return joinCamelCase(splitAtWordBreaks("a_$identifier"))
        }

        return identifier
    }

    /**
     * converts a source string to a valid (camel case) java *class* identifier. One way, ie it is
     * not reversible.
     *
     * conversion rules:
     * create camel case from word breaks. A word break is any invalid character (i.e. it is not
     * allowed in a java identifier), an underscore or an upper case letter. Invalid characters
     * are dropped.
     *
     * All words are converted to lowercase and are capitalized and joined.
     *
     * @param src the source string
     *
     * @return a valid camel case java class identifier
     */
    override fun toClass(src: String): String {
        return toCamelCase(src).capitalizeFirstChar()
    }

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
    override fun toEnum(src: String): String {
        return joinEnum(splitAtWordBreaks(src))
    }

    /**
     * convert an invalid property accessor to a valid property accessor by adding an "A" prefix.
     * This is used to avoid generating a getClass() method that conflicts with Object::getClass().
     *
     * @param src the source property name without get/set prefix
     * @return a valid accessor name  without get/set prefix
     */
    override fun toMethodTail(src: String): String {
        return when (src) {
            "Class" -> "AClass"
            else -> src
        }
    }

    /**
     * joins the given words to a single camel case string.
     *
     * The first word is lower case.
     *
     * @param words a list of words
     * @return a came case string
     */
    private fun joinCamelCase(words: List<String>): String {
        val sb = StringBuilder()

        words.forEachIndexed { idx, p ->
            if (idx == 0) {
                sb.append(p.lowercase())
            } else {
                sb.append(p.lowercase().capitalizeFirstChar())
            }
        }

        if (sb.isEmpty()) {
            return "invalid"
        }

        return sb.toString()
    }

    /**
     * joins the given words to a single uppercase string separated by underscore.
     *
     * @param words a list of words
     * @return an uppercase string
     */
    private fun joinEnum(words: List<String>): String {
        val result = words.joinToString("_") { it.uppercase(Locale.getDefault()) }

        if (result.isEmpty()) {
            return "INVALID"
        }

        return result
    }

    /**
     * splits the given string at the word breaks.
     *
     * @param src the source "string"
     * @return a list of split words
     */
    private fun splitAtWordBreaks(src: String): List<String> {
        val words = ArrayList<String>()
        val current = StringBuilder()

        val trimmed = src.trimInvalidStart()
        trimmed.forEachIndexed { idx, c ->
            if (idx == 0 || !trimmed.isWordBreak(idx)) {
                current.append(c)
                return@forEachIndexed
            }

            if(current.isNotEmpty()) {
                words.add(current)
                current.clear()
            }

            current.appendValid(c)
        }

        if(current.isNotEmpty()) {
            words.add(current)
        }

        return words
    }

    private fun String.isWordBreak(idx: Int): Boolean {
        return this.isForcedBreak(idx)
            || this.isCaseBreak(idx)
    }

    private fun String.isForcedBreak(idx: Int): Boolean {
        return this[idx].isWordBreak()
    }

    // detect existing camel case word breaks
    private fun String.isCaseBreak(idx: Int): Boolean {
        if (idx == 0)
            return false

        val prev = this[idx - 1]
        val curr = this[idx]

        if (prev.isLowerCase() && curr.isUpperCase()) {
            return true
        }

        if (options.wordBreakFromDigitToLetter) {
            return prev.isDigit() && curr.isLetter()
        }

        return false
    }

    private fun Char.isWordBreak(): Boolean {
        return isWordBreakChar(this)
            || !isJavaIdentifierPart(this)
    }

    private fun isValid(c: Char): Boolean {
        return isJavaIdentifierPart(c) && !isValidWordBreak(c)
    }

    private fun isValidStart(c: Char): Boolean {
        return isJavaIdentifierStart(c) && !isValidWordBreak(c)
    }

    private fun isWordBreakChar(c: Char): Boolean {
        return isInvalidWordBreak(c) || isValidWordBreak(c)
    }

    private fun isValidWordBreak(c: Char): Boolean {
        return VALID_WORD_BREAKS.contains(c)
    }

    private fun isInvalidWordBreak(c: Char): Boolean {
        return INVALID_WORD_BREAKS.contains(c)
    }
}





private fun ArrayList<String>.add(builder: StringBuilder) {
    add(builder.toString())
}

private fun String.trimInvalidStart(): String {
    return this.trimStart {
        !isValidStart(it)
    }
}

private fun StringBuilder.appendValid(c: Char): StringBuilder {
    if (isValid(c)) {
        append(c)
    }
    return this
}

private val INVALID_WORD_BREAKS = listOf(' ', '-')
private val VALID_WORD_BREAKS = listOf('_')


private fun isValid(c: Char): Boolean {
    return isJavaIdentifierPart(c) && !isValidWordBreak(c)
}

private fun isValidStart(c: Char): Boolean {
    return isJavaIdentifierStart(c) && !isValidWordBreak(c)
}

private fun isValidWordBreak(c: Char): Boolean {
    return VALID_WORD_BREAKS.contains(c)
}
