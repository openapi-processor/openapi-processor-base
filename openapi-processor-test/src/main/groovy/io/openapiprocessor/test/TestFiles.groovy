/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test

interface TestFiles {
    void init(TestSet testSet)
    URI getApiPath(TestSet testSet)
    URI getTargetDir()

    Mapping getMapping(TestSet testSet)
    TestItems getOutputFiles(TestSet testSet)
}
