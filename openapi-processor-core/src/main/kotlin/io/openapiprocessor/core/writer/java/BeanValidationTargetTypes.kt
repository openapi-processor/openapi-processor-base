/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.GenericDataType
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class BeanValidationTargetTypes {
    var log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    fun supports(annotationType: String, targetDataType: DataType): Boolean {
        val supported = supportedTargets[annotationType]
        if (supported == null) {
            return false
        }

        val targetTypeName = getCanonicalName(targetDataType)
        for (supported in supported) {
            val matches = supported.matches(targetTypeName)
            if (matches) {
                return true
            }
        }

        //log.warn("bean validation annotation '$annotationType' is not supported on '${targetDataType.getTypeName()}'!")
        return false
    }

    private fun getCanonicalName(targetDataType: DataType): String {
        val pkg = targetDataType.getPackageName()
        val name = if (targetDataType is GenericDataType) {
            targetDataType.getTypeNameNoGenerics()
        } else {
            targetDataType.getTypeName()
        }

        if (pkg.isEmpty()) {
            return name
        }

        return "$pkg.$name"
    }
}



interface SupportedTarget {
    fun matches(source: String): Boolean
}

class Type(val type: String): SupportedTarget {
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

class TypeAny(): SupportedTarget {
    val primitives = listOf("byte", "short", "int", "long", "long", "float", "double", "char")

    override fun matches(source: String): Boolean {
        return !primitives.contains(source)
    }
}

class Primitive(val type: String): SupportedTarget {
    override fun matches(source: String): Boolean {
        return source == type
    }

    override fun toString(): String {
        return type
    }
}

class Array(): SupportedTarget {
    override fun matches(source: String): Boolean {
        return source.endsWith("[]")
    }

    override fun toString(): String {
        return "[]"
    }
}

private val BOOLEAN_TYPES = listOf(
    Type("java.lang.Boolean"),
    Primitive("boolean")
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
    Primitive("double")
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
    Primitive("long")
)

private val LENGTH_TYPES = listOf(
    Type("java.lang.CharSequence"),
    Type("java.util.Collection"),
    Type("java.util.Map"),
    Array()
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
    Primitive("long")
)

private val EMAIL_TYPES = listOf(
    Type("java.lang.CharSequence")
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
)

// not all annotation are supported/used by the BeanValidationFactory
private val supportedTargets = mutableMapOf<String, MutableList<SupportedTarget>>(
    /* javax */
//    "javax.validation.constraints.Null" to ANY_TYPES.toMutableList(),
    "javax.validation.constraints.NotNull" to ANY_TYPES.toMutableList(),
//    "javax.validation.constraints.AssertFalse" to BOOLEAN_TYPES.toMutableList(),
//    "javax.validation.constraints.AssertTrue" to BOOLEAN_TYPES.toMutableList(),
//    "javax.validation.constraints.Min" to MAX_MIN_TYPES.toMutableList(),
//    "javax.validation.constraints.Max" to MAX_MIN_TYPES.toMutableList(),
    "javax.validation.constraints.DecimalMin" to INTEGER_TYPES.toMutableList(),
    "javax.validation.constraints.DecimalMax" to INTEGER_TYPES.toMutableList(),
//    "javax.validation.constraints.Digits" to INTEGER_TYPES.toMutableList(),
//    "javax.validation.constraints.Negative" to NUMBER_TYPES.toMutableList(),
//    "javax.validation.constraints.NegativeOrZero" to NUMBER_TYPES.toMutableList(),
//    "javax.validation.constraints.Positive" to NUMBER_TYPES.toMutableList(),
//    "javax.validation.constraints.PositiveOrZero" to NUMBER_TYPES.toMutableList(),
    "javax.validation.constraints.Size" to LENGTH_TYPES.toMutableList(),
    "javax.validation.constraints.Email" to EMAIL_TYPES.toMutableList(),
    "javax.validation.constraints.Pattern" to EMAIL_TYPES.toMutableList(),
//    "javax.validation.constraints.Past" to PAST_TYPES.toMutableList(),
//    "javax.validation.constraints.PastOrPresent" to PAST_TYPES.toMutableList(),

    /* jakarta */
//    "jakarta.validation.constraints.Null" to ANY_TYPES.toMutableList(),
    "javax.validation.constraints.NotNull" to ANY_TYPES.toMutableList(),
//    "jakarta.validation.constraints.AssertFalse" to BOOLEAN_TYPES.toMutableList(),
//    "jakarta.validation.constraints.AssertTrue" to BOOLEAN_TYPES.toMutableList(),
//    "jakarta.validation.constraints.Min" to MAX_MIN_TYPES.toMutableList(),
//    "jakarta.validation.constraints.Max" to MAX_MIN_TYPES.toMutableList(),
    "jakarta.validation.constraints.DecimalMin" to INTEGER_TYPES.toMutableList(),
    "jakarta.validation.constraints.DecimalMax" to INTEGER_TYPES.toMutableList(),
//    "javax.validation.constraints.Digits" to INTEGER_TYPES.toMutableList(),
//    "jakarta.validation.constraints.Negative" to NUMBER_TYPES.toMutableList(),
//    "jakarta.validation.constraints.NegativeOrZero" to NUMBER_TYPES.toMutableList(),
//    "jakarta.validation.constraints.Positive" to NUMBER_TYPES.toMutableList(),
//    "jakarta.validation.constraints.PositiveOrZero" to NUMBER_TYPES.toMutableList(),
    "jakarta.validation.constraints.Size" to LENGTH_TYPES.toMutableList(),
    "jakarta.validation.constraints.Email" to EMAIL_TYPES.toMutableList(),
    "jakarta.validation.constraints.Pattern" to EMAIL_TYPES.toMutableList(),
//    "jakarta.validation.constraints.Past" to PAST_TYPES.toMutableList(),
//    "jakarta.validation.constraints.PastOrPresent" to PAST_TYPES.toMutableList(),
)
