/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-test
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test

import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path

/**
 * used to execute test sets.
 */
class TestSetRunner {

    TestSet testSet
    FileSupport files

    TestSetRunner(TestSet testSet, FileSupport files) {
        this.testSet = testSet
        this.files = files
    }

    /**
     * runs test set on the native file system
     *
     * @param folder temp folder
     * @return true on success, false on failure, ie. if there were any differences
     */
    boolean runOnNativeFileSystem (File folder) {
        def source = testSet.name

        def processor = testSet.processor
        def options = [
            parser: testSet.parser,
            apiPath: "resource:/tests/${source}/inputs/${testSet.openapi}",
            targetDir: folder.absolutePath
        ]

        def mappingYaml = files.getResource ("/tests/${source}/inputs/mapping.yaml")
        if(mappingYaml) {
            options.mapping = mappingYaml.text
        } else {
            options.mapping = testSet.defaultOptions
        }

        when:
        processor.run (options)

        then:
        def packageName = testSet.packageName
        def sourcePath = "/tests/${source}"
        def expectedPath = "${sourcePath}/${packageName}"
        def generatedPath = Path.of (folder.absolutePath).resolve (packageName)

        def expectedFiles = files.getExpectedFiles (sourcePath, packageName)
        def generatedFiles = files.getGeneratedFiles (generatedPath)

        // even if not expected, check that the annotation was generated
        def expectedFilesPlus = expectedFiles + ["support/Generated.java"]
        assert expectedFilesPlus == generatedFiles

        // compare expected files with the generated files
        def success = true
        expectedFiles.each {
            def expected = "${expectedPath}/$it"
            def generated = generatedPath.resolve (it)

            success &= !files.printUnifiedDiff (expected, generated)
        }

        success
    }

    /**
     * runs test set on the given file system
     *
     * @param fs the file system
     * @return true on success, false on failure, ie. if there were any differences
     */
    boolean runOnCustomFileSystem (FileSystem fs) {
        def source = testSet.name

        Path root = Files.createDirectory (fs.getPath ("source"))

        def path = "/tests/${source}"
        files.copy (path, files.collectAbsoluteInputPaths (path), root)
        files.copy (path, files.collectAbsoluteOutputPaths (path), root)

        Path api = root.resolve ("inputs/${testSet.openapi}")
        Path target = fs.getPath ('target')

        def processor = testSet.processor
        def options = [
            parser: testSet.parser,
            apiPath: api.toUri ().toURL ().toString (),
            targetDir: target.toUri ().toURL ().toString ()
        ]

        def mappingYaml = root.resolve ('inputs/mapping.yaml')
        if(Files.exists (mappingYaml)) {
            options.mapping = mappingYaml.toUri ().toURL ().toString ()
        } else {
            options.mapping = testSet.defaultOptions
        }

        def packageName = testSet.packageName
        def expectedPath = root.resolve (packageName)
        def generatedPath = target.resolve (packageName)

        when:
        processor.run (options)

        then:
        def expectedFiles = files.getExpectedFiles (path, packageName)
        def generatedFiles = files.getGeneratedFiles (generatedPath)

        // even if not expected, check that the annotation was generated
        def expectedFilesPlus = expectedFiles + ["support/Generated.java"]
        assert expectedFilesPlus == generatedFiles

        def success = true
        expectedFiles.each {
            def expected = expectedPath.resolve (it)
            def generated = generatedPath.resolve (it)

            success &= !files.printUnifiedDiff (expected, generated)
        }

        success
    }

}
