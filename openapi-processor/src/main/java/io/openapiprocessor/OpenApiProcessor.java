/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor;

import java.lang.annotation.*;

@Target (ElementType.TYPE)
@Retention (RetentionPolicy.SOURCE)
@Repeatable(OpenApiProcessors.class)
public @interface OpenApiProcessor {
}
