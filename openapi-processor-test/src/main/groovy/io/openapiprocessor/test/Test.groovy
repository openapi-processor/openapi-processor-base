/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test

class Test {
    private TestSet testSet
    private TestFiles testFiles

    Test(TestSet testSet, TestFiles testFiles) {
        this.testSet = testSet
        this.testFiles = testFiles
    }

    void init() {
        testFiles.init(testSet)
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
        def mapping = testFiles.getMapping(testSet)
        mapping.setModelType(testSet.modelType)
        return mapping
    }

    /**
     * get the expected files (from outputs.yaml). Returns a map of file to location. The location may be null
     * (with classic layout) or "src" or "resources" (with standard layout).
     *
     * @return the expected files map
     */
    Map<String, String> getExpectedFiles() {
        def testProcessor = testSet.processor as OpenApiProcessorTest
        def sourceRoot = testProcessor.sourceRoot
        def resourceRoot = testProcessor.resourceRoot

        def result = new TreeMap<String, String>()

        def testItems = testFiles.getOutputFiles(testSet)
        def files = testItems.items.collect {
            it.substring (testSet.expected.size () + 1)
        }

        files.each {
            if (sourceRoot != null && it.startsWith(sourceRoot)) {
                result[it.substring(sourceRoot.length() + 1)] = sourceRoot

            } else if (resourceRoot != null && it.startsWith(resourceRoot)) {
                result[it.substring(resourceRoot.length() + 1)] = resourceRoot

            } else {
                result[it] = null
            }
        }

        return result
    }
}
