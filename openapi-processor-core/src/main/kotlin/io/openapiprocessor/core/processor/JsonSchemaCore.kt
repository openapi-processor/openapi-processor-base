/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor

import java.net.URI

const val MAPPING_SCHEMA_VERSION = "v18"

val JSON_SCHEMA_CORE = JsonSchema(
    URI("https://openapiprocessor.io/schemas/mapping/mapping-${MAPPING_SCHEMA_VERSION}.json"),
    "/mapping/${MAPPING_SCHEMA_VERSION}/mapping.yaml.json")
