/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.samples;

import io.openapiprocessor.annotations.SpringApi;

@SpringApi (
    apiPath = "src/openapi/openapi.yml",
    mapping = "src/openapi/mapping.yml",
    parser = "OPENAPI4J"
)
public class Parameters {
}
