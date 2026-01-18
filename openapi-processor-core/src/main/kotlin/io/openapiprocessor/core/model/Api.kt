/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model

import io.openapiprocessor.core.model.datatypes.InterfaceDataType
import io.openapiprocessor.core.model.datatypes.ModelDataType
import io.openapiprocessor.core.model.datatypes.StringEnumDataType
import java.util.function.Consumer

/**
 * Root of the internal model used to generate the api.
 */
class Api(
    private var interfaces: List<Interface> = emptyList(),

    /**
     * resources
     */
    private var resources: List<Resource> = emptyList(),

    /**
     * named data types (i.e., $ref) used in the OpenAPI description.
     */
    private val dataTypes: DataTypes = DataTypes()
) {
    fun getInterfaces(): List<Interface> {
        return interfaces
    }

    fun getInterface(name: String): Interface {
        return interfaces.find { it.name.equals(name, ignoreCase = true) }!!
    }

    fun setInterfaces(ifs: List<Interface>) {
        interfaces = ifs
    }

    fun setResources(resources: List<Resource>) {
        this.resources = resources
    }

    fun getDataTypes(): DataTypes {
        return dataTypes
    }

    fun forEachInterface(action: Consumer<Interface>) {
        interfaces.forEach(action)
    }

    fun forEachModelDataType(action: Consumer<ModelDataType>) {
        dataTypes.getModelDataTypes().forEach(action)
    }

    fun forEachInterfaceDataType(action: Consumer<InterfaceDataType>) {
        dataTypes.getInterfaceDataTypes().forEach(action)
    }

    fun forEachEnumDataType(action: Consumer<StringEnumDataType>) {
        dataTypes.getEnumDataTypes().forEach(action)
    }

    fun forEachResource(action: Consumer<Resource>) {
        resources.forEach(action)
    }
}
