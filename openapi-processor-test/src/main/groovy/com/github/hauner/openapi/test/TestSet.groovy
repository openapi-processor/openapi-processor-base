/*
 * Copyright 2019-2020 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.hauner.openapi.test

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
