/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.converter.mapping.AddParameterTypeMapping
import io.openapiprocessor.core.converter.mapping.TargetType
import io.openapiprocessor.core.converter.mapping.UnknownDataTypeException
import io.openapiprocessor.core.converter.mapping.UnknownParameterTypeException
import io.openapiprocessor.core.converter.wrapper.MultiDataTypeWrapper
import io.openapiprocessor.core.converter.wrapper.ResultDataTypeWrapper
import io.openapiprocessor.core.converter.wrapper.SingleDataTypeWrapper
import io.openapiprocessor.core.framework.Framework
import io.openapiprocessor.core.model.*
import io.openapiprocessor.core.model.datatypes.*
import io.openapiprocessor.core.parser.*
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.parser.RequestBody
import io.openapiprocessor.core.parser.Response
import io.openapiprocessor.core.processor.mapping.v2.ResultStyle
import io.openapiprocessor.core.support.capitalizeFirstChar
import io.openapiprocessor.core.writer.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import io.openapiprocessor.core.model.RequestBody as ModelRequestBody
import io.openapiprocessor.core.model.Response as ModelResponse
import io.openapiprocessor.core.model.HttpStatus as ModelHttpStatus
import io.openapiprocessor.core.model.ContentType as ModelContentType
import io.openapiprocessor.core.model.parameters.Parameter as ModelParameter

const val MULTIPART = "multipart/"
const val INTERFACE_DEFAULT_NAME = ""

/**
 * Converts the open api model to a new model that is better suited for generating source files
 * from the open api specification.
 */
class  ApiConverter(
    private val options: ApiOptions,
    private val identifier: Identifier,
    private val framework: Framework
) {
    var log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    private val mappingFinder = MappingFinder(options)
    private val dataTypeWrapper = ResultDataTypeWrapper(options, identifier, mappingFinder)
    private val dataTypeConverter = DataTypeConverter(options, identifier, mappingFinder)
    private val singleDataTypeWrapper = SingleDataTypeWrapper(options, mappingFinder)
    private val multiDataTypeWrapper = MultiDataTypeWrapper(options, mappingFinder)

    /**
     * converts the openapi model to the source generation model
     *
     * @param api the open api model
     * @return source generation model
     */
    fun convert(api: OpenApi): Api {
        val target = Api()
        createInterfaces(api, target)
        createResources(api, target)
        return target
    }

    private fun createInterfaces(api: OpenApi, target: Api) {
        val interfaces = hashMapOf<String, Interface>()
        val serverPath = getServerPath(api)

        api.getPaths().forEach { (path, pathValue) ->
            val operations = pathValue.getOperations()

            operations.forEach { op ->
                val itf = createInterface(path, serverPath, op, interfaces)

                val ep = createEndpoint(path, op, target.getDataTypes(), api.getRefResolver())
                if (ep != null) {
                    itf.add(ep)
                }
            }
        }

        target.setInterfaces(interfaces.values.map { it })
    }

    private fun createResources(api: OpenApi, target: Api) {
        val resources = mutableListOf<Resource>()

        val serverPath = getServerPath(api)
        if (serverPath != null) {
            resources.add(Resource(options.basePathOptions.propertiesName, """
                openapi.base.path = $serverPath
                
            """.trimIndent()))
        }

        target.setResources(resources)
    }

    private fun getServerPath(api: OpenApi): String? {
        if (!options.basePathOptions.enabled) {
            return null
        }

        val servers = api.getServers()
        if (servers.isEmpty()) {
            return null
        }

        return servers[options.basePathOptions.serverUrl!!].getUri().path
    }

    private fun createInterface(
        path: String,
        pathPrefix: String?,
        operation: Operation,
        interfaces: MutableMap<String, Interface>
    ): Interface {
        val targetInterfaceName = getInterfaceName(operation, isExcluded(path, operation.getMethod()))

        var itf = interfaces[targetInterfaceName]
        if (itf != null) {
            return itf
        }

        itf = Interface(
            targetInterfaceName,
            listOf(options.packageName, "api").joinToString("."),
            pathPrefix,
            identifier)

        interfaces[targetInterfaceName] = itf
        return itf
    }

    private fun createEndpoint(path: String, operation: Operation, dataTypes: DataTypes, resolver: RefResolver): Endpoint? {
        return try {
            val ctx = ApiConverterContext(
                path,
                operation.getMethod(),
                dataTypes,
                resolver
            )

            val parameters = collectParameters(operation.getParameters(), ctx)
            val requestBodies = collectRequestBodies(operation.getRequestBody(), ctx)
            val responses = collectResponses (operation.getResponses(), ctx)

            val ep = Endpoint(
                path,
                operation.getMethod(),
                parameters + requestBodies.parameters,
                requestBodies.bodies,
                responses,
                operation.getOperationId(),
                operation.isDeprecated(),
                Documentation(
                    summary = operation.summary,
                    description = operation.description
                ))

            checkSuccessResponse(ep)
            ep
        } catch (e: UnknownDataTypeException) {
            log.error ("failed to parse endpoint {} {} because of: '{}'", path, operation.getMethod(), e.message, e)
            null
        }
    }

    private fun checkSuccessResponse(endpoint: Endpoint) {
        if (endpoint.endpointResponses.isEmpty()) {
            log.warn("endpoint '${endpoint.path}' has no success 2xx response.")
        }
    }

    private fun collectParameters(parameters: List<Parameter>, ctx: ApiConverterContext): List<ModelParameter> {
        val resultParameters: MutableList<ModelParameter> = mutableListOf()

        parameters.forEach {
            resultParameters.add(createParameter(it, ctx))
        }

        val addMappings = getAdditionalParameterMappings (ctx.path, ctx.method)
        addMappings.forEach {
            resultParameters.add (createAdditionalParameter (it))
        }

        return resultParameters
    }

    private fun getAdditionalParameterMappings(path: String, method: HttpMethod): List<AddParameterTypeMapping> {
        return mappingFinder.findAddParameterTypeMappings(MappingFinderQuery(path, method))
    }

    data class RequestBodies(val bodies: List<ModelRequestBody>, val parameters: List<ModelParameter>)

    private fun collectRequestBodies(requestBody: RequestBody?, ctx: ApiConverterContext): RequestBodies {
        if (requestBody == null) {
            return RequestBodies(emptyList(), emptyList())
        }

        val bodies: MutableList<ModelRequestBody> = mutableListOf()
        val params: MutableList<ModelParameter> = mutableListOf()

        requestBody.getContent().forEach { (contentType, mediaType) ->
            val info = SchemaInfo(
                SchemaInfo.Endpoint(ctx.path, ctx.method),
                getInlineRequestBodyName (ctx.path, ctx.method),
                "",
                mediaType.getSchema(),
                ctx.resolver)

            if (contentType.startsWith(MULTIPART)) {
                params.addAll(createMultipartParameter(info, mediaType.encodings, ctx.dataTypes))
            } else {
                bodies.add (createRequestBody (contentType, info, requestBody, ctx.dataTypes))
            }
        }

        return RequestBodies(bodies, params)
    }

    private fun collectResponses(responses: Map<HttpStatus, Response>, ctx: ApiConverterContext): Map<ModelHttpStatus, List<ModelResponse>> {
        val resultResponses: MutableMap<HttpStatus, List<ModelResponse>>  = mutableMapOf()
        val contentTypeInterfaces = collectContentTypeInterfaces(responses, ctx)

        responses.forEach { (httpStatus, httpResponse) ->
            val results = createResponses(
                httpStatus,
                httpResponse,
                ctx.with(contentTypeInterfaces))

            resultResponses[httpStatus] = results
        }

        return resultResponses
    }

    private fun collectContentTypeInterfaces(responses: Map<HttpStatus, Response>, ctx: ApiConverterContext)
    : Map<ModelContentType, ContentTypeInterface> {
        if (!options.responseInterface) {
            return emptyMap()
        }

        val resultStyle = getResultStyle(ctx.path, ctx.method)
        val responseCollector = ContentTypeResponseCollector(responses, resultStyle)

        // to check if a response marker interface is wanted it is necessary to know if the responses have the same
        // result data type. In case they have the same data type we do not need the marker interface.
        //
        // Unfortunately we have to calculate the result data types to achieve this because it is currently not possible
        // to detect this from the parsed OpenAPI.

        val checkResponses: MutableMap<ModelHttpStatus, List<ModelResponse>> = mutableMapOf()

        val checkDataTypes = ctx.dataTypes.copy()
        responses.forEach { (httpStatus, httpResponse) ->
            val results = createResponses(
                httpStatus,
                httpResponse,
                ctx.with(checkDataTypes)
            )

            checkResponses[httpStatus] = results
        }

        val interfaceCollector = ContentTypeInterfaceCollector(ctx.path, ctx.method)
        val contentTypeInterfaces = interfaceCollector.collectContentTypeInterfaces(
            responseCollector.contentTypeResponses,
            checkResponses
        )
        return contentTypeInterfaces
    }

    private fun createParameter(parameter: Parameter, ctx: ApiConverterContext): ModelParameter {
        val info = SchemaInfo (
            SchemaInfo.Endpoint(ctx.path, ctx.method),
            parameter.getName(),
            "",
            parameter.getSchema(),
            ctx.resolver,
            "parameters")

        val dataType = convertDataType(info, ctx.dataTypes)

        return when (parameter.getIn()) {
            "query" ->
                framework.createQueryParameter (parameter, dataType)
            "path" ->
                framework.createPathParameter (parameter, dataType)
            "header" ->
                framework.createHeaderParameter (parameter, dataType)
            "cookie" ->
                framework.createCookieParameter (parameter, dataType)
            else ->
                // should not reach this, the openapi parser ignores parameters with unknown type.
                throw UnknownParameterTypeException(parameter.getName(), parameter.getIn())
        }
    }

    private fun createAdditionalParameter(mapping: AddParameterTypeMapping): ModelParameter {
        val addType = dataTypeConverter.createAdditionalParameterDataType(mapping.mapping)

        var annotationType: AnnotationDataType? = null
        if (mapping.annotation != null) {
            val at = TargetType(mapping.annotation.type, emptyList())

            annotationType = AnnotationDataType(
                at.getName(),
                at.getPkg(),
                mapping.annotation.parameters
            )
        }

        val parameter = object: Parameter {

            override fun getIn(): String {
                return "add"
            }

            override fun getName(): String {
                return mapping.parameterName
            }

            override fun getSchema(): Schema {
                throw NotImplementedError("unexpected method call.")
            }

            override fun isRequired(): Boolean {
                return false
            }

            override fun isDeprecated(): Boolean {
                return false
            }

            override val description: String?
                get() = null
        }

        return framework.createAdditionalParameter (parameter, addType, annotationType)
    }

    private fun createRequestBody(contentType: String, info: SchemaInfo, requestBody: RequestBody, dataTypes: DataTypes): ModelRequestBody {
        val dataType = convertDataType(info, dataTypes)

        val changedType = if (dataType.isCollection()) {
            multiDataTypeWrapper.wrap(dataType, info)
        } else {
            singleDataTypeWrapper.wrap(dataType, info)
        }

        return framework.createRequestBody(contentType, requestBody, changedType)
    }

    private fun createMultipartParameter(info: SchemaInfo, encodings: Map<String, Encoding>,
        dataTypes: DataTypes): Collection<ModelParameter> {
        val dataType = convertDataType(info, dataTypes)
        if (dataType !is ObjectDataType) {
            throw MultipartResponseBodyException(info.getPath())
        }

        dataTypes.del(dataType)
        val parameters = mutableListOf<ModelParameter>()
        dataType.forEach { property, propertyDataType ->
            val mpp = MultipartParameter(property, encodings[property]?.contentType)
            val parameter = framework.createMultipartParameter(mpp, propertyDataType)
            parameters.add(parameter)
        }
        return parameters
    }

    private fun createResponses(httpStatus: String, response: Response, ctx: ApiConverterContext): List<ModelResponse> {
        if (response.getContent().isEmpty()) {
            val info = SchemaInfo (
                SchemaInfo.Endpoint(ctx.path, ctx.method),
                "",
                "",
                NullSchema,
                ctx.resolver,
                "response")

            val dataType = NoneDataType()
            val singleDataType = singleDataTypeWrapper.wrap (dataType, info)
            val resultDataType = dataTypeWrapper.wrap (singleDataType, info)

            return listOf(EmptyResponse (responseType = resultDataType))
        }

        val responses = mutableListOf<ModelResponse>()
        response.getContent().forEach { (contentType, mediaType) ->
            val ctInterface = ctx.getContentTypeInterface(contentType)
            val schema = mediaType.getSchema()

            val info = SchemaInfo (
                SchemaInfo.Endpoint(ctx.path, ctx.method),
                getInlineResponseName (ctx.path, ctx.method, httpStatus),
                contentType,
                schema,
                ctx.resolver,
                "response",
                ctInterface != null,
                getResponseMarkerInterfaceName(ctx.path, ctx.method, contentType))

            val dataType = convertDataType(info, ctx.dataTypes)
            val changedType = if (!info.isArray ()) { // todo fails if ref
                singleDataTypeWrapper.wrap(dataType, info)
            } else {
                multiDataTypeWrapper.wrap(dataType, info)
            }
            val resultDataType = dataTypeWrapper.wrap(changedType, info)

            responses.add (ModelResponse(contentType, resultDataType, response.description))
        }

        return responses
    }

    private fun convertDataType(info: SchemaInfo, dataTypes: DataTypes): DataType {
        return dataTypeConverter.convert(info, dataTypes)
    }

    private fun getInlineRequestBodyName(path: String, method: HttpMethod): String {
        return identifier.toClass(path) + method.method.replaceFirstChar { it.uppercase() } + "RequestBody"
    }

    private fun getInlineResponseName(path: String, method: HttpMethod, httpStatus: String): String {
        return identifier.toClass(path) + method.method.replaceFirstChar { it.uppercase() } + "Response" + httpStatus
    }

    private fun isExcluded(path: String, method: HttpMethod): Boolean {
        return mappingFinder.isEndpointExcluded(MappingFinderQuery(path, method))
    }

    private fun getResultStyle(path: String, method: HttpMethod): ResultStyle {
        return mappingFinder.findResultStyleMapping(MappingFinderQuery(path, method))
    }

    private fun getInterfaceName(op: Operation, excluded: Boolean): String {
        var targetInterfaceName = INTERFACE_DEFAULT_NAME

        if((op.hasTags())) {
            targetInterfaceName = identifier.toClass(op.getFirstTag()!!)
        }

        if (excluded) {
            targetInterfaceName += "Excluded"
        }

        return targetInterfaceName
    }

    private fun getResponseMarkerInterfaceName(path: String, method: HttpMethod, contentType: String): String {
        return listOf(
            method.method.capitalizeFirstChar(),
            identifier.toClass(path),
            identifier.toClass(contentType),
            "Response"
        ).joinToString("")
    }
}
