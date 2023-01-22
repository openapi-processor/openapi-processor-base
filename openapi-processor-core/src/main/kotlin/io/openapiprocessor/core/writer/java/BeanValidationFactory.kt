/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.model.datatypes.*
import org.apache.commons.text.StringEscapeUtils.escapeJava

/**
 * creates bean validation imports and annotations.
 */
open class BeanValidationFactory(
    val format: BeanValidationFormat = BeanValidationFormat.JAVAX
) {
    val validations: BeanValidations = BeanValidations(format)

    /**
     * override to add annotations to the model object class.
     */
    open fun validate(dataType: ModelDataType): BeanValidationInfo {
        return BeanValidationInfoSimple(dataType, emptyList())
    }

    fun validate(dataType: DataType, required: Boolean = false): BeanValidationInfo {
        return if (dataType is CollectionDataType) {
            BeanValidationInfoCollection(
                dataType,
                collectAnnotations(dataType, required),
                validate(dataType.item, false)
            )
        } else {
            BeanValidationInfoSimple(
                dataType,
                collectAnnotations(dataType, required)
            )
        }
    }

    private fun collectAnnotations(dataType: DataType, required: Boolean = false): List<Annotation>  {
        val annotations = mutableListOf<Annotation>()

        if (dataType.shouldHaveValid()) {
            annotations.add(Annotation(validations.VALID))
        }

        val sourceDataType = getSourceDataType(dataType)
        if (required) {
            annotations.add(Annotation(validations.NOT_NULL))
        }

        if (sourceDataType.hasSizeConstraints()) {
            annotations.add(createSizeAnnotation(sourceDataType))
        }

        if (sourceDataType.hasMinConstraint()) {
            annotations.add(createDecimalMinAnnotation (sourceDataType))
        }

        if (sourceDataType.hasMaxConstraint()) {
            annotations.add(createDecimalMaxAnnotation (sourceDataType))
        }

        if (sourceDataType.patternConstraint()) {
            annotations.add(createPatternAnnotation(sourceDataType))
        }

        if (sourceDataType.emailConstraint()) {
            annotations.add(createEmailAnnotation())
        }

        return annotations
    }

    private fun getSourceDataType(dataType: DataType): DataType {
        if (dataType is MappedSourceDataType && dataType.sourceDataType != null) {
            return dataType.sourceDataType!!
        }

        return dataType
    }

    private fun createDecimalMinAnnotation(dataType: DataType): Annotation {
        val parameters = linkedMapOf<String, String>()

        val minimum = dataType.constraints?.minimumConstraint!!
        parameters["value"] = """"${minimum.value}""""

        if (minimum.exclusive) {
            parameters["inclusive"] = "false"
        }

        return Annotation(validations.DECIMAL_MIN, parameters)
    }

    private fun createDecimalMaxAnnotation(dataType: DataType): Annotation {
        val parameters = linkedMapOf<String, String>()

        val maximum = dataType.constraints?.maximumConstraint!!
        parameters["value"] = """"${maximum.value}""""

        if (maximum.exclusive) {
            parameters["inclusive"] = "false"
        }

        return Annotation(validations.DECIMAL_MAX, parameters)
    }

    private fun createSizeAnnotation(dataType: DataType): Annotation {
        return if (dataType.isString()) {
            createSizeAnnotation(dataType.lengthConstraints())
        } else {
            createSizeAnnotation(dataType.itemConstraints())
        }
    }

    private fun createSizeAnnotation(size: SizeConstraints): Annotation {
        val parameters = linkedMapOf<String, String>()

        if (size.hasMin) {
            parameters["min"] = "${size.min}"
        }

        if (size.hasMax) {
            parameters["max"] = "${size.max}"
        }

        return Annotation(validations.SIZE, parameters)
    }

    private fun createPatternAnnotation(dataType: DataType): Annotation {
        val parameters = linkedMapOf<String, String>()
        parameters["regexp"] = """"${escapeJava(dataType.constraints?.pattern!!)}""""
        return Annotation(validations.PATTERN, parameters)
    }

    private fun createEmailAnnotation(): Annotation {
        return Annotation(validations.EMAIL)
    }
}

private fun DataType.shouldHaveValid(): Boolean {
    if (this is ModelDataType)
        return true

    if (this is ArrayDataType)
        return item is ModelDataType

    if (this is InterfaceDataType)
        return true

    if (this is MappedCollectionDataType)
        return false

    if (this is MappedSourceDataType) {
        return sourceDataType?.shouldHaveValid() ?: false
    }

    return false
}

private fun DataType.isString(): Boolean = this is StringDataType

private fun DataType.isCollection(): Boolean =
      this is ArrayDataType
   || this is MappedCollectionDataType

private fun DataType.isNumber(): Boolean =
      this is FloatDataType
   || this is DoubleDataType
   || this is IntegerDataType
   || this is LongDataType

private fun DataType.hasArrayConstraints(): Boolean = constraints?.hasItemConstraints() ?: false

private fun DataType.hasLengthConstraints(): Boolean = constraints?.hasLengthConstraints() ?: false

private fun DataType.hasSizeConstraints(): Boolean =
       (isCollection() && hasArrayConstraints())
    || (isString() && hasLengthConstraints())

private fun DataType.hasMinConstraint(): Boolean = isNumber() && constraints?.minimum != null

private fun DataType.hasMaxConstraint(): Boolean = isNumber() && constraints?.maximum != null

private fun DataType.lengthConstraints(): SizeConstraints = constraints?.lengthConstraints!!

private fun DataType.itemConstraints(): SizeConstraints = constraints?.itemConstraints!!

private fun DataType.patternConstraint(): Boolean = constraints?.pattern != null

private fun DataType.emailConstraint(): Boolean = "email" == constraints?.format
