/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.processor.SupportedTypes
import io.openapiprocessor.core.model.datatypes.DataType
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class BeanValidationSupportedTypes(additionalSupportedTypes: SupportedTypes = mapOf()) {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    private val supported: MutableMap<String, MutableSet<SupportedType>> = supportedTargets
        .mapValues { (_, value) -> value.toMutableSet() }.toMutableMap()

    init {
        additionalSupportedTypes.forEach { (k, v) ->
            supported[k]?.addAll(v.map { AdditionalType(it) })
        }
    }

    fun supports(annotationType: String, targetDataType: DataType): Boolean {
        val targets = supported[annotationType]
        if (targets == null) {
            return false
        }

        val targetTypeName = getCanonicalName(targetDataType)
        for (target in targets) {
            val matches = target.matches(targetTypeName)
            if (matches) {
                return true
            }
        }

        //log.warn("bean validation annotation '$annotationType' is not supported on '${targetDataType.getTypeName()}'!")
        return false
    }

    private fun getCanonicalName(targetDataType: DataType): String {
        val pkg = targetDataType.getPackageName()
        val name = targetDataType.rawTypeName

        if (pkg.isEmpty()) {
            return name
        }

        return "$pkg.$name"
    }
}



interface SupportedType {
    fun matches(source: String): Boolean
}

class Type(val type: String): SupportedType {
    private var typeClass: Class<*>? = getClass(type)

    override fun matches(source: String): Boolean {
        val clazz = getClass(source)
        if (clazz != null) {
            return typeClass?.isAssignableFrom(clazz) == true
        }
        return false
    }

    private fun getClass(canonicalClass: String): Class<*>? {
        return try {
            Class.forName(canonicalClass)
        } catch (_: ClassNotFoundException) {
            null
        }
    }

    override fun toString(): String {
        return type
    }
}

class TypeAny(): SupportedType {
    val primitives = listOf("byte", "short", "int", "long", "long", "float", "double", "char")

    override fun matches(source: String): Boolean {
        return !primitives.contains(source)
    }
}

class Primitive(val type: String): SupportedType {
    override fun matches(source: String): Boolean {
        return source == type
    }

    override fun toString(): String {
        return type
    }
}

typealias AdditionalType = Primitive

class Array(): SupportedType {
    override fun matches(source: String): Boolean {
        return source.endsWith("[]")
    }

    override fun toString(): String {
        return "[]"
    }
}


private const val JSON_NULLABLE = "org.openapitools.jackson.nullable.JsonNullable"
private const val CHAR_SEQUENCE = "java.lang.CharSequence"
private const val BIG_DECIMAL = "java.math.BigDecimal"
private const val BIG_INTEGER = "java.math.BigInteger"
private const val BYTE = "java.lang.Byte"
private const val BYTE_PRIMITIVE = "byte"
private const val SHORT = "java.lang.Short"
private const val SHORT_PRIMITIVE = "short"
private const val INTEGER = "java.lang.Integer"
private const val INTEGER_PRIMITIVE = "int"
private const val LONG = "java.lang.Long"
private const val LONG_PRIMITIVE = "long"
private const val BOOLEAN = "java.lang.Boolean"
private const val BOOLEAN_PRIMITIVE = "boolean"
private const val FLOAT = "java.lang.Float"
private const val FLOAT_PRIMITIVE = "float"
private const val DOUBLE = "java.lang.Double"
private const val DOUBLE_PRIMITIVE = "double"

private val BOOLEAN_TYPES = listOf(
    Type(BOOLEAN),
    Primitive(BOOLEAN_PRIMITIVE),
    AdditionalType(JSON_NULLABLE)
)

private val NUMBER_TYPES = listOf(
    Type(BIG_DECIMAL),
    Type(BIG_INTEGER),
    Type(BYTE),
    Type(SHORT),
    Type(INTEGER),
    Type(LONG),
    Type(FLOAT),
    Type(DOUBLE),
    Primitive(BYTE_PRIMITIVE),
    Primitive(SHORT_PRIMITIVE),
    Primitive(INTEGER_PRIMITIVE),
    Primitive(LONG_PRIMITIVE),
    Primitive(FLOAT_PRIMITIVE),
    Primitive(DOUBLE_PRIMITIVE),
    AdditionalType(JSON_NULLABLE)
)

private val INTEGER_TYPES = listOf(
    Type(BIG_DECIMAL),
    Type(BIG_INTEGER),
    Type(CHAR_SEQUENCE),
    Type(BYTE),
    Type(SHORT),
    Type(INTEGER),
    Type(LONG),
    Primitive(BYTE_PRIMITIVE),
    Primitive(SHORT_PRIMITIVE),
    Primitive(INTEGER_PRIMITIVE),
    Primitive(LONG_PRIMITIVE),
    AdditionalType(JSON_NULLABLE)
)

private val LENGTH_TYPES = listOf(
    Type(CHAR_SEQUENCE),
    Type("java.util.Collection"),
    Type("java.util.Map"),
    Array(),
    AdditionalType(JSON_NULLABLE)
)

private val MAX_MIN_TYPES = listOf(
    Type(BIG_DECIMAL),
    Type(BIG_INTEGER),
    Type(BYTE),
    Type(SHORT),
    Type(INTEGER),
    Type(LONG),
    Primitive(BYTE_PRIMITIVE),
    Primitive(SHORT_PRIMITIVE),
    Primitive(INTEGER_PRIMITIVE),
    Primitive(LONG_PRIMITIVE),
    AdditionalType(JSON_NULLABLE)
)

private val EMAIL_TYPES = listOf(
    Type(CHAR_SEQUENCE),
    AdditionalType(JSON_NULLABLE)
)

private val ANY_TYPES = listOf(TypeAny())

private val PAST_TYPES = listOf(
    Type("java.util.Date"),
    Type("java.util.Calendar"),
    Type("java.time.Instant"),
    Type("java.time.LocalDate"),
    Type("java.time.LocalDateTime"),
    Type("java.time.LocalTime"),
    Type("java.time.MonthDay"),
    Type("java.time.OffsetDateTime"),
    Type("java.time.OffsetTime"),
    Type("java.time.Year"),
    Type("java.time.YearMonth"),
    Type("java.time.ZonedDateTime"),
    Type("java.time.HijrahDate"),
    Type("java.time.JapaneseDate"),
    Type("java.time.MinguoDate"),
    Type("java.time.ThaiBuddhistDate"),
    AdditionalType(JSON_NULLABLE)
)

// not all annotations are supported/used by the BeanValidationFactory
private val supportedTargets = mapOf<String, Set<SupportedType>>(
    /* javax */
//    "javax.validation.constraints.Null" to ANY_TYPES.toMutableSet(),
    "javax.validation.constraints.NotNull" to ANY_TYPES.toMutableSet(),
//    "javax.validation.constraints.AssertFalse" to BOOLEAN_TYPES.toMutableSet(),
//    "javax.validation.constraints.AssertTrue" to BOOLEAN_TYPES.toMutableSet(),
//    "javax.validation.constraints.Min" to MAX_MIN_TYPES.toMutableSet(),
//    "javax.validation.constraints.Max" to MAX_MIN_TYPES.toMutableSet(),
    "javax.validation.constraints.DecimalMin" to INTEGER_TYPES.toMutableSet(),
    "javax.validation.constraints.DecimalMax" to INTEGER_TYPES.toMutableSet(),
//    "javax.validation.constraints.Digits" to INTEGER_TYPES.toMutableSet(),
//    "javax.validation.constraints.Negative" to NUMBER_TYPES.toMutableSet(),
//    "javax.validation.constraints.NegativeOrZero" to NUMBER_TYPES.toMutableSet(),
//    "javax.validation.constraints.Positive" to NUMBER_TYPES.toMutableSet(),
//    "javax.validation.constraints.PositiveOrZero" to NUMBER_TYPES.toMutableSet(),
    "javax.validation.constraints.Size" to LENGTH_TYPES.toMutableSet(),
    "javax.validation.constraints.Email" to EMAIL_TYPES.toMutableSet(),
    "javax.validation.constraints.Pattern" to EMAIL_TYPES.toMutableSet(),
//    "javax.validation.constraints.Past" to PAST_TYPES.toMutableSet(),
//    "javax.validation.constraints.PastOrPresent" to PAST_TYPES.toMutableSet(),

    /* jakarta */
//    "jakarta.validation.constraints.Null" to ANY_TYPES.toMutableSet(),
    "javax.validation.constraints.NotNull" to ANY_TYPES.toMutableSet(),
//    "jakarta.validation.constraints.AssertFalse" to BOOLEAN_TYPES.toMutableSet(),
//    "jakarta.validation.constraints.AssertTrue" to BOOLEAN_TYPES.toMutableSet(),
//    "jakarta.validation.constraints.Min" to MAX_MIN_TYPES.toMutableSet(),
//    "jakarta.validation.constraints.Max" to MAX_MIN_TYPES.toMutableSet(),
    "jakarta.validation.constraints.DecimalMin" to INTEGER_TYPES.toMutableSet(),
    "jakarta.validation.constraints.DecimalMax" to INTEGER_TYPES.toMutableSet(),
//    "javax.validation.constraints.Digits" to INTEGER_TYPES.toMutableSet(),
//    "jakarta.validation.constraints.Negative" to NUMBER_TYPES.toMutableSet(),
//    "jakarta.validation.constraints.NegativeOrZero" to NUMBER_TYPES.toMutableSet(),
//    "jakarta.validation.constraints.Positive" to NUMBER_TYPES.toMutableSet(),
//    "jakarta.validation.constraints.PositiveOrZero" to NUMBER_TYPES.toMutableSet(),
    "jakarta.validation.constraints.Size" to LENGTH_TYPES.toMutableSet(),
    "jakarta.validation.constraints.Email" to EMAIL_TYPES.toMutableSet(),
    "jakarta.validation.constraints.Pattern" to EMAIL_TYPES.toMutableSet(),
//    "jakarta.validation.constraints.Past" to PAST_TYPES.toMutableSet(),
//    "jakarta.validation.constraints.PastOrPresent" to PAST_TYPES.toMutableSet(),
)
