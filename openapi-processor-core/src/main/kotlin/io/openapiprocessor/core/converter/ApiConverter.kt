/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.converter.mapping.*
import io.openapiprocessor.core.converter.wrapper.MultiDataTypeWrapper
import io.openapiprocessor.core.converter.wrapper.ResultDataTypeWrapper
import io.openapiprocessor.core.converter.wrapper.SingleDataTypeWrapper
import io.openapiprocessor.core.framework.Framework
import io.openapiprocessor.core.model.*
import io.openapiprocessor.core.model.datatypes.*
import io.openapiprocessor.core.parser.*
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.parser.NullSchema.Companion.nullSchema
import io.openapiprocessor.core.parser.RequestBody
import io.openapiprocessor.core.parser.Response
import io.openapiprocessor.core.writer.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import io.openapiprocessor.core.model.RequestBody as ModelRequestBody
import io.openapiprocessor.core.model.Response as ModelResponse
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
        val ep = Endpoint(
            path,
            operation.getMethod(),
            operation.getOperationId(),
            operation.isDeprecated(),
            Documentation(
                summary = operation.summary,
                description = operation.description)
        )

        return try {
            collectParameters (operation.getParameters(), ep, dataTypes, resolver)
            collectRequestBody (operation.getRequestBody(), ep, dataTypes, resolver)
            collectResponses (operation.getResponses(), ep, dataTypes, resolver)
            ep.initEndpointResponses ()
            checkSuccessResponse(ep)
            ep
        } catch (e: UnknownDataTypeException) {
            log.error ("failed to parse endpoint {} {} because of: '{}'", ep.path, ep.method, e.message, e)
            null
        }
    }

    private fun checkSuccessResponse(endpoint: Endpoint) {
        if (endpoint.endpointResponses.isEmpty()) {
            log.warn("endpoint '${endpoint.path}' has no success 2xx response.")
        }
    }

    private fun collectParameters(parameters: List<Parameter>, ep: Endpoint, dataTypes: DataTypes, resolver: RefResolver) {
        parameters.forEach { parameter ->
            ep.parameters.add (createParameter (ep, parameter, dataTypes, resolver))
        }

        val addMappings = getAdditionalParameter (ep)
        addMappings.forEach {
            ep.parameters.add (createAdditionalParameter (it, dataTypes, resolver))
        }
    }

    private fun getAdditionalParameter(ep: Endpoint): List<AddParameterTypeMapping> {
        return mappingFinder.findAddParameterTypeMappings(MappingFinderQuery(ep.path, ep.method))
    }

    private fun collectRequestBody(requestBody: RequestBody?, ep: Endpoint, dataTypes: DataTypes, resolver: RefResolver) {
        if (requestBody == null) {
            return
        }

        requestBody.getContent().forEach { (contentType, mediaType) ->
            val info = SchemaInfo(
                SchemaInfo.Endpoint(ep.path, ep.method),
                getInlineRequestBodyName (ep.path, ep.method),
                "",
                mediaType.getSchema(),
                resolver)

            if (contentType.startsWith(MULTIPART)) {
                ep.parameters.addAll (createMultipartParameter(info, mediaType.encodings, dataTypes))
            } else {
                ep.requestBodies.add (createRequestBody (contentType, info, requestBody, dataTypes))
            }
        }
    }

    private fun collectResponses(responses: Map<String, Response>, ep: Endpoint, dataTypes: DataTypes, resolver: RefResolver) {
        responses.forEach { (httpStatus, httpResponse) ->
            val results = createResponses(
                ep,
                httpStatus,
                httpResponse,
                dataTypes,
                resolver)

            ep.addResponses (httpStatus, results)
        }
    }

    private fun createParameter(ep: Endpoint, parameter: Parameter, dataTypes: DataTypes, resolver: RefResolver): ModelParameter {
        val info = SchemaInfo (
            SchemaInfo.Endpoint(ep.path, ep.method),
            parameter.getName(),
            "",
            parameter.getSchema(),
            resolver,
            "parameters")

        val dataType = convertDataType(info, dataTypes)

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

    @Suppress("UNUSED_PARAMETER")
    private fun createAdditionalParameter(mapping: AddParameterTypeMapping, dataTypes: DataTypes, resolver: RefResolver): ModelParameter {
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

    private fun createResponses(ep: Endpoint, httpStatus: String, response: Response, dataTypes: DataTypes, resolver: RefResolver): List<ModelResponse> {
        if (response.getContent().isEmpty()) {
            val info = SchemaInfo (
                SchemaInfo.Endpoint(ep.path, ep.method),
                "", "", nullSchema, resolver)

            val dataType = NoneDataType()
            val singleDataType = singleDataTypeWrapper.wrap (dataType, info)
            val resultDataType = dataTypeWrapper.wrap (singleDataType, info)

            return listOf(EmptyResponse (responseType = resultDataType))
        }

        val responses = mutableListOf<ModelResponse>()
        response.getContent().forEach { (contentType, mediaType) ->
            val schema = mediaType.getSchema()

            val info = SchemaInfo (
                SchemaInfo.Endpoint(ep.path, ep.method),
                getInlineResponseName (ep.path, ep.method, httpStatus),
                contentType,
                schema,
                resolver)

            val dataType = convertDataType(info, dataTypes)
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

}
