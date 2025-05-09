/*
 * Copyright 2015 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model

data class ResponseWithStatus(val status: HttpStatus, val response: Response)
