/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.version

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.json.JsonMapper
import java.net.URI
import java.time.Instant


class GitHubVersionProvider(private val repoName: String) {

    companion object {
        const val MARKER = "<REPO>"
        const val LATEST_PATTERN = "https://api.github.com/repos/openapi-processor/<REPO>/releases/latest"
    }

    fun getVersion(): GitHubVersion {
        val latestUri = URI(LATEST_PATTERN.replace(MARKER, repoName))

        try {
            val json = JsonMapper().readValue(latestUri.toURL(), object: TypeReference<Map<String, Any>>() {})

            return GitHubVersion(
                    name = json["name"] as String,
                    publishedAt = Instant.parse(json["published_at"] as CharSequence),
                    text = json["body"] as String
            )
        } catch (t: Throwable) {
            throw GitHubVersionException(latestUri, t)
        }
    }
}
