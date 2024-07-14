/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.framework.Framework
import io.openapiprocessor.core.framework.FrameworkBase
import io.openapiprocessor.core.converter.ApiConverter as ApiConverterKt

import static io.openapiprocessor.core.support.ApiConverterKt.apiConverter as apiConverterKt

class FactoryHelper {

    static ApiConverterKt apiConverter() {
        return apiConverter([:])
    }

    static ApiConverterKt apiConverter(ApiOptions options) {
        return apiConverter(options: options)
    }

    static ApiConverterKt apiConverter(Framework framework) {
        return apiConverter(framework: framework)
    }

    static ApiConverterKt apiConverter(ApiOptions options, Framework framework) {
        return apiConverterKt(options, framework)
    }

    static ApiConverterKt apiConverter(Map args) {
        return apiConverterKt(
            args.options as ApiOptions ?: new ApiOptions(),
            args.framework as Framework ?: new FrameworkBase())
    }
}
