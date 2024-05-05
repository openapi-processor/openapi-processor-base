/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.support.capitalizeFirstChar
import io.openapiprocessor.core.writer.Identifier
import java.lang.Character.isJavaIdentifierPart
import java.lang.Character.isJavaIdentifierStart
import java.util.*
import javax.lang.model.SourceVersion
import kotlin.collections.ArrayList


private val INVALID_WORD_PREFIX = "v"
private val INVALID_WORD_BREAKS = listOf(' ', '-')
private val VALID_WORD_BREAKS = listOf('_')

class JavaIdentifier(val options: IdentifierOptions = IdentifierOptions()): Identifier {

    override fun toCamelCase(src: String): String {
        return joinCamelCase(splitAtWordBreaks(src))
    }

    override fun toIdentifier(src: String): String {
        val identifier = joinCamelCase(splitAtWordBreaks(src))

        if (SourceVersion.isKeyword(identifier)) {
            return joinCamelCase(splitAtWordBreaks("a_$identifier"))
        }

        return identifier
    }

    override fun toClass(src: String): String {
        return toCamelCase(src).capitalizeFirstChar()
    }

    override fun toClass(src: String, suffix: String): String {
        val classTypeName = toClass(src)

        if (src.isEmpty())
            return classTypeName

        if (src.endsWith(suffix.capitalizeFirstChar()))
            return classTypeName

        return "$classTypeName${suffix.capitalizeFirstChar()}"
    }

    override fun toEnum(src: String): String {
        return joinEnum(splitAtWordBreaks(src))
    }

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

        var trimmed = src.trimInvalidStart()
        if (trimmed.isEmpty()) {
            trimmed = "$INVALID_WORD_PREFIX$src"
        }

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

//        if(words.isEmpty()) {
//            words.add(INVALID_WORD_PREFIX)
//            words.add(src)
//        }

        return words
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

    private fun ArrayList<String>.add(builder: StringBuilder) {
        add(builder.toString())
    }

    private fun String.isWordBreak(idx: Int): Boolean {
        return isForcedBreak(idx)
            || isCaseBreak(idx)
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

    private fun String.trimInvalidStart(): String {
        return trimStart {
            !isValidStart(it)
        }
    }

    private fun StringBuilder.appendValid(c: Char): StringBuilder {
        if (isValid(c)) {
            append(c)
        }
        return this
    }

    private fun Char.isWordBreak(): Boolean {
        return isWordBreakChar(this)
            || !isJavaIdentifierPart(this)
    }
}
