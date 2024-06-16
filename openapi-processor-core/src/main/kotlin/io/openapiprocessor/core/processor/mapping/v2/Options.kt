/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.openapiprocessor.core.support.Empty

/**
 * general options
 */
data class Options(

    /**
     * the root package name of the generated interfaces & models (required)
     *
     * Interfaces and models will be generated into the `api` and `model` subpackages of
     * `packageName`.
     * - so the final package name of the generated interfaces will be `"${packageName}.api"`
     * - and the final package name of the generated models will be `"${packageName}.model"`
     */
    val packageName: String = "io.openapiprocessor.generated",

    /**
     * enable/disable clearing of targetDir (optional).
     */
    val clearTargetDir: Boolean = true,

    /**
     * bean validation (optional)
     */
    val beanValidation: String = "false",

    /**
     * generate javadoc (optional)
     */
    val javadoc: Boolean = false,

    /**
     * generate pojo|record model classes (optional)
     */
    val modelType: String = "default",

    /**
     * generate enum default|string|framework (optional)
     */
    val enumType: String = "default",

    /**
     * generate model source files with suffix (optional)
     */
    val modelNameSuffix: String = String.Empty,

    /**
     * generate common interface for an `oneOf` object list (optional)
     */
    val oneOfInterface: Boolean = false,

    /**
     * enable/disable the code formatter (optional)
     */
    val formatCode: Boolean = false,

    /**
     * enable/disable the @Generated annotation (optional)
     */
    val generatedAnnotation: Boolean = true,

    /**
     * enable/disable the @Generated date (optional)
     */
    val generatedDate: Boolean = true
)
