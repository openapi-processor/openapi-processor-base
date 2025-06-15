/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.parser.Operation

private const val SUB_PACKAGE = "api"

class OperationPackage(private val options: ApiOptions) {
   private val pkg = PackageSelector(options.packageOptions)

    fun getPackageName(operation: Operation): String {
        return if (options.packageNameFromLocation) {
            pkg.getPackageName(operation.getDocumentUri(), SUB_PACKAGE)
        } else {
            pkg.getPackageName(SUB_PACKAGE)
        }
    }
}
