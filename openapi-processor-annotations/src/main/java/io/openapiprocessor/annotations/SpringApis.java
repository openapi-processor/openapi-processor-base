/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.annotations;

import java.lang.annotation.*;

/**
 * enable & configure multiple openapi-processor-spring. Alternatively multiple {@link SpringApi}
 * annotations can be used with multiple classes (one class, one {@link SpringApi}).
 *
 * <p>
 * Usage example:
 * <pre>
 * &#064;SpringApis({
 *   &#064;SpringApi(apiPath  = "src/api/apiA.yml", mapping = "src/api/mappingA.yml"),
 *   &#064;SpringApi(apiPath  = "src/api/apiB.yml", mapping = "src/api/mappingB.yml")
 * })
 * class OpenApiConfiguration {}
 * </pre>
 */
@Target (ElementType.TYPE)
@Retention (RetentionPolicy.SOURCE)
public @interface SpringApis {
    SpringApi[] value () default {};
}
