/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.mapping.Annotation as MappingAnnotation
import io.openapiprocessor.core.converter.mapping.AnnotationTypeMapping
import io.openapiprocessor.core.converter.mapping.SimpleParameterValue
import io.openapiprocessor.core.extractImports
import io.openapiprocessor.core.model.Annotation
import io.openapiprocessor.core.model.datatypes.*
import io.openapiprocessor.core.support.datatypes.ObjectDataType
import io.openapiprocessor.core.support.datatypes.ListDataType
import io.openapiprocessor.core.support.datatypes.propertyDataType
import io.openapiprocessor.core.support.datatypes.propertyDataTypeString
import java.io.StringWriter
import java.util.*

class DataTypeWriterPojoSpec: StringSpec({
    this.isolationMode = IsolationMode.InstancePerTest

    val options = ApiOptions()
    val generatedWriter = SimpleGeneratedWriter(options)
    var writer = DataTypeWriterPojo(options, generatedWriter, BeanValidationFactory())
    val target = StringWriter()

    "writes @Generated annotation import" {
        val dataType = ObjectDataType("Foo", "pkg", linkedMapOf(
            Pair("foo", propertyDataTypeString())
        ))

        // when:
        writer.write(target, dataType)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import io.openapiprocessor.generated.support.Generated;"
    }

    "writes @Generated annotation" {
        val dataType = ObjectDataType("Foo", "pkg", linkedMapOf(
            Pair("foo", propertyDataTypeString())
        ))

        // when:
        writer.write(target, dataType)

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
        writer.write(target, dataType)

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
        writer.write(target, dataType)

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
        writer.write(target, dataType)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import java.util.List;"
    }

    "writes additional bean validation object annotation import" {
        options.beanValidation = true
        val validation = object : BeanValidationFactory() {
            override fun validate(dataType: ModelDataType): BeanValidationInfo {
                return BeanValidationInfoSimple(dataType, listOf(Annotation("foo.Foo")))
            }
        }

        writer = DataTypeWriterPojo(options, generatedWriter, validation)

        val dataType = ObjectDataType("Foo",
            "pkg", linkedMapOf("foo" to propertyDataTypeString()))

        // when:
        writer.write(target, dataType)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import foo.Foo;"
    }

    "writes additional bean validation object annotation" {
        options.beanValidation = true
        val validation = object : BeanValidationFactory() {
            override fun validate(dataType: ModelDataType): BeanValidationInfo {
                return BeanValidationInfoSimple(dataType, listOf(Annotation("foo.Foo")))
            }
        }
        writer = DataTypeWriterPojo(options, generatedWriter, validation)

        val dataType = ObjectDataType("Foo",
            "pkg", linkedMapOf("foo" to propertyDataTypeString()))

        // when:
        writer.write(target, dataType)

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
        writer.write (target, dataType)

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
        writer.write (target, dataType)

        target.toString() shouldContain ("public class Foo implements MarkerInterface {")
    }

    "writes additional object annotation import from annotation mapping" {
        options.typeMappings = listOf(
            AnnotationTypeMapping(
                "Foo", annotation = MappingAnnotation("foo.Bar")
            ))
        writer = DataTypeWriterPojo(options, generatedWriter, BeanValidationFactory())

        val dataType = ObjectDataType("Foo",
            "pkg", linkedMapOf("foo" to propertyDataTypeString()))

        // when:
        writer.write(target, dataType)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import foo.Bar;"
    }

    "writes additional object annotation from annotation mapping" {
        options.typeMappings = listOf(
            AnnotationTypeMapping(
                "Foo", annotation = MappingAnnotation(
                    "foo.Bar", linkedMapOf("bar" to SimpleParameterValue(""""rab""""))
                )
            ))
        writer = DataTypeWriterPojo(options, generatedWriter, BeanValidationFactory())

        val dataType = ObjectDataType("Foo",
            "pkg", linkedMapOf("foo" to propertyDataTypeString()))

        // when:
        writer.write(target, dataType)

        // then:
        target.toString() shouldContain
            """    
            |@Bar(bar = "rab")
            |@Generated
            |public class Foo {
            |
            """.trimMargin()
    }

    "writes additional annotation import from annotation mapping for a mapped property data type" {
        options.typeMappings = listOf(
            AnnotationTypeMapping(
                "Foo", annotation = MappingAnnotation("foo.Bar")
            ))
        writer = DataTypeWriterPojo(options, generatedWriter, BeanValidationFactory())

        val dataType = ObjectDataType("Object",
            "pkg", linkedMapOf(
                "foo" to propertyDataType(MappedDataType("MappedFoo", "pkg",
                    sourceDataType = ObjectDataType("Foo", "pkg")))
            ))

        // when:
        writer.write(target, dataType)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import foo.Bar;"
    }

    "writes additional annotation from annotation mapping for a mapped property data type" {
        options.typeMappings = listOf(
            AnnotationTypeMapping(
                "Foo", annotation = MappingAnnotation(
                    "foo.Bar", linkedMapOf("bar" to SimpleParameterValue(""""rab""""))
                )
            ))
        writer = DataTypeWriterPojo(options, generatedWriter, BeanValidationFactory())

        val dataType = ObjectDataType("Object",
            "pkg", linkedMapOf(
                "foo" to propertyDataType(MappedDataType("MappedFoo", "pkg",
                    sourceDataType = ObjectDataType("Foo", "pkg")))
            ))

        // when:
        writer.write(target, dataType)

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
        options.typeMappings = listOf(
            AnnotationTypeMapping(
                "string", "uuid", annotation = MappingAnnotation(
                    "foo.Bar", linkedMapOf("bar" to SimpleParameterValue(""""rab""""))
                )
            ))
        writer = DataTypeWriterPojo(options, generatedWriter, BeanValidationFactory())

        val dataType = ObjectDataType("Object",
            "pkg", linkedMapOf(
                "foo" to propertyDataType(
                    MappedDataType("Uuid", "java.util",
                        sourceDataType = StringDataType("string:uuid")))
            ))

        // when:
        writer.write(target, dataType)

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
        options.typeMappings = listOf(
            AnnotationTypeMapping(
                "object", annotation = MappingAnnotation("foo.Bar", linkedMapOf())
            )
        )

        writer = DataTypeWriterPojo(options, generatedWriter, BeanValidationFactory())

        val dataType = ObjectDataType("Object",
            "pkg", linkedMapOf(
                "foo" to propertyDataType(ObjectDataType("Foo", "model")))
            )

        // when:
        writer.write(target, dataType)

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
        options.typeMappings = listOf(
            AnnotationTypeMapping(
                "Foo", annotation = MappingAnnotation("foo.Bar", linkedMapOf())
            )
        )

        writer = DataTypeWriterPojo(options, generatedWriter, BeanValidationFactory())

        val dataType = ObjectDataType("Object",
            "pkg", linkedMapOf(
                "foo" to propertyDataType(ObjectDataType("Foo", "model")))
            )

        // when:
        writer.write(target, dataType)

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

})
