/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import java.net.URI
import java.nio.file.Paths

/**
 * convert a source string to a valid URI.
 *
 * if the source is an uri string it converts it to a URI. If the source has no scheme it assumes
 * a local path and adds the file scheme (i.e. file:).
 *
 * @param source source path or url
 * @return a URI to the given source
 */
fun toURI(source: String): URI {
    try {
        val uri = URI(source)
        if (uri.scheme != null) {
            return uri
        }
    } catch (ignore: Exception) {
        // ignore
    }

    // no scheme, assume file path
    return Paths.get(source)
        .normalize()
        .toAbsolutePath()
        .toUri()
}

/**
 * converts a document URI to a corresponding package name within a given root package. If the document
 * URI does not contain the root package path the result is the root package.
 *
 * example:
 * (resource:/tests/packages/inputs/main/kotlin/io/openapiprocessor/foo/foo.yaml, io.openapiprocessor) =>
 * io.openapiprocessor.foo
 *
 * @param documentUri the URI of the document to be converted
 * @param rootPackage the root package name to serve as the base for the resulting package name
 * @return the package name derived from the document URI, based on the specified root package
 *
 *
 */
fun toPackageName(documentUri: URI, rootPackage: String): String {
    val parentPath = documentUri
        .resolve(".").path
        .replace("/", ".")
        .dropLast(1)

    val rootIndex = parentPath.indexOf(rootPackage)
    if (rootIndex == -1) {
        return rootPackage
    }

    val pkgName = parentPath.substring(rootIndex)
    return pkgName
}
