/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.version

import io.openapiprocessor.api.v2.Version
import java.time.Instant

class GitHubVersion(val name: String, val publishedAt: Instant, val text: String) : Version {

    override fun getName(): String {
        return name
    }

    override fun getPublishedAt(): Instant {
        return publishedAt
    }

    override fun getText(): String {
        return text
    }
}
