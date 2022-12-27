/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.swagger

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.parameters.RequestBody as SwaggerRequestBody

class RefResolverNative(private val api: OpenAPI) {

    fun resolve(body: SwaggerRequestBody): SwaggerRequestBody {
        val refName = getRefName(body.`$ref`)
        val requestBodies = api.components?.requestBodies
        return requestBodies?.get(refName)!!
    }

    private fun getRefName(ref: String): String? {
        val split = ref.split('#')
        if (split.size > 1) {
            val hash = split[1]
            return hash.substring(hash.lastIndexOf('/') + 1)
        }
        return null
    }
}
