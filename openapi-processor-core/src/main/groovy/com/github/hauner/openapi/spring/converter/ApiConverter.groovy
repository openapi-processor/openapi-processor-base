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

package com.github.hauner.openapi.spring.converter

import com.github.hauner.openapi.spring.converter.mapping.AddParameterTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.AmbiguousTypeMappingException
import com.github.hauner.openapi.spring.converter.mapping.EndpointTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.Mapping
import com.github.hauner.openapi.spring.converter.mapping.MappingSchema
import com.github.hauner.openapi.spring.converter.mapping.TargetType
import com.github.hauner.openapi.spring.converter.mapping.TypeMapping
import com.github.hauner.openapi.spring.converter.schema.SchemaInfo
import com.github.hauner.openapi.spring.model.Api
import com.github.hauner.openapi.spring.model.DataTypes
import com.github.hauner.openapi.spring.model.Endpoint
import com.github.hauner.openapi.spring.model.Interface
import com.github.hauner.openapi.spring.model.RequestBody as ModelRequestBody
import com.github.hauner.openapi.spring.model.datatypes.MappedDataType
import com.github.hauner.openapi.spring.model.datatypes.ObjectDataType
import com.github.hauner.openapi.spring.model.parameters.AdditionalParameter
import com.github.hauner.openapi.spring.model.parameters.CookieParameter
import com.github.hauner.openapi.spring.model.parameters.HeaderParameter
import com.github.hauner.openapi.spring.model.parameters.MultipartParameter
import com.github.hauner.openapi.spring.model.parameters.Parameter as ModelParameter
import com.github.hauner.openapi.spring.model.parameters.PathParameter
import com.github.hauner.openapi.spring.model.parameters.QueryParameter
import com.github.hauner.openapi.spring.model.Response
import com.github.hauner.openapi.spring.model.datatypes.DataType
import com.github.hauner.openapi.spring.parser.OpenApi
import com.github.hauner.openapi.spring.parser.MediaType as ParserMediaType
import com.github.hauner.openapi.spring.parser.Operation as ParserOperation
import com.github.hauner.openapi.spring.parser.Parameter as ParserParameter
import com.github.hauner.openapi.spring.parser.Path as ParserPath
import com.github.hauner.openapi.spring.parser.RefResolver as ParserRefResolver
import com.github.hauner.openapi.spring.parser.Response as ParserResponse
import com.github.hauner.openapi.spring.parser.RequestBody as ParserRequestBody
import com.github.hauner.openapi.spring.parser.swagger.Operation
import com.github.hauner.openapi.spring.parser.swagger.Schema
import com.github.hauner.openapi.support.Identifier
import groovy.util.logging.Slf4j

/**
 * Converts the open api model to a new model that is better suited for generating source files
 * from the open api specification.
 *
 * @author Martin Hauner
 */
@Slf4j
class ApiConverter {
    public static final String MULTIPART = "multipart/form-data"
    public static final String INTERFACE_DEFAULT_NAME = ''

    private DataTypeConverter dataTypeConverter
    private ApiOptions options

    class MappingSchemaEndpoint implements MappingSchema {
        String path

        @Override
        String getPath () {
            path
        }

        @Override
        String getName () {
            null
        }

        @Override
        String getContentType () {
            null
        }
    }

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
    Api convert (OpenApi api) {
        def target = new Api ()
        createInterfaces (api, target)
        target
    }

    private void createInterfaces (OpenApi api, Api target) {
        Map<String, Interface>interfaces = new HashMap<> ()

        api.paths.each { Map.Entry<String, ParserPath> pathEntry ->
            String path = pathEntry.key
            ParserPath pathValue = pathEntry.value

            def operations = pathValue.operations
            operations.each { ParserOperation op ->
                Interface itf = createInterface (path, op, interfaces)

                Endpoint ep = createEndpoint (path, op, target.models, api.refResolver)
                if (ep) {
                    itf.endpoints.add (ep)
                }
            }
        }

        target.interfaces = interfaces.values () as List<Interface>
    }

    private Interface createInterface (String path, ParserOperation operation, Map<String, Interface> interfaces) {
        def targetInterfaceName = getInterfaceName (operation, isExcluded (path))

        def itf = interfaces.get (targetInterfaceName)
        if (itf) {
            return itf
        }

        itf = new Interface (
            pkg: [options.packageName, 'api'].join ('.'),
            name: targetInterfaceName
        )

        interfaces.put (targetInterfaceName, itf)
        itf
    }

    private Endpoint createEndpoint (String path, ParserOperation operation, DataTypes dataTypes, ParserRefResolver resolver) {
        Endpoint ep = new Endpoint (path: path, method: operation.method)

        try {
            collectParameters (operation.parameters, ep, dataTypes, resolver)
            collectRequestBody (operation.requestBody, ep, dataTypes, resolver)
            collectResponses (operation.responses, ep, dataTypes, resolver)
            ep

        } catch (UnknownDataTypeException e) {
            log.error ("failed to parse endpoint {} {} because of: '{}'", ep.path, ep.method, e.message)
            null
        }
    }

    private void collectParameters (List<ParserParameter> parameters, Endpoint ep, DataTypes dataTypes, ParserRefResolver resolver) {
        parameters.each { ParserParameter parameter ->
            ep.parameters.add (createParameter (ep.path, parameter, dataTypes, resolver))
        }

        List<Mapping> addMappings = findAdditionalParameter (ep)
        addMappings.each {
            ep.parameters.add (createAdditionalParameter (ep.path, it as AddParameterTypeMapping, dataTypes, resolver))
        }
    }

    private void collectRequestBody (ParserRequestBody requestBody, Endpoint ep, DataTypes dataTypes, ParserRefResolver resolver) {
        if (requestBody == null) {
            return
        }

        def required = requestBody.required != null ?: false

        requestBody.content.each { Map.Entry<String, ParserMediaType> requestBodyEntry ->
            def contentType = requestBodyEntry.key
            def mediaType = requestBodyEntry.value

            def info = new SchemaInfo (
                path: ep.path,
                name: getInlineRequestBodyName (ep.path),
                schema: mediaType.schema,
                resolver: resolver)

            if (contentType == MULTIPART) {
                ep.parameters.addAll (createMultipartParameter (info, required))
            } else {
                ep.requestBodies.add (createRequestBody (contentType, info, required, dataTypes))
            }
        }
    }

    private collectResponses (Map<String, ParserResponse> responses, Endpoint ep, DataTypes dataTypes, ParserRefResolver resolver) {
        responses.each { Map.Entry<String, ParserResponse> responseEntry ->
            def httpStatus = responseEntry.key
            def httpResponse = responseEntry.value

            if (!httpResponse.content) {
                ep.addResponses (httpStatus, [Response.EMPTY])
            } else {
                List<Response> results = createResponses (
                    ep.path,
                    httpStatus,
                    httpResponse,
                    dataTypes,
                    resolver)

                ep.addResponses (httpStatus, results)
            }
        }

    }

    private ModelParameter createParameter (String path, ParserParameter parameter, DataTypes dataTypes, resolver) {
        def info = new SchemaInfo (
            path: path,
            name: parameter.name,
            schema: parameter.schema,
            resolver: resolver)

        DataType dataType = dataTypeConverter.convert (info, dataTypes)

        switch (parameter.in) {
            case 'query':
                return new QueryParameter (name: parameter.name, required: parameter.required, dataType: dataType)
            case 'path':
                return new PathParameter (name: parameter.name, required: parameter.required, dataType: dataType)
            case 'header':
                return new HeaderParameter (name: parameter.name, required: parameter.required, dataType: dataType)
            case 'cookie':
                return new CookieParameter (name: parameter.name, required: parameter.required, dataType: dataType)
            default:
                // should not reach this, the openapi parser ignores parameters with unknown type.
                throw new UnknownParameterTypeException(parameter.name, parameter.in)
        }
    }

    private ModelParameter createAdditionalParameter (String path, AddParameterTypeMapping mapping, DataTypes dataTypes, ParserRefResolver resolver) {
        TypeMapping tm = mapping.childMappings.first ()
        TargetType tt = tm.targetType

        def addType = new MappedDataType (
            type: tt.name,
            pkg: tt.pkg,
            genericTypes: tt.genericNames
        )

        new AdditionalParameter (name: mapping.parameterName, required: true, dataType: addType)
    }

    private ModelRequestBody createRequestBody (String contentType, SchemaInfo info, boolean required, DataTypes dataTypes) {
        DataType dataType = dataTypeConverter.convert (info, dataTypes)

        new ModelRequestBody(
            contentType: contentType,
            requestBodyType: dataType,
            required: required)
    }

    private Collection<ModelParameter> createMultipartParameter (SchemaInfo info, boolean required) {
        DataType dataType = dataTypeConverter.convert (info, new DataTypes())
        if (! (dataType instanceof ObjectDataType)) {
            throw new MultipartResponseBodyException(info.path)
        }

        dataType.getObjectProperties ().collect {
            new MultipartParameter (name: it.key, required: required, dataType: it.value)
        }
    }

    private List<Response> createResponses (String path, String httpStatus, ParserResponse response, DataTypes dataTypes, ParserRefResolver resolver) {
        def responses = []

        response.content.each { Map.Entry<String, ParserMediaType> contentEntry ->
            def contentType = contentEntry.key
            def mediaType = contentEntry.value
            def schema = mediaType.schema

            def info = new SchemaInfo (
                path: path,
                contentType: contentType,
                name: getInlineResponseName (path, httpStatus),
                schema: schema,
                resolver: resolver)

            DataType dataType = dataTypeConverter.convert (info, dataTypes)

            def resp = new Response (
                contentType: contentType,
                responseType: dataType)

            responses.add (resp)
        }

        responses
    }

    private List<Mapping> findAdditionalParameter (Endpoint ep) {
        def addMappings = options.typeMappings.findAll {
            it.matches (Mapping.Level.ENDPOINT, new MappingSchemaEndpoint (path: ep.path))
        }.collectMany {
            it.childMappings
        }.findAll {
            it.matches (Mapping.Level.ADD, null as MappingSchema)
        }
        addMappings as List<Mapping>
    }

    private String getInlineRequestBodyName (String path) {
        Identifier.toClass (path) + 'RequestBody'
    }

    private String getInlineResponseName (String path, String httpStatus) {
        Identifier.toClass (path) + 'Response' + httpStatus
    }


    private boolean isExcluded (String path) {
        def endpointMatches = options.typeMappings.findAll {
            it.matches (Mapping.Level.ENDPOINT, new MappingSchemaEndpoint(path: path))
        }

        if (!endpointMatches.empty) {
            if (endpointMatches.size () != 1) {
                throw new AmbiguousTypeMappingException (endpointMatches)
            }

            def match = endpointMatches.first () as EndpointTypeMapping
            return match.exclude
        }

        false
    }

    private String getInterfaceName (def op, boolean excluded) {
        String targetInterfaceName = INTERFACE_DEFAULT_NAME

        if ((op.hasTags())) {
            targetInterfaceName = op.firstTag
        }

        if (excluded) {
            targetInterfaceName += 'Excluded'
        }

        targetInterfaceName
    }

}
