package com.github.hauner.openapi.spring.converter

import com.github.hauner.openapi.spring.ApiOptions
import com.github.hauner.openapi.spring.model.Api
import com.github.hauner.openapi.spring.model.Endpoint
import com.github.hauner.openapi.spring.model.Response
import com.github.hauner.openapi.spring.model.Schema
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.responses.ApiResponse

/**
 * converts the open api model to a new model that is better suited for generating source files
 * from the open api specification.
 */
class ApiConverter {

    private ApiOptions options

    ApiConverter(ApiOptions options) {
        this.options = options
    }

    /**
     * converts the openapi model to the source generation model
     *
     * @param api the open api model
     * @return source generation model
     */
    Api convert (OpenAPI api) {
        def target = new Api ()

        collectInterfaces (api, target)
        addEndpointsToInterfaces (api, target)

        target
    }

    private Map<String, PathItem> addEndpointsToInterfaces (OpenAPI api, Api target) {
        api.paths.each { Map.Entry<String, PathItem> pathEntry ->
            PathItem pathItem = pathEntry.value

            def httpMethods = new OperationCollector ().collect (pathItem)
            httpMethods.each { httpMethod ->
                def itf = target.getInterface (httpMethod.tags.first ())

                Endpoint ep = new Endpoint (path: pathEntry.key, method: httpMethod.httpMethod)

                httpMethod.responses.each { Map.Entry<String, ApiResponse> responseEntry ->
                    def httpStatus = responseEntry.key
                    def httpResponse = responseEntry.value

                    httpResponse.content.each { Map.Entry<String, MediaType> contentEntry ->
                        def contentType = contentEntry.key
                        def mediaType = contentEntry.value

                        def schema = new Schema(type: mediaType.schema.type)

                        def response = new Response (
                            contentType: contentType,
                            responseType: schema)

                        ep.responses.push (response)
                    }
                }

                itf.endpoints.push (ep)
            }
        }
    }

    private void collectInterfaces (OpenAPI api, Api target) {
        target.interfaces = new InterfaceCollector ()
            .collect (api.paths)
    }
}
