/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-test
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test

import io.openapiprocessor.api.OpenApiProcessor


class TestSet {

    static String DEFAULT_MAPPING = """\
openapi-processor-spring: v2

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
     * parser type: "SWAGGER" or "OPENAPI4J"
     */
    String parser

    static String getDefaultOptions() {
        DEFAULT_MAPPING
    }

    @Override
    String toString () {
        "${parser.toLowerCase ()} - $name"
    }

}
