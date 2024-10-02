/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.converter.mapping.*
import io.openapiprocessor.core.converter.mapping.Annotation


@Deprecated(message = "use parseOptions()")
fun mappings(init: MappingsBuilder.() -> Unit): Mappings {
    val mappings = MappingsBuilder()
    mappings.init()

    return Mappings(
        typeMappings = mappings.buildTypeMappings(),
        parameterTypeMappings = mappings.buildParameterTypeMappings()
    )
}

//        options.globalMappings = mappings {
//            typeMappings {
//                type("Foo @ io.openapiprocessor.Type")
//
//                annotation("Foo") { type = "io.openapiprocessor.Type" }
//            }
//            parameterTypeMappings {
//                type("Foo @ io.openapiprocessor.Type")
//                name("foo @ foo.Bar")
//            }
//        }

class MappingsBuilder {
    val mappings = mutableListOf<Mapping>()
    val parameterMappings = mutableListOf<Mapping>()

    fun type(mapping: String) {

    }

    fun typeMappings(init: TypeMappingsBuilder.() -> Unit) {
        val mappings = TypeMappingsBuilder()
        mappings.init()
        this.mappings.addAll(mappings.mappings)
    }

    fun parameterTypeMappings(init: ParameterTypeMappingsBuilder.() -> Unit) {
        val mappings = ParameterTypeMappingsBuilder()
        mappings.init()
        this.parameterMappings.addAll(mappings.mappings)
    }

    fun buildTypeMappings(): TypeMappings {
        return TypeMappings(mappings)
    }

    fun buildParameterTypeMappings(): TypeMappings {
        return TypeMappings(parameterMappings)
    }
}

class TypeMappingsBuilder {
    val mappings = mutableListOf<Mapping>()

    fun annotation(
        sourceTypeName: String,
        sourceTypeFormat: String? = null,
        init: AnnotationTypeMappingBuilder.() -> Unit
    ) {
        val builder = AnnotationTypeMappingBuilder(sourceTypeName, sourceTypeFormat)
        builder.init()
        mappings.add(builder.annotation())
    }
}

class ParameterTypeMappingsBuilder {
    val mappings = mutableListOf<Mapping>()

    fun annotation(
        sourceTypeName: String,
        sourceTypeFormat: String? = null,
        init: AnnotationTypeMappingBuilder.() -> Unit
    ) {
        val builder = AnnotationTypeMappingBuilder(sourceTypeName, sourceTypeFormat)
        builder.init()
        mappings.add(builder.annotation())
    }

    fun annotationName(
        sourceParameterName: String,
        init: AnnotationNameTypeMappingBuilder.() -> Unit
    ) {
        val builder = AnnotationNameTypeMappingBuilder(sourceParameterName)
        builder.init()
        mappings.add(builder.annotation())
    }
}

class AnnotationTypeMappingBuilder(val sourceTypeName: String, val sourceTypeFormat: String?) {
    lateinit var type: String

    fun annotation(): Mapping {
        return AnnotationTypeMappingDefault(
            sourceTypeName,
            sourceTypeFormat,
            Annotation(type, linkedMapOf()))
    }
}

class AnnotationNameTypeMappingBuilder(val sourceTypeName: String) {
    lateinit var type: String

    fun annotation(): Mapping {
        return AnnotationNameTypeMappingDefault(
            sourceTypeName,
            Annotation(type, linkedMapOf()))
    }
}
