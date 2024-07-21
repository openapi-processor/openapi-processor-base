/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.JsonPropertyAnnotationMode
import io.openapiprocessor.core.converter.mapping.*
import io.openapiprocessor.core.extractImports
import io.openapiprocessor.core.model.Annotation
import io.openapiprocessor.core.model.datatypes.*
import io.openapiprocessor.core.support.datatypes.ListDataType
import io.openapiprocessor.core.support.datatypes.ObjectDataType
import io.openapiprocessor.core.support.datatypes.propertyDataType
import io.openapiprocessor.core.support.datatypes.propertyDataTypeString
import io.openapiprocessor.core.support.parseOptions
import io.openapiprocessor.core.support.parseOptionsMapping
import java.io.StringWriter
import java.util.*
import io.openapiprocessor.core.converter.mapping.Annotation as MappingAnnotation

class DataTypeWriterPojoSpec: StringSpec({
    this.isolationMode = IsolationMode.InstancePerTest

    val options = ApiOptions()
    val identifier = JavaIdentifier()
    val generatedWriter = SimpleGeneratedWriter(options)
    val target = StringWriter()

    fun createWriter(validations: BeanValidationFactory = BeanValidationFactory(options)): DataTypeWriterPojo {
        return DataTypeWriterPojo(options, identifier, generatedWriter, validations)
    }

    fun writer(opts: ApiOptions = options): DataTypeWriterPojo {
        return DataTypeWriterPojo(
            opts,
            identifier,
            generatedWriter,
            BeanValidationFactory(opts))
    }

    "writes @Generated annotation import" {
        val dataType = ObjectDataType("Foo", "pkg", linkedMapOf(
            Pair("foo", propertyDataTypeString())
        ))

        // when:
        createWriter().write(target, dataType)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import io.openapiprocessor.generated.support.Generated;"
    }

    "writes @Generated annotation" {
        val dataType = ObjectDataType("Foo", "pkg", linkedMapOf(
            Pair("foo", propertyDataTypeString())
        ))

        // when:
        createWriter().write(target, dataType)

        // then:
        target.toString() shouldContain
            """    
            |@Generated
            |public class Foo {
            |
            """.trimMargin()
    }

    "writes @NotNull import for required property" {
        options.beanValidation = true

        val dataType = ObjectDataType("Foo", "pkg", linkedMapOf(
            Pair("foo", propertyDataTypeString())
        ), DataTypeConstraints(required = listOf("foo")), false)

        // when:
        createWriter().write(target, dataType)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import javax.validation.constraints.NotNull;"
    }

    "writes required property with @NotNull annotation" {
        options.beanValidation = true

        val dataType = ObjectDataType("Foo", "pkg", linkedMapOf(
            Pair("foo", propertyDataTypeString())
        ), DataTypeConstraints(required = listOf("foo")), false)

        // when:
        createWriter().write(target, dataType)

        // then:
        target.toString() shouldContain
            """    
            |    @NotNull
            |    @JsonProperty("foo")
            |    private String foo;
            |
            """.trimMargin()
    }

    "writes import of nested generic list type" {
        val dataType = ObjectDataType("Foo", "pkg",
            linkedMapOf("foos" to propertyDataType(ListDataType(StringDataType()))
        ))

        // when:
        createWriter().write(target, dataType)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import java.util.List;"
    }

    "writes additional bean validation object annotation import" {
        options.beanValidation = true
        val validations = object : BeanValidationFactory(options) {
            override fun validate(dataType: ModelDataType): BeanValidationInfo {
                return BeanValidationInfoSimple(dataType, listOf(Annotation("foo.Foo")))
            }
        }

        val dataType = ObjectDataType("Foo",
            "pkg", linkedMapOf("foo" to propertyDataTypeString()))

        // when:
        createWriter(validations).write(target, dataType)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import foo.Foo;"
    }

    "writes additional bean validation object annotation" {
        options.beanValidation = true
        val validations = object : BeanValidationFactory(options) {
            override fun validate(dataType: ModelDataType): BeanValidationInfo {
                return BeanValidationInfoSimple(dataType, listOf(Annotation("foo.Foo")))
            }
        }

        val dataType = ObjectDataType("Foo",
            "pkg", linkedMapOf("foo" to propertyDataTypeString()))

        // when:
        createWriter(validations).write(target, dataType)

        // then:
        target.toString() shouldContain
            """    
            |@Foo
            |@Generated
            |public class Foo {
            |
            """.trimMargin()
    }

    "writes properties with @JsonProperty access annotation" {
        val dataType = ObjectDataType ("Foo", "pkg", linkedMapOf(
            "foo" to PropertyDataType (
                readOnly = true,
                writeOnly = false,
                dataType = StringDataType ()
            ),
            "bar" to PropertyDataType (
                readOnly = false,
                writeOnly = true,
                dataType = StringDataType ()
            )
        ))

        // when:
        createWriter().write (target, dataType)

        // then:
        target.toString () shouldContain
            """
            |    @JsonProperty(value = "foo", access = JsonProperty.Access.READ_ONLY)
            |    private String foo;
            |
            |    @JsonProperty(value = "bar", access = JsonProperty.Access.WRITE_ONLY)
            |    private String bar;
            """.trimMargin()
    }

    "writes class with implements" {
        options.oneOfInterface = true

        val dataType = ObjectDataType ("Foo", "pkg", linkedMapOf(
            "foo" to PropertyDataType (
                readOnly = false,
                writeOnly = false,
                dataType = StringDataType ()
            )
        ))

        val ifDataType = InterfaceDataType(
            DataTypeName("MarkerInterface"), "pkg", listOf(dataType))

        dataType.implementsDataType = ifDataType

        // when:
        createWriter().write (target, dataType)

        target.toString() shouldContain ("public class Foo implements MarkerInterface {")
    }

    "writes additional object annotation import from annotation mapping" {
        val opts = parseOptions(
            """
            |openapi-processor-mapping: v8
            |options:
            |  package-name: pkg
            |map:
            |  types:
            |    - type: Foo @ foo.Bar
            """.trimMargin())

        val dataType = ObjectDataType("Foo",
            "pkg", linkedMapOf("foo" to propertyDataTypeString()))

        // when:
        writer(opts).write(target, dataType)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import foo.Bar;"
    }

    "writes additional object annotation from annotation mapping" {
        options.globalMappings = Mappings(
            typeMappings = TypeMappings(
                AnnotationTypeMappingDefault(
                    sourceTypeName = "Foo",
                    annotation = MappingAnnotation(
                        "foo.Bar", linkedMapOf("bar" to SimpleParameterValue(""""rab""""))))))

        val dataType = ObjectDataType("Foo",
            "pkg", linkedMapOf("foo" to propertyDataTypeString()))

        // when:
        createWriter().write(target, dataType)

        // then:
        target.toString() shouldContain
            """    
            |@Bar(bar = "rab")
            |@Generated
            |public class Foo {
            |
            """.trimMargin()
    }

    "writes additional object annotation from annotation mapping with model name suffix" {
        options.modelNameSuffix = "X"
        options.globalMappings = Mappings(
            typeMappings = TypeMappings(
                AnnotationTypeMappingDefault(
                    sourceTypeName = "Foo",
                    annotation = MappingAnnotation(
                        "foo.Bar", linkedMapOf()
                    ))))

        val dataType = ObjectDataType(DataTypeName("Foo", "FooX"),
            "pkg", linkedMapOf("foo" to propertyDataTypeString()))

        // when:
        createWriter().write(target, dataType)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import foo.Bar;"

        target.toString() shouldContain
            """
            |@Bar
            |@Generated
            |public class FooX {
            |
            """.trimMargin()
    }

    "writes additional annotation import from annotation mapping for a mapped property data type" {
        options.globalMappings = Mappings(
            typeMappings = TypeMappings(
                AnnotationTypeMappingDefault(
                    sourceTypeName = "Foo",
                    annotation = MappingAnnotation("foo.Bar")
                )
            ))

        val dataType = ObjectDataType("Object",
            "pkg", linkedMapOf(
                "foo" to propertyDataType(
                    MappedDataType(
                        "MappedFoo",
                        "pkg",
                        sourceDataType = ObjectDataType("Foo", "pkg")))
            ))

        // when:
        createWriter().write(target, dataType)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import foo.Bar;"
    }

    "writes additional annotation from annotation mapping for a mapped property data type" {
        options.globalMappings = Mappings(
            typeMappings = TypeMappings(
                AnnotationTypeMappingDefault(
                    sourceTypeName = "Foo",
                    annotation = MappingAnnotation(
                        "foo.Bar", linkedMapOf("bar" to SimpleParameterValue(""""rab"""")))
            )))

        val dataType = ObjectDataType("Object",
            "pkg", linkedMapOf(
                "foo" to propertyDataType(MappedDataType("MappedFoo", "pkg",
                    sourceDataType = ObjectDataType("Foo", "pkg")))
            ))

        // when:
        createWriter().write(target, dataType)

        // then:
        target.toString() shouldContain
            """    
            |@Generated
            |public class Object {
            |
            |    @Bar(bar = "rab")
            |    @JsonProperty("foo")
            |    private MappedFoo foo;
            |
            |    public MappedFoo getFoo() {
            |        return foo;
            |    }
            |
            |    public void setFoo(MappedFoo foo) {
            |        this.foo = foo;
            |    }
            |
            |}
            """.trimMargin()
    }

    "writes additional annotation from annotation mapping for a simple mapped property data type" {
        options.globalMappings = Mappings(
            typeMappings = TypeMappings(
                AnnotationTypeMappingDefault(
                    sourceTypeName = "string",
                    sourceTypeFormat = "uuid",
                    annotation = MappingAnnotation(
                        "foo.Bar", linkedMapOf("bar" to SimpleParameterValue(""""rab"""")))
            )))

        val dataType = ObjectDataType("Object",
            "pkg", linkedMapOf(
                "foo" to propertyDataType(
                    MappedDataType("Uuid", "java.util",
                        sourceDataType = StringDataType("string:uuid")))
            ))

        // when:
        createWriter().write(target, dataType)

        // then:
        target.toString() shouldContain
            """    
            |@Generated
            |public class Object {
            |
            |    @Bar(bar = "rab")
            |    @JsonProperty("foo")
            |    private Uuid foo;
            |
            |    public Uuid getFoo() {
            |        return foo;
            |    }
            |
            |    public void setFoo(Uuid foo) {
            |        this.foo = foo;
            |    }
            |
            |}
            """.trimMargin()
    }

    "writes additional annotation from 'object' annotation mapping" {
        options.globalMappings = Mappings(
            typeMappings = TypeMappings(
                AnnotationTypeMappingDefault(
                    sourceTypeName = "object",
                    annotation = MappingAnnotation(
                        "foo.Bar", linkedMapOf()
                    ))))

        val dataType = ObjectDataType("Object",
            "pkg", linkedMapOf(
                "foo" to propertyDataType(ObjectDataType("Foo", "model")))
            )

        // when:
        createWriter().write(target, dataType)

        // then:
        target.toString() shouldContain
            """    
            |@Bar
            |@Generated
            |public class Object {
            |
            |    @JsonProperty("foo")
            |    private Foo foo;
            |
            |    public Foo getFoo() {
            |        return foo;
            |    }
            |
            |    public void setFoo(Foo foo) {
            |        this.foo = foo;
            |    }
            |
            |}
            """.trimMargin()
    }

    "skips additional annotation from annotation mapping for un-mapped object datatype property" {
        val options = parseOptionsMapping("""
            |mapping:
            |  types:
            |    - type: Foo @ foo.Bar
            """)

        val dataType = ObjectDataType("Object",
            "pkg", linkedMapOf(
                "foo" to propertyDataType(ObjectDataType("Foo", "model")))
            )

        // when:
        writer(options).write(target, dataType)

        // then:
        val t1 = target.toString()
        val t2 =
            """package pkg;
            |
            |import com.fasterxml.jackson.annotation.JsonProperty;
            |import io.openapiprocessor.generated.support.Generated;
            |import model.Foo;
            |
            |@Generated
            |public class Object {
            |
            |    @JsonProperty("foo")
            |    private Foo foo;
            |
            |    public Foo getFoo() {
            |        return foo;
            |    }
            |
            |    public void setFoo(Foo foo) {
            |        this.foo = foo;
            |    }
            |
            |}
            |
            """.trimMargin()

        Arrays.mismatch(t1.toByteArray(), t2.toByteArray()).shouldBe(-1)
    }

    "writes additional property annotation from extension mapping" {
        options.extensionMappings = mapOf(
            "x-foo" to ExtensionMappings(
                mapOf("ext" to listOf(
                    AnnotationNameMappingDefault(
                        "ext", annotation = MappingAnnotation("annotation.Extension", linkedMapOf())
                    )))),

            "x-bar" to ExtensionMappings(
                mapOf(
                    "barA" to listOf(
                        AnnotationNameMappingDefault(
                            "barA", annotation = MappingAnnotation("annotation.BarA", linkedMapOf())
                        )),
                    "barB" to listOf(
                        AnnotationNameMappingDefault(
                            "barB", annotation = MappingAnnotation("annotation.BarB", linkedMapOf())
                        ))
                    ))
            )

        val dataType = ObjectDataType("Foo", "pkg", linkedMapOf(
                "foo" to propertyDataType(StringDataType(), mapOf(
                    "x-foo" to "ext",
                    "x-bar" to listOf("barA", "barB")
                ))))

        // when:
        createWriter().write(target, dataType)

        // then:
        val t1 = target.toString()
        val t2 =
            """package pkg;
            |
            |import annotation.BarA;
            |import annotation.BarB;
            |import annotation.Extension;
            |import com.fasterxml.jackson.annotation.JsonProperty;
            |import io.openapiprocessor.generated.support.Generated;
            |
            |@Generated
            |public class Foo {
            |
            |    @Extension
            |    @BarA
            |    @BarB
            |    @JsonProperty("foo")
            |    private String foo;
            |
            |    public String getFoo() {
            |        return foo;
            |    }
            |
            |    public void setFoo(String foo) {
            |        this.foo = foo;
            |    }
            |
            |}
            |
            """.trimMargin()

        t1 shouldBeEqual t2
        Arrays.mismatch(t1.toByteArray(), t2.toByteArray()).shouldBe(-1)
    }

    "does not add @JsonProperty annotation if OpenAPI property name = java property name" {
        options.jsonPropertyAnnotation = JsonPropertyAnnotationMode.Auto

        val dataType = ObjectDataType("Foo", "pkg", linkedMapOf(
            Pair("foo", propertyDataTypeString())
        ))

        // when:
        createWriter().write(target, dataType)

        target.toString() shouldContain
            """package pkg;
            |
            |import io.openapiprocessor.generated.support.Generated;
            |
            |@Generated
            |public class Foo {
            |
            |    private String foo;
            |
            |    public String getFoo() {
            |        return foo;
            |    }
            |
            |    public void setFoo(String foo) {
            |        this.foo = foo;
            |    }
            |
            |}
            |
            """.trimMargin()
    }

    "does add @JsonProperty annotation if OpenAPI property name != java property name" {
        options.jsonPropertyAnnotation = JsonPropertyAnnotationMode.Auto

        val dataType = ObjectDataType("Foo", "pkg", linkedMapOf(
            Pair("1foo", propertyDataTypeString())
        ))

        // when:
        createWriter().write(target, dataType)

        target.toString() shouldContain
            """package pkg;
            |
            |import com.fasterxml.jackson.annotation.JsonProperty;
            |import io.openapiprocessor.generated.support.Generated;
            |
            |@Generated
            |public class Foo {
            |
            |    @JsonProperty("1foo")
            |    private String foo;
            |
            |    public String getFoo() {
            |        return foo;
            |    }
            |
            |    public void setFoo(String foo) {
            |        this.foo = foo;
            |    }
            |
            |}
            |
            """.trimMargin()
    }

    "does not add @JsonProperty annotation if OpenAPI property name != java property name" {
        options.jsonPropertyAnnotation = JsonPropertyAnnotationMode.Never

        val dataType = ObjectDataType("Foo", "pkg", linkedMapOf(
            Pair("1foo", propertyDataTypeString())
        ))

        // when:
        createWriter().write(target, dataType)

        target.toString() shouldContain
            """package pkg;
            |
            |import io.openapiprocessor.generated.support.Generated;
            |
            |@Generated
            |public class Foo {
            |
            |    private String foo;
            |
            |    public String getFoo() {
            |        return foo;
            |    }
            |
            |    public void setFoo(String foo) {
            |        this.foo = foo;
            |    }
            |
            |}
            |
            """.trimMargin()
    }

    "does add @JsonProperty annotation if OpenAPI property is read only" {
        options.jsonPropertyAnnotation = JsonPropertyAnnotationMode.Auto

        val dataType = ObjectDataType("Foo", "pkg", linkedMapOf(
            Pair("foo", propertyDataTypeString(readOnly = true))
        ))

        // when:
        createWriter().write(target, dataType)

        target.toString() shouldContain
            """package pkg;
            |
            |import com.fasterxml.jackson.annotation.JsonProperty;
            |import io.openapiprocessor.generated.support.Generated;
            |
            |@Generated
            |public class Foo {
            |
            |    @JsonProperty(value = "foo", access = JsonProperty.Access.READ_ONLY)
            |    private String foo;
            |
            |    public String getFoo() {
            |        return foo;
            |    }
            |
            |    public void setFoo(String foo) {
            |        this.foo = foo;
            |    }
            |
            |}
            |
            """.trimMargin()
    }
})
