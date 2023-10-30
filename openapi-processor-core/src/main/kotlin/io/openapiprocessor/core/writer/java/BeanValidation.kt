/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

enum class BeanValidationFormat(val pkg: String) {
    JAVAX("javax"),
    JAKARTA("jakarta")
}

@Suppress("PropertyName")
class BeanValidations(val format: BeanValidationFormat = BeanValidationFormat.JAVAX) {
    val DECIMAL_MAX = "${format.pkg}.validation.constraints.DecimalMax"
    val DECIMAL_MIN = "${format.pkg}.validation.constraints.DecimalMin"
    val EMAIL = "${format.pkg}.validation.constraints.Email"
    val NOT_NULL = "${format.pkg}.validation.constraints.NotNull"
    val PATTERN = "${format.pkg}.validation.constraints.Pattern"
    val SIZE = "${format.pkg}.validation.constraints.Size"
    val VALID = "${format.pkg}.validation.Valid"
    val VALUES = "support.validation.Values"
}
