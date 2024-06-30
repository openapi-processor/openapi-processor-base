/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.converter.ApiConverter
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.framework.Framework
import io.openapiprocessor.core.framework.FrameworkBase
import io.openapiprocessor.core.writer.java.JavaIdentifier

fun apiConverter(options: ApiOptions = ApiOptions(), framework: Framework = FrameworkBase()): ApiConverter {
    return ApiConverter (options, JavaIdentifier(), framework)
}
