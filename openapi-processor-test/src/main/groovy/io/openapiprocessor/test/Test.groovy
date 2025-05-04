/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test

import io.openapiprocessor.test.api.OpenApiProcessorTest

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

    /**
     * create a map of relative file names to their absolute path in the resource source folder.
     * @returnmap of relative file name to absolute path
     */
    Map<String, Path> getExpectedFilePaths() {
        def testItems = testFiles.getOutputFiles(testSet)
        def expected = getExpectedFiles(testItems)

        def result = new TreeMap<String, Path>()
        def prefix = testItems.prefix
        expected.forEach {item ->
            def key = stripModelType(item)
            if (prefix != null) {
                key = "$prefix/$key".toString()
            }
            result.put(key, resolveModelTypeInSource(item))
        }
        return result
    }

    private static String stripModelType(String itemName) {
        return itemName.replace("<model>/", "")
    }

    /**
     * create a map of relative file names to their absolute path in the generated folder.
     *
     * @return map of relative file name to absolute path
     */
    Map<String, Path> getGeneratedFilePaths() {
        return Collector.collectPathDictionary(Path.of(testFiles.targetDir))
    }

    Set<String> getExpectedFiles(TestItems testItems) {
        // strip "outputs" part
        return testItems.items.collect {
            it.substring (testSet.expected.size () + 1)
        }
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
}
