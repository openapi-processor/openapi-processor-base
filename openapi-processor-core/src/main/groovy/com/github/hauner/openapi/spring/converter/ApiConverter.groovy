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

package com.github.hauner.openapi.spring.converter

import com.github.hauner.openapi.spring.generatr.ApiOptions
import com.github.hauner.openapi.spring.generatr.DefaultApiOptions
import com.github.hauner.openapi.spring.model.Api
import com.github.hauner.openapi.spring.model.Endpoint
import com.github.hauner.openapi.spring.model.Response
import com.github.hauner.openapi.spring.model.datatypes.DataType
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.responses.ApiResponse

/**
 * Converts the open api model to a new model that is better suited for generating source files
 * from the open api specification.
 *
 * @author Martin Hauner
 */
class ApiConverter {

    private DataTypeConverter dataTypeConverter
    private ApiOptions options

    ApiConverter(ApiOptions options) {
        this.options = options

        if (!this.options) {
            this.options = new DefaultApiOptions()
        }

        dataTypeConverter = new DataTypeConverter(this.options)
    }

    /**
     * converts the openapi model to the source generation model
     *
     * @param api the open api model
     * @return source generation model
     */
    Api convert (OpenAPI api) {
        def target = new Api ()

        collectModels (api, target)
        collectInterfaces (api, target)
        addEndpointsToInterfaces (api, target)

        target
    }

    private Map<String, PathItem> addEndpointsToInterfaces (OpenAPI api, Api target) {
        api.paths.each { Map.Entry<String, PathItem> pathEntry ->
            String path = pathEntry.key
            PathItem pathItem = pathEntry.value

            def operations = new OperationCollector ().collect (pathItem)
            operations.each { httpOperation ->
                def itf = target.getInterface (getInterfaceName (httpOperation))

                Endpoint ep = new Endpoint (path: path, method: httpOperation.httpMethod)

                httpOperation.responses.each { Map.Entry<String, ApiResponse> responseEntry ->
                    def httpStatus = responseEntry.key
                    def httpResponse = responseEntry.value

                    if (!httpResponse.content) {
                        ep.responses.add (createEmptyResponse ())
                    } else {
                        ep.responses.addAll (createResponses (httpResponse,
                            getInlineResponseName (path, httpStatus), target))
                    }
                }

                itf.endpoints.add (ep)
            }
        }
    }

    private String getInlineResponseName (String path, String httpStatus) {
        path.substring (1).capitalize () + 'Response' + httpStatus
    }

    private Response createEmptyResponse () {
        new Response (responseType: dataTypeConverter.none ())
    }

    private List<Response> createResponses (ApiResponse apiResponse, String inlineName, Api target) {
        def responses = []

        apiResponse.content.each { Map.Entry<String, MediaType> contentEntry ->
            def contentType = contentEntry.key
            def mediaType = contentEntry.value

            DataType dataType = dataTypeConverter.convert (mediaType.schema, inlineName, target.models)

            def response = new Response (
                contentType: contentType,
                responseType: dataType)

            responses.add (response)
        }

        responses
    }

    private void collectInterfaces (OpenAPI api, Api target) {
        target.interfaces = new InterfaceCollector (options)
            .collect (api.paths)
    }

    private void collectModels (OpenAPI api, Api target) {
        if (!api.components || !api.components.schemas) {
            return
        }

        target.models = new SchemaCollector(converter: dataTypeConverter)
            .collect (api.components.schemas)
    }

    private String getInterfaceName(def operation) {
        if (!hasTags (operation)) {
            return InterfaceCollector.INTERFACE_DEFAULT_NAME
        }

        operation.tags.first ()
    }

    private boolean hasTags (op) {
        op.tags && !op.tags.empty
    }
}
