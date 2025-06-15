/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.SchemaInfo

private const val SUB_PACKAGE = "model"

class SchemaPackage(private val options: ApiOptions)  {
    private val pkg = PackageSelector(options.packageOptions)

    fun getPackageName(schemaInfo: SchemaInfo): String {
        return if (options.packageNameFromLocation) {
            pkg.getPackageName(schemaInfo.getDocumentUri(), SUB_PACKAGE)
        } else {
            pkg.getPackageName(SUB_PACKAGE)
        }
    }
}
