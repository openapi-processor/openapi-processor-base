/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions

enum class BeanValidationFormat(val pkg: String) {
    JAVAX("javax"),
    JAKARTA("jakarta")
}

@Suppress("PropertyName")
class BeanValidations(val format: BeanValidationFormat = BeanValidationFormat.JAVAX) {

    companion object {
        fun create(options: ApiOptions): BeanValidations {
            return BeanValidations(getValidationFormat(options.beanValidationFormat))
        }

        fun getValidationFormat(beanValidationFormat: String?): BeanValidationFormat {
            return if (beanValidationFormat != null)
                BeanValidationFormat.valueOf(beanValidationFormat.uppercase())
            else
                BeanValidationFormat.JAVAX
        }
    }

    val DECIMAL_MAX = "${format.pkg}.validation.constraints.DecimalMax"
    val DECIMAL_MIN = "${format.pkg}.validation.constraints.DecimalMin"
    val EMAIL = "${format.pkg}.validation.constraints.Email"
    val NOT_NULL = "${format.pkg}.validation.constraints.NotNull"
    val PATTERN = "${format.pkg}.validation.constraints.Pattern"
    val SIZE = "${format.pkg}.validation.constraints.Size"
    val VALID = "${format.pkg}.validation.Valid"
    val VALUES = "validation.Values"
}
