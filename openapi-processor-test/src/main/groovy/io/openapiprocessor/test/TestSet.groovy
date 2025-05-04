/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-test
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test

import io.openapiprocessor.api.v2.OpenApiProcessor


class TestSet {

    static String DEFAULT_MAPPING = """\
openapi-processor-mapping: v13

options:
  package-name: generated
    """

    /**
     * name of the test set, i.e. its folder name in the resources.
     */
    String name

    /**
     * the processor that should run this test case
     */
    OpenApiProcessor processor

    /**
     * path of the expected files folder inside the test folder. It MUST match the "package-name"
     * in the mapping.yaml
     */
    String packageName = 'generated'

    /**
     * parser type: "INTERNAL", "SWAGGER" or "OPENAPI4J" (deprecated)
     */
    String parser

    /**
     * model type
     */
    String modelType

    /**
     * root openapi file
     */
    String openapi = "openapi.yaml"

    /**
     * the inputs.yaml
     */
    String inputs = "inputs.yaml"

    /**
     * the outputs.yaml
     */
    String outputs = "outputs.yaml"

    /**
     * folder with expected files
     */
    String expected = "outputs"

    static String getDefaultOptions() {
        DEFAULT_MAPPING
    }

    @Override
    String toString () {
        "${parser.toLowerCase ()} - $name ($openapi, $modelType)"
    }

}
