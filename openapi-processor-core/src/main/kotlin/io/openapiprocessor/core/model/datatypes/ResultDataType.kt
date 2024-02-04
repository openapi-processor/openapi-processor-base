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
    private val genericType: String?,
    private val genericPkg: String?
): DataType {

    override fun getName(): String {
        if (genericType?.isNotEmpty() == true) {
            return "$name<${genericType}<${dataType.getName()}>>"
        } else {
            return "$name<${dataType.getName()}>"

        }
    }

    override fun getTypeName(): String {
        if (genericType?.isNotEmpty() == true) {
            return "$name<${genericType}<${dataType.getTypeName()}>>"
        } else {
            return "$name<${dataType.getTypeName()}>"
        }
    }

    override fun getPackageName(): String {
        return pkg
    }

    override fun getImports(): Set<String> {
        return setOf("${getPackageName()}.$name") + dataType.getImports() + if (genericType?.isNotBlank() == true) { setOf("${genericPkg}.$genericType")} else { emptySet() }
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
