/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.builder.api

import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.model.RequestBody
import io.openapiprocessor.core.model.Response
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.model.Documentation

/**
 * entry point of model [Endpoint] builder dsl
 */
fun endpoint(path: String, method: HttpMethod = HttpMethod.GET, init: EndpointBuilder.() -> Unit): Endpoint {
    val builder = EndpointBuilder(path, method)
    init(builder)
    return builder.build()
}

class EndpointBuilder(
    private val path: String,
    private val method: HttpMethod = HttpMethod.GET
) {
    private var parameters: List<Parameter> = emptyList()
    private var bodies: List<RequestBody> = emptyList()
    private var responses: Map<String, List<Response>> = mapOf()
    private var deprecated = false

    var operationId: String? = null
    var summary: String? = null
    var description: String? = null

    fun deprecated() {
        deprecated = true
    }

    fun summary(summary: String) {
        this.summary = summary
    }

    fun description(description: String) {
        this.description = description
    }

    fun parameters(init: ParametersBuilder.() -> Unit) {
        val builder = ParametersBuilder()
        init(builder)
        parameters = builder.parameters()
        bodies = builder.bodies()
    }

    fun responses(init: ResponsesBuilder.() -> Unit) {
        val builder = ResponsesBuilder()
        init(builder)
        responses = builder.build()
    }

    fun build(): Endpoint {
        return Endpoint(
            path,
            method,
            parameters,
            bodies,
            responses,
            operationId = operationId,
            deprecated = deprecated,
            documentation = Documentation(summary, description)
        )
    }
}
