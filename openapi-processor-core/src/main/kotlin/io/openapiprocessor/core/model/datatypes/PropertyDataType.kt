/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

import io.openapiprocessor.core.model.Documentation

/**
 * schema "properties" data type wrapper. readOnly/writeOnly may be different on each use of the
 * same schema as a property in another schema.
 */
open class PropertyDataType(
    val readOnly: Boolean,
    val writeOnly: Boolean,
    val dataType: DataType,
    override val documentation: Documentation?,
    val extensions: Map<String, *> = emptyMap<String, Any>()
): DataType by dataType {

    override val referencedImports: Set<String>
        get() = dataType.referencedImports

    override val constraints: DataTypeConstraints?
        get() = dataType.constraints

    override val deprecated: Boolean
        get() = dataType.deprecated
}
