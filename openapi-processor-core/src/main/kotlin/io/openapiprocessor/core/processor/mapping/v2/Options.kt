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
     * The root package name of the generated interfaces and models (required)
     *
     * Interfaces and models will be generated into the `api` and `model` subpackages of
     * `packageName`.
     * - so the final package name of the generated interfaces will be `"${packageName}.api"`
     * - and the final package name of the generated models will be `"${packageName}.model"`
     */
    val packageName: String = "io.openapiprocessor.generated",

    val packageNames: PackageNames = PackageNames(),

    /**
     * enable/disable clearing of targetDir (optional).
     */
    val clearTargetDir: Boolean = true,

    /**
     * target-dir-related options.
     */
    val targetDir: TargetDir = TargetDir(),

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
     * generate pojo getter and setter methods (optional)
     */
    val modelAccessors: Boolean = true,

    /**
     * generate enum default|string|framework (optional)
     */
    val enumType: String = "default",

    /**
     * generate model source files with suffix (optional)
     */
    val modelNameSuffix: String = String.Empty,

    /**
     * base path-related options (optional)
     */
    val basePath: BasePath = BasePath(),

    /**
     * generate common interface for an `oneOf` object list (optional)
     */
    val oneOfInterface: Boolean = false,

    /**
     * generate a common interface for different responses of the same content type (optional)
     */
    val responseInterface: Boolean = false,

    /**
     * enable/disable the code formatter (optional)
     */
    val formatCode: String = "false",

    /**
     * enable/disable the @Generated annotation (optional)
     */
    val generatedAnnotation: Boolean = true,

    /**
     * enable/disable the @Generated date (optional)
     */
    val generatedDate: Boolean = true,

    /**
     * generate @JsonProperty annotation always|auto|never (optional)
     */
    val jsonPropertyAnnotation: String = "always"
)
