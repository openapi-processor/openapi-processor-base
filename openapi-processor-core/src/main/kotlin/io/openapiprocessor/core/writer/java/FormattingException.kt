/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

class FormattingException(override val message: String, cause: Throwable): java.lang.RuntimeException(cause)
