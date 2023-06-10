/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-gradle
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.version

import java.net.URI

class GitHubVersionException(uri: URI, cause: Throwable): RuntimeException("can't find version: ${uri}!", cause)
