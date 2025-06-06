package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.WithDataTestName
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.converter.MappingFinderQuery
import io.openapiprocessor.core.converter.mapping.*
import io.openapiprocessor.core.converter.mapping.matcher.AnnotationTypeMatcher
import io.openapiprocessor.core.converter.mapping.steps.RootStep
import io.openapiprocessor.core.converter.mapping.steps.TypesStep
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.processor.MappingReader
import io.openapiprocessor.core.support.query
import io.openapiprocessor.core.support.typeMatcher

class MappingConverterTypeSpec: FreeSpec({
    val reader = MappingReader()

    fun buildMapping(source: String, generics: List<String>?): String {
        var yaml = """
           |openapi-processor-mapping: v8
           |options:
           |  package-name: ignored
           |map:
           |  types:
           |    - type: $source
           |
           """.trimMargin()

        if (generics == null) {
            return yaml
        }

        yaml += """
            |      generics:
            |
        """.trimMargin()

        generics.forEach {
            yaml += """
                |      - $it
                |
                """.trimMargin()
        }

        return yaml
    }

    data class Item(
        val dataTestName: String,
        val source: String,
        val generics: List<String>? = null,
        val expected: TypeMapping
    ) : WithDataTestName {
        override fun dataTestName(): String {
            return dataTestName
        }
    }

    "read global type mapping" - {
        withData(
            Item("normal",
                source = "array => java.util.Collection",
                expected = TypeMapping(
                    "array",
                    null,
                    "java.util.Collection",
                    emptyList(),
                    primitive = false,
                    primitiveArray = false
                )),
            Item("normal, extra whitespace",
                source = "  array   =>    java.util.Collection  ",
                expected = TypeMapping(
                    "array",
                    null,
                    "java.util.Collection",
                    emptyList(),
                    primitive = false,
                    primitiveArray = false
                )),
            Item("with format",
                source = "string:date-time => java.time.Instant",
                expected = TypeMapping(
                    "string",
                    "date-time",
                    "java.time.Instant",
                    emptyList(),
                    primitive = false,
                    primitiveArray = false
                )),
            Item("with format, extra whitespace",
                source = "'  string  :  date-time   =>   java.time.Instant  '",
                expected = TypeMapping(
                    "string",
                    "date-time",
                    "java.time.Instant",
                    emptyList(),
                    primitive = false,
                    primitiveArray = false
                )),
            Item("with inline generics",
                source = "Foo => mapping.Bar<java.lang.String, java.lang.Boolean>",
                expected = TypeMapping (
                    "Foo",
                    null,
                    "mapping.Bar",
                    listOf(
                        TargetType("java.lang.String"),
                        TargetType("java.lang.Boolean")
                    ),
                    primitive = false,
                    primitiveArray = false
                )),
            Item("with inline generics, extra whitespace",
                source = "Foo => mapping.Bar  <  java.lang.String  ,   java.lang.Boolean  >  ",
                expected = TypeMapping (
                    "Foo",
                    null,
                    "mapping.Bar",
                    listOf(
                        TargetType("java.lang.String"),
                        TargetType("java.lang.Boolean")
                    ),
                    primitive = false,
                    primitiveArray = false
                )),
            Item("with extra generics",
                source = "Foo => mapping.Bar",
                generics = listOf("java.lang.String", "java.lang.Boolean"),
                expected = TypeMapping (
                    "Foo",
                    null,
                    "mapping.Bar",
                    listOf(
                        TargetType("java.lang.String"),
                        TargetType("java.lang.Boolean")
                    ),
                    primitive = false,
                    primitiveArray = false
                )),
            Item("with extra generics, extra whitespace",
                source = "Foo => mapping.Bar",
                generics = listOf("  java.lang.String  ", "  java.lang.Boolean  "),
                expected = TypeMapping (
                    "Foo",
                    null,
                    "mapping.Bar",
                    listOf(
                        TargetType("java.lang.String"),
                        TargetType("java.lang.Boolean")
                    ),
                    primitive = false,
                    primitiveArray = false
                )),
            Item("primitive",
                source = "Foo => byte",
                expected = TypeMapping (
                    "Foo",
                    null,
                    "byte",
                    emptyList(),
                    primitive = true,
                    primitiveArray = false
                )),
            Item("primitive array",
                source = "Foo => byte[]",
                expected = TypeMapping (
                    "Foo",
                    null,
                    "byte",
                    emptyList(),
                    primitive = true,
                    primitiveArray = true
                ))
        ) { (_, source, generics, expected) ->
            val yaml = buildMapping(source, generics)

            // when:
            val mappings = MappingConverter(reader.read(yaml) as Mapping).convert().globalMappings

            // then:
            val typeMapping = mappings.findTypeMapping(
                typeMatcher(name = expected.sourceTypeName, format = expected.sourceTypeFormat),
                TypesStep())!!

            typeMapping.sourceTypeName shouldBe expected.sourceTypeName
            typeMapping.sourceTypeFormat shouldBe expected.sourceTypeFormat
            typeMapping.targetTypeName shouldBe expected.targetTypeName
            typeMapping.genericTypes.size shouldBe expected.genericTypes.size
            typeMapping.genericTypes.forEachIndexed { index, targetType ->
                targetType.typeName shouldBe expected.genericTypes[index].typeName
                targetType.genericTypes shouldBe expected.genericTypes[index].genericTypes
            }
            typeMapping.primitive shouldBe expected.primitive
            typeMapping.primitiveArray shouldBe expected.primitiveArray
        }
    }

    "missing global type mapping returns null" - {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           |  
           """.trimMargin()

        // when:
        val mappings = MappingConverter(reader.read(yaml) as Mapping).convert().globalMappings

        // then:
        val typeMapping = mappings.findTypeMapping(typeMatcher(name = "Foo"), TypesStep())

        typeMapping.shouldBeNull()
    }

    "duplicate global type mapping throws" - {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           |  
           |map:
           |  types:
           |    - type: Foo => io.openapiprocessor.Foo
           |    - type: Foo => io.openapiprocessor.Foo
           """.trimMargin()

        val mappings = MappingConverter(reader.read(yaml) as Mapping).convert().globalMappings

        shouldThrow<AmbiguousTypeMappingException> {
            mappings.findTypeMapping(typeMatcher(name = "Foo"), TypesStep())
        }
    }

    "read global annotation type mapping, skip object mapping" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           | 
           |map:
           |  types:
           |    - type: Foo @ io.openapiprocessor.Foo
           |    - type: object @ io.openapiprocessor.Object
           """.trimMargin()

        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert()

        val annotationMappings = mappings.globalMappings.findAnnotationTypeMapping(
            AnnotationTypeMatcher(MappingFinderQuery(type = "Foo")), TypesStep())

        annotationMappings shouldHaveSize 1
        val annotationMapping = annotationMappings.first()
        annotationMapping.sourceTypeName shouldBe  "Foo"
        annotationMapping.sourceTypeFormat.shouldBeNull()
        annotationMapping.annotation.type shouldBe "io.openapiprocessor.Foo"
        annotationMapping.annotation.parameters.shouldBeEmpty()
    }

    "multiple global annotation type mappings returns all" - {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           |  
           |map:
           |  types:
           |    - type: Foo @ io.openapiprocessor.Foo
           |    - type: Foo @ io.openapiprocessor.Bar
           |    - type: object @ io.openapiprocessor.Object
           """.trimMargin()

        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert()

        val annotationMappings = mappings.globalMappings.findAnnotationTypeMapping(
            AnnotationTypeMatcher(MappingFinderQuery(type = "Foo", allowObject = true)), TypesStep())

        annotationMappings shouldHaveSize 3
    }

    "missing annotation type mapping returns empty list" - {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           |  
           """.trimMargin()

        // when:
        val mappings = MappingConverter(reader.read(yaml) as Mapping).convert().globalMappings

        // then:
        val annotationMappings = mappings.findAnnotationTypeMapping(
            AnnotationTypeMatcher(MappingFinderQuery(name = "Foo")), TypesStep())

        annotationMappings.shouldBeEmpty()
    }

    "reads endpoint type mapping" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           | 
           |map:
           |  paths:
           |    /foo:
           |      types:
           |        - type: Foo => io.openapiprocessor.Foo
           |      
           |      get:
           |        types:
           |          - type: Foo => io.openapiprocessor.Foo2
           |
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert()
        val epMappings = mappings.endpointMappings["/foo"]


        // then:
        val typeMapping = epMappings?.findTypeMapping(
            query(path = "/foo", method = HttpMethod.POST, name = "Foo"), TypesStep())!!

        typeMapping.sourceTypeName shouldBe "Foo"
        typeMapping.sourceTypeFormat.shouldBeNull()
        typeMapping.targetTypeName shouldBe "io.openapiprocessor.Foo"

        val typeMappingGet = epMappings.findTypeMapping(
            query(path = "/foo", method = HttpMethod.GET, name = "Foo"), TypesStep())!!

        typeMappingGet.sourceTypeName shouldBe "Foo"
        typeMappingGet.sourceTypeFormat.shouldBeNull()
        typeMappingGet.targetTypeName shouldBe "io.openapiprocessor.Foo2"
    }

    "read endpoint annotation type mapping" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           | 
           |map:
           |  paths:
           |    /foo:
           |      types:
           |        - type: Foo @ io.openapiprocessor.Foo
           |      
           |      get:
           |        types:
           |          - type: Foo @ io.openapiprocessor.Foo2
           |
           """.trimMargin()

        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert()
        val repository = MappingRepository(endpointMappings = mappings.endpointMappings)

        val query = MappingFinderQuery(path = "/foo", method = HttpMethod.POST, type = "Foo")
        val annotationMappings = repository.findEndpointAnnotationTypeMapping(query, RootStep("", query))

        annotationMappings shouldHaveSize 1
        val annotationMapping = annotationMappings.first()
        annotationMapping.sourceTypeName shouldBe  "Foo"
        annotationMapping.sourceTypeFormat.shouldBeNull()
        annotationMapping.annotation.type shouldBe "io.openapiprocessor.Foo"
        annotationMapping.annotation.parameters.shouldBeEmpty()

        val queryGet = MappingFinderQuery(path = "/foo", method = HttpMethod.GET, type = "Foo")
        val annotationMappingsGet = repository.findEndpointAnnotationTypeMapping(queryGet, RootStep("", queryGet))

        annotationMappingsGet shouldHaveSize 1
        val annotationMappingGet = annotationMappingsGet.first()
        annotationMappingGet.sourceTypeName shouldBe  "Foo"
        annotationMappingGet.sourceTypeFormat.shouldBeNull()
        annotationMappingGet.annotation.type shouldBe "io.openapiprocessor.Foo2"
        annotationMappingGet.annotation.parameters.shouldBeEmpty()
    }
})
