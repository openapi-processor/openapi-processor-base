/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.samples;

import io.openapiprocessor.annotations.SpringApi;
import io.openapiprocessor.annotations.SpringApis;

@SpringApis ({
    @SpringApi(apiPath = "src/openapi/openapiA.yml", mapping = "src/openapi/mappingA.yml"),
    @SpringApi(apiPath = "src/openapi/openapiB.yml", mapping = "src/openapi/mappingB.yml")
})
public class Multiple {
}
