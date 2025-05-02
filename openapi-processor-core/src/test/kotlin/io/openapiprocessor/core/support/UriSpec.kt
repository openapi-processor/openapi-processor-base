/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.NotWindows
import io.openapiprocessor.core.Windows
import java.net.URI
import java.nio.file.Paths

class UriSpec: StringSpec({

    "converts string source to uri on windows os".config(tags = setOf(Windows)) {
        val current = Paths.get("").toAbsolutePath().toString().replace("\\", "/")
        val currentDrive = current.first()

        forAll(
            row("file://some/path", "file://some/path"),
            row("https://some/path", "https://some/path"),
            row("some/path", "file:///$current/some/path"),
            row("/some/path", "file:///$currentDrive:/some/path"),
            row("c:\\windows\\path", "file:///c:/windows/path"),
            row("c:\\windows\\path/mixed/with/slash/path", "file:///c:/windows/path/mixed/with/slash/path")
        ) { source, uri ->
            toURI(source).toString() shouldBe uri
        }
    }

    "converts string source to uri on unix-like os".config(tags = setOf(NotWindows)) {
        val current = Paths.get("").toAbsolutePath().toString()

        forAll(
            row("file://some/path", "file://some/path"),
            row("https://some/path", "https://some/path"),
            row("some/path", "file://$current/some/path"),
            row("/some/path", "file:///some/path"),
        ) { source, uri ->
            toURI(source).toString() shouldBe uri
        }
    }

    "extract package name from document location" {
        forAll(
            row(
                "file:///user/folder/project/src/main/api/openapi.yaml",
                "io.openapiprocessor",
                "io.openapiprocessor"
            ),
            row(
                "file:///user/folder/project/src/main/kotlin/io/openapiprocessor/foo/foo.yaml",
                "io.openapiprocessor",
                "io.openapiprocessor.foo"
            )
        ) { docUri, rootPkg, expectedPkg ->
            toPackageName(URI.create(docUri), rootPkg) shouldBe expectedPkg
        }
    }
})
