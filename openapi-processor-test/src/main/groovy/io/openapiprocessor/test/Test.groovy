/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test

import io.openapiprocessor.test.api.OpenApiProcessorTest

import java.nio.file.Files
import java.nio.file.Path

class Test {
    private TestSet testSet
    private TestFiles testFiles
    private OpenApiProcessorTest testProcessor

    private Mapping mapping
    private String packageName

    Test(TestSet testSet, TestFiles testFiles) {
        this.testSet = testSet
        this.testFiles = testFiles
        this.testProcessor = testSet.processor as OpenApiProcessorTest
    }

    void init() {
        testFiles.init(testSet)

        mapping = testFiles.getMapping(testSet)
        mapping.setModelType(testSet.modelType)
        packageName = mapping.packageName
    }

    String getParser() {
        return testSet.parser
    }

    URI getApiPath() {
        return testFiles.getApiPath(testSet)
    }

    String getTargetDir() {
        return testFiles.getTargetDir()
    }

    Mapping getMapping() {
        return mapping
    }

    String getPackagePath() {
        return packageName.replace(".", "/")
    }

    // relative key (i.e., with prefix and no placeholder) => absolute path (to resource source folder)
    Map<String, Path> getExpectedFilePaths() {
        def testItems = testFiles.getOutputFiles(testSet)
        def expected = getExpectedFiles(testItems)

        def result = new TreeMap<String, Path>()
        def prefix = testItems.prefix
        expected.forEach {k, v ->
            def kNoPlaceholder = k.replace("<model>/", "")

            if (prefix != null) {
                kNoPlaceholder = "$prefix/$kNoPlaceholder"
            }

            result.put(kNoPlaceholder.toString(), getExpectedFilePath(k, v))
        }
        return result
    }

    /**
     * get the expected files (from outputs.yaml). Returns a map of file to location. The location may be null
     * (with classic layout) or "src" or "resources" (with standard layout).
     *
     * @return the expected files map
     */
    Map<String, String> getExpectedFiles(TestItems testItems) {
        def result = new TreeMap<String, String>()

        // strip "outputs" part
        def files = testItems.items.collect {
            it.substring (testSet.expected.size () + 1)
        }

        files.each {
            result[it] = null
        }

        return result
    }

    Set<String> getGeneratedSourceFiles() {
        def targetPath = Path.of(testFiles.targetDir)
        def sourcePath = getGeneratedSourcePath(targetPath, null)
        return getGeneratedFiles(sourcePath)
    }

    Set<String> getGeneratedResourceFiles() {
        def targetPath = Path.of(testFiles.targetDir)
        def sourcePath = getGeneratedResourcePath(targetPath, null)
        return getGeneratedFiles(sourcePath)
    }

    Path getExpectedFilePath(String file, String sourcePrefix) {
        def expectedFilePath = file
        if (sourcePrefix != null) {
            expectedFilePath = "${sourcePrefix}/${file}"
        }

        return resolveModelTypeInSource(expectedFilePath)
    }

    Path resolveModelTypeInSource(String path) {
        return testFiles.getSourcePath(testSet, resolveModelTypeName(path))
    }

    static boolean printUnifiedDiff (Path expected, Path generated) {
        return Diff.printUnifiedDiff(expected, generated)
    }

    void print() {
        testFiles.printTree()
    }

    private String resolveModelTypeName(String path) {
        def model = '_default_'

        if (testSet.modelType == 'record') {
            model = '_record_'
        }

        def result = path.replaceFirst("<model>", model)
        return result
    }

    private static Path getGeneratedSourcePath(Path target, String source) {
        def path = target
        if (source != null) {
            path = path.resolve(source)
        }
        return path
    }

    private static Path getGeneratedResourcePath(Path target, String resource) {
        def path = target
        if (resource != null) {
            path = path.resolve(resource)
        }
        return path
    }

    /**
     * get the generated files.
     *
     * @param path path of the generated files
     * @return the generated files
     */
    private static Set<String> getGeneratedFiles (Path path) {
        def result = new TreeSet<String> ()
        if (Files.exists(path)) {
            result.addAll (Collector.collectPaths (path))
        }
        result
    }
}
