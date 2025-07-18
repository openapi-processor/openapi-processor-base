/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.converter.mapping.*
import io.openapiprocessor.core.converter.options.BasePathOptions
import io.openapiprocessor.core.converter.options.PackageOptions
import io.openapiprocessor.core.converter.options.TargetDirOptions
import io.openapiprocessor.core.support.Empty

/**
 * Options of the processor.
 */
class ApiOptions: MappingSettings {

    /**
     * The destination folder for generating interfaces & DTOs. This is the parent of the
     * {@link #packageName} folder tree.
     */
    var targetDir: String? = null

    /**
     * target-dir-related options.
     */
    var targetDirOptions: TargetDirOptions = TargetDirOptions()

    /**
     * The root package of the generated interfaces/model. The package folder tree will be created
     * inside {@link #targetDir}. Interfaces and models will be placed into the "api" and "model"
     * subpackages of packageName:
     * - interfaces => "${packageName}.api"
     * - models => "${packageName}.model"
     */
    var packageName = "io.openapiprocessor.generated"

    /**
     * package-name related options
     */
    var packageOptions: PackageOptions = PackageOptions()

    val packageNameFromLocation get() = packageOptions.fromLocation

    // todo move to packageOptions
    var packageNameApi : String = "api"
    var packageNameModel: String = "model"
    var packageNameSupport: String = "support"
    var packageNameValidation: String = "validation"

    val packageApi get() = "${packageName}.${packageNameApi}"
    val packageModel get() = "${packageName}.${packageNameModel}"
    val packageSupport get() = "${packageName}.${packageNameSupport}"
    val packageValidation get() = "${packageName}.${packageNameValidation}"

    /**
     * Enable Bean Validation (JSR303) annotations. Default is false (disabled)
     */
    var beanValidation = false

    /**
     * Bean Validation format: javax (v2) or jakarta (v3)
     */
    var beanValidationFormat: String? = null

    /**
     * enable/disable generation of Javadoc comments based on the `description` OpenAPI property.
     *
     * *experimental*
     */
    var javadoc = false

    /**
     * model type. pojo or record.
     */
    var modelType = "default"

    fun isRecord(): Boolean {
        return modelType == "record"
    }

    /**
     * model accessors. enable/disable generation of model accessors (i.e. pojo getter and setter).
     */
    var modelAccessors = true

    /**
     * enum type. default|string|supplier.
     */
    var enumType = "default"

    /**
     * suffix for model class names and enum names. Default is none, i.e. an empty string.
     */
    var modelNameSuffix = String.Empty

    /**
     * enable/disable generation of a common interface for an `oneOf` list of objects. All objects
     * implement that interface.
     */
    var oneOfInterface = false

    /**
     * enable/disable generation of a marker interface for responses. If a response has multiple different responses
     * (multiple status codes for the same content type) all objects will implement the marker interface. If enabled
     * an endpoint will return the interface data type, if disabled it will return Object.
     */
    var responseInterface = false

    /**
     * enable/disable the code formatter (optional).
     */
    var formatCode = false

    /**
     * formatter: google or eclipse
     */
    var formatCodeFormatter: String? = null

    /**
     *  enable/disable the @Generated annotation (optional).
     */
    var generatedAnnotation = true

    /**
     *  enable/disable the @Generated date (optional).
     */
    var generatedDate = true

    /**
     * add JSON property annotation: always/auto (optional).
     */
    var jsonPropertyAnnotation = JsonPropertyAnnotationMode.Always

    /**
     * base path-related options
     */
    var basePathOptions: BasePathOptions = BasePathOptions()

    /**
     * provide additional type mapping information to map OpenAPI types to java types.
     */
    override var globalMappings: Mappings = Mappings()
    override var endpointMappings: Map<String /* path */, EndpointMappings> = emptyMap()
    override var extensionMappings: Map<String /* x- */, ExtensionMappings> = emptyMap()

    /**
     * logging related options
     */
    var loggingOptions = LoggingOptions()

    /**
     * validate that targetDir is set, throws if not.
     */
    fun validate() {
        if (targetDir == null) {
            throw InvalidOptionException("targetDir")
        }
    }

    // compatibility options

    /**
     * add @Valid on the reactive type and not on the wrapped type
     */
    var beanValidationValidOnReactive = true

    /**
     * break identifier names from digits to letters.
     */
    var identifierWordBreakFromDigitToLetter = true

    /**
     * prefix enum identifier if it starts with an invalid character.
     */
    var identifierPrefixInvalidEnumStart = true
}
