/*
 * Copyright 2020 the original authors
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

package com.github.hauner.openapi.spring.processor

import com.github.hauner.openapi.spring.converter.mapping.TypeMapping
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Subject

@Ignore
class MappingConverterV2Spec extends Specification {

    def reader = new MappingReader()

    @Subject
    def converter = new MappingConverter()

    void "reads global type mapping" () {
        String yaml = """\
openapi-processor-spring: v2.0

map:
  types:
    - type: array => java.util.Collection
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size () == 1
        def type = mappings.first () as TypeMapping
        type.sourceTypeName == 'array'
        type.sourceTypeFormat == null
        type.targetTypeName == 'java.util.Collection'
        type.genericTypeNames == []
    }

}
