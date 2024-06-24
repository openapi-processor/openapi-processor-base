package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.WithDataTestName
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.converter.mapping.TargetType
import io.openapiprocessor.core.converter.mapping.TypeMapping
import io.openapiprocessor.core.converter.mapping.matcher.TypeMatcher
import io.openapiprocessor.core.processor.MappingReader
import io.openapiprocessor.core.support.MappingSchema

class MappingConverterTypeSpec: FreeSpec({
    val reader = MappingReader()

//    enableTraceMapping()

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
            val mapping = reader.read (yaml) as Mapping
            val mappings = MappingConverter(mapping).convertX()

            // then:
            val typeMapping = mappings.findGlobalTypeMapping(
                TypeMatcher(MappingSchema(
                    name = expected.sourceTypeName,
                    format = expected.sourceTypeFormat))
            )!!

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

    // todo null
    // todo ambiguous
})
