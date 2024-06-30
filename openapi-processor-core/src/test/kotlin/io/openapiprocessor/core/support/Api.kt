/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.model.Api
import io.openapiprocessor.core.model.Endpoint


fun Api.getEndpoint(path: String): Endpoint {
    return this.getInterfaces()
        .map { it.getEndpoint(path) }[0]!!
}
