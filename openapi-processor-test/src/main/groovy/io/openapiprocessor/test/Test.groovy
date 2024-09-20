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
}
