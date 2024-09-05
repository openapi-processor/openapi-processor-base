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
    private val identifier: Identifier,
    val path: String? = null
) {
    val endpoints: List<Endpoint> = mutableListOf()

    fun getEndpoint(path: String): Endpoint? {
        return endpoints.find { it.path == path }
    }

    fun getPackageName(): String {
        return pkg
    }

    fun getInterfaceName(): String {
        return if (name.isNotEmpty())
            identifier.toClass (name) + "Api"
        else
            "Api"
    }

    fun add(vararg endpoint: Endpoint) {
        endpoints as MutableList
        endpoints.addAll(endpoint)
    }

    fun hasPath(): Boolean {
        return path != null
    }

    override fun toString(): String {
        return "$pkg.$name"
    }
}
