/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.builder.api

import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.model.Interface
import io.openapiprocessor.core.writer.Identifier
import io.openapiprocessor.core.writer.java.JavaIdentifier
import io.openapiprocessor.core.builder.api.endpoint as ep

fun `interface`(
    name: String = "Foo",
    pkg: String = "io.openapiprocessor.test",
    identifier: Identifier = JavaIdentifier(),
    init: InterfaceBuilder.() -> Unit
): Interface {
    val builder = InterfaceBuilder(name, pkg, identifier)
    init(builder)
    return builder.build()
}

class InterfaceBuilder(
    private val name: String,
    private val pkg: String,
    private val identifier: Identifier
) {
    private val endpoints = mutableListOf<Endpoint>()

    fun endpoint(path: String, method: HttpMethod = HttpMethod.GET, init: EndpointBuilder.() -> Unit) {
        endpoints.add(ep(path, method, init))
    }

    fun build(): Interface {
        val itf = Interface(name, pkg, identifier)
        itf.add(*endpoints.toTypedArray())
        return itf
    }
}
