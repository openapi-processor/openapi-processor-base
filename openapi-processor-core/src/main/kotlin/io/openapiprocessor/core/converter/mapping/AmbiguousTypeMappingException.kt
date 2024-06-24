/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

/**
 * thrown when an ambiguous data type mapping is found
 */
class AmbiguousTypeMappingException(val typeMappings: List<Mapping>): RuntimeException() {
    override val message: String
        get() {
            var msg = "ambiguous type mapping:\n"
            typeMappings.forEach {
                msg += "  ${it}\n"
            }
            return msg
        }
}
