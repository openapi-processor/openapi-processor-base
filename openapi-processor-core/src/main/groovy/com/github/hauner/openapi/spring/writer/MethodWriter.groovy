/*
 * Copyright 2019 the original authors
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

package com.github.hauner.openapi.spring.writer

import com.github.hauner.openapi.spring.model.Endpoint
import com.github.hauner.openapi.support.StringUtil

/**
 * Writer for Java interface methods, i.e. endpoints.
 *
 * @author Martin Hauner
 */
class MethodWriter {

    void write (Writer target, Endpoint endpoint) {
        target.write ("""\
    ${createMappingAnnotation (endpoint)}
    ResponseEntity<${endpoint.response.responseType.name}> ${createMethodName (endpoint)}();
""")
    }

    private String createMappingAnnotation (Endpoint endpoint) {
        String mapping = "${endpoint.method.mappingAnnotation}"
        mapping += "("
        mapping += 'path = ' + quote(endpoint.path)

        if (!endpoint.response.empty) {
            mapping += ", "
            mapping += 'produces = {' + quote(endpoint.response.contentType) + '}'
        }

        mapping += ")"
        mapping
    }


    private String createMethodName (Endpoint endpoint) {
        def tokens = endpoint.path.tokenize ('/')
        tokens = tokens.collect { StringUtil.toCamelCase (it) }
        def name = tokens.join ('')
        "${endpoint.method.method}${name}"
    }

    private String quote (String content) {
        '"' + content + '"'
    }

}
