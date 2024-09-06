/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model

import io.openapiprocessor.core.writer.Identifier


/**
 * Java interface properties.
 */
class Interface(
    val name: String,
    private val pkg: String,
    private val pathPrefix: String?,
    private val identifier: Identifier
) {
    val endpoints: List<Endpoint> = mutableListOf()

    fun getEndpoint(path: String): Endpoint? {
        return endpoints.find { it.path == path }
    }

    fun getInterfaceName(): String {
        return if (name.isNotEmpty())
            identifier.toClass (name) + "Api"
        else
            "Api"
    }

    fun getPackageName(): String {
        return pkg
    }

    fun hasPathPrefix(): Boolean {
        return pathPrefix != null
    }

    fun getPathPrefix(): String? {
        return pathPrefix
    }

    fun add(vararg endpoint: Endpoint) {
        endpoints as MutableList
        endpoints.addAll(endpoint)
    }

    override fun toString(): String {
        return "$pkg.$name"
    }
}
