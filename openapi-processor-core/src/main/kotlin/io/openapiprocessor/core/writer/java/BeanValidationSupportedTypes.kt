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

private val BOOLEAN_TYPES = listOf(
    Type("java.lang.Boolean"),
    Primitive("boolean"),
    AdditionalType("org.openapitools.jackson.nullable.JsonNullable")
)

private val NUMBER_TYPES = listOf(
    Type("java.math.BigDecimal"),
    Type("java.math.BigInteger"),
    Type("java.lang.Byte"),
    Type("java.lang.Short"),
    Type("java.lang.Integer"),
    Type("java.lang.Long"),
    Type("java.lang.Float"),
    Type("java.lang.Double"),
    Primitive("byte"),
    Primitive("short"),
    Primitive("int"),
    Primitive("long"),
    Primitive("float"),
    Primitive("double"),
    AdditionalType("org.openapitools.jackson.nullable.JsonNullable")
)

private val INTEGER_TYPES = listOf(
    Type("java.math.BigDecimal"),
    Type("java.math.BigInteger"),
    Type("java.lang.CharSequence"),
    Type("java.lang.Byte"),
    Type("java.lang.Short"),
    Type("java.lang.Integer"),
    Type("java.lang.Long"),
    Primitive("byte"),
    Primitive("short"),
    Primitive("int"),
    Primitive("long"),
    AdditionalType("org.openapitools.jackson.nullable.JsonNullable")
)

private val LENGTH_TYPES = listOf(
    Type("java.lang.CharSequence"),
    Type("java.util.Collection"),
    Type("java.util.Map"),
    Array(),
    AdditionalType("org.openapitools.jackson.nullable.JsonNullable")
)

private val MAX_MIN_TYPES = listOf(
    Type("java.math.BigDecimal"),
    Type("java.math.BigInteger"),
    Type("java.lang.Byte"),
    Type("java.lang.Short"),
    Type("java.lang.Integer"),
    Type("java.lang.Long"),
    Primitive("byte"),
    Primitive("short"),
    Primitive("int"),
    Primitive("long"),
    AdditionalType("org.openapitools.jackson.nullable.JsonNullable")
)

private val EMAIL_TYPES = listOf(
    Type("java.lang.CharSequence"),
    AdditionalType("org.openapitools.jackson.nullable.JsonNullable")
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
    AdditionalType("org.openapitools.jackson.nullable.JsonNullable")
)

// not all annotation are supported/used by the BeanValidationFactory
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
