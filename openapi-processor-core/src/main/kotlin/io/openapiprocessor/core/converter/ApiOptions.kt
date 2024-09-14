/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.converter.mapping.*
import io.openapiprocessor.core.converter.options.BasePathOptions
import io.openapiprocessor.core.converter.options.TargetDirOptions
import io.openapiprocessor.core.support.Empty

/**
 * Options of the processor.
 */
class ApiOptions: MappingSettings {

    /**
     * the destination folder for generating interfaces & DTOs. This is the parent of the
     * {@link #packageName} folder tree.
     */
    var targetDir: String? = null

    /**
     * target-dir related options.
     */
    var targetDirOptions: TargetDirOptions = TargetDirOptions()

    /**
     * the root package of the generated interfaces/model. The package folder tree will be created
     * inside {@link #targetDir}. Interfaces and models will be placed into the "api" and "model"
     * subpackages of packageName:
     * - interfaces => "${packageName}.api"
     * - models => "${packageName}.model"
     */
    var packageName = "io.openapiprocessor.generated"

    var packageNameApi : String = "api"
    var packageNameModel: String = "model"
    var packageNameSupport: String = "support"
    var packageNameValidation: String = "validation"

    val packageApi get() = "${packageName}.${packageNameApi}"
    val packageModel get() = "${packageName}.${packageNameModel}"
    val packageSupport get() = "${packageName}.${packageNameSupport}"
    val packageValidation get() = "${packageName}.${packageNameValidation}"

    /**
     * enable Bean Validation (JSR303) annotations. Default is false (disabled)
     */
    var beanValidation = false

    /**
     * Bean Validation format: javax (v2) or jakarta (v3)
     */
    var beanValidationFormat: String? = null

    /**
     * enable/disable generation of javadoc comments based on the `description` OpenAPI property.
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
     * enable/disable the code formatter (optional).
     */
    var formatCode = false

    /**
     *  enable/disable the @Generated annotation (optional).
     */
    var generatedAnnotation = true

    /**
     *  enable/disable the @Generated date (optional).
     */
    var generatedDate = true

    /**
     * add json property annotation: always/auto (optional).
     */
    var jsonPropertyAnnotation = JsonPropertyAnnotationMode.Always

    /**
     * base path related options
     */
    var basePathOptions: BasePathOptions = BasePathOptions()

    /**
     * provide additional type mapping information to map OpenAPI types to java types.
     */
    @Deprecated(message = "use mappings below")
    var typeMappings: List<Mapping> = emptyList()

    override var globalMappings: Mappings = Mappings()
    override var endpointMappings: Map<String /* path */, EndpointMappings> = emptyMap()
    override var extensionMappings: Map<String /* x- */, ExtensionMappings> = emptyMap()

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
     * add @Valid on reactive type and not on the wrapped type
     */
    var beanValidationValidOnReactive = true

    /**
     * break identifier names from digits to letters.
     */
    var identifierWordBreakFromDigitToLetter = true
}
