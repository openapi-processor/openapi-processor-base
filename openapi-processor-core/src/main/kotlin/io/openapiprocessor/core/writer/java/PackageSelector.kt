/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.options.PackageOptions
import java.net.URI

class PackageSelector(private val options: PackageOptions) {

    fun getPackageName(subPackage: String): String {
        return toPackageName(subPackage)
    }

    fun getPackageName(location: URI, subPackage: String): String {
        if (options.location == null) {
            // create package from packageName option
            return toPackageName(subPackage)
        }

        val locationPackage = toPackage(location)
        val childPackage = getChildPackage(locationPackage)
        if (childPackage != null) {
            return childPackage
        }

        return toPackageName(subPackage)
    }

    private fun toPackageName(subPackage: String): String {
        return listOf(options.base, subPackage).joinToString(".")
    }

    private fun toPackage(location: URI): String {
        return location
            .resolve(".")
            .path
            .replace("/", ".")
            .dropLast(1) // file name
    }

    private fun getChildPackage(locationPackage: String): String? {
        val childIndex = locationPackage.indexOf(options.location!!)
        if (childIndex == -1) {
            return null
        }

        val pkgName = locationPackage.substring(childIndex)
        return pkgName
    }
}
