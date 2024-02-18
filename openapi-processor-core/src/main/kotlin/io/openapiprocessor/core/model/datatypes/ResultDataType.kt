/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes


/**
 * Result data type wrapper. Assumes a single generic parameter.
 */
class ResultDataType(
    private val name: String,
    private val pkg: String,
    private val dataType: DataType,
    private val genericTypes: List<GenericDataType> = emptyList()
): DataType {

    override fun getName(): String {
        return if (genericTypes.isEmpty()) {
            "$name<${dataType.getName()}>"
        } else {
            "$name<${genericTypes.first().getName()}<${dataType.getName()}>>"
        }
    }

    override fun getTypeName(): String {
        return if (genericTypes.isEmpty()) {
            "$name<${dataType.getTypeName()}>"
        } else {
            "$name<${genericTypes.first().getTypeName()}<${dataType.getTypeName()}>>"
        }
    }

    override fun getPackageName(): String {
        return pkg
    }

    override fun getImports(): Set<String> {
        return setOf("${getPackageName()}.$name") + genericImports + dataType.getImports()
    }

    private val genericImports: Set<String>
        get() {
            return genericTypes
                .map { it.getImports() }
                .flatten()
                .toSet()
        }

    /**
     * type if the result data type can have multiple values.
     *
     * @return type with ? as the generic parameter
     */
    fun getNameMulti(): String {
        return "$name<?>"
    }

    fun getImportsMulti(): Set<String> {
        return setOf("${getPackageName()}.$name")
    }

}
