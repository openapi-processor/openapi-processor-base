/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.mapping.ParameterValue
import io.openapiprocessor.core.converter.mapping.SimpleParameterValue
import io.openapiprocessor.core.model.Annotation
import io.openapiprocessor.core.model.datatypes.*
import org.apache.commons.text.StringEscapeUtils.escapeJava

/**
 * creates bean validation imports and annotations.
 */
open class BeanValidationFactory(
    private val options: ApiOptions
) {
    val validations: BeanValidations = BeanValidations(getValidationFormat())
    val targetTypes: BeanValidationSupportedTypes = BeanValidationSupportedTypes(
        options.beanValidationAditionalSupportedTypes)

    /**
     * override to add annotations to the model object class.
     */
    open fun validate(dataType: ModelDataType): BeanValidationInfo {
        return BeanValidationInfoSimple(dataType, emptyList())
    }

    fun validate(dataType: DataType, required: Boolean = false, parentHasValid: Boolean = false): BeanValidationInfo {
        return if (dataType is CollectionDataType) {
            val (annotations, valid) = collectAnnotationsWithValid(dataType, required, parentHasValid)

            BeanValidationInfoCollection(
                dataType,
                annotations,
                validate(dataType.item, parentHasValid = valid)
            )
        } else {
            BeanValidationInfoSimple(
                dataType,
                collectAnnotations(dataType, required, parentHasValid)
            )
        }
    }

    private fun collectAnnotations(dataType: DataType, required: Boolean = false, parentHasValid: Boolean): List<Annotation> {
        return collectAnnotationsWithValid(dataType, required, parentHasValid).first
    }

    private fun collectAnnotationsWithValid(dataType: DataType, required: Boolean = false, parentHasValid: Boolean): Pair<List<Annotation>, Boolean>  {
        val annotations = mutableListOf<Annotation>()
        var valid = false

        if (!parentHasValid && dataType.shouldHaveValid(options)) {
            annotations.add(Annotation(validations.VALID))
            valid = true
        }

        val sourceDataType = getSourceDataType(dataType)
        if (required) {
            annotations.add(Annotation(validations.NOT_NULL))
        }

        if (hasSizeConstraints(dataType)) {
            annotations.add(createSizeAnnotation(sourceDataType))
        }

        if (hasMinConstraint(dataType)) {
            annotations.add(createDecimalMinAnnotation (sourceDataType))
        }

        if (hasMaxConstraint(dataType)) {
            annotations.add(createDecimalMaxAnnotation (sourceDataType))
        }

        if (hasPatternConstraint(dataType)) {
            annotations.add(createPatternAnnotation(sourceDataType))
        }

        if (hasEmailConstraint(dataType)) {
            annotations.add(createEmailAnnotation())
        }

        if (sourceDataType.valuesConstraint()) {
            annotations.add(createValuesAnnotation(dataType))
        }

        return Pair(annotations, valid)
    }

    private fun getSourceDataType(dataType: DataType): DataType {
        if (dataType is SourceDataType && dataType.sourceDataType != null) {
            return dataType.sourceDataType!!
        }

        return dataType
    }

    private fun hasSizeConstraints(dataType: DataType): Boolean {
        val sourceDataType = getSourceDataType(dataType)
        val constraint = sourceDataType.hasSizeConstraints()
        if (sourceDataType == dataType || !constraint) {
            return constraint
        }

        return targetTypes.supports(validations.SIZE, dataType)
    }

    private fun hasMinConstraint(dataType: DataType): Boolean {
        val sourceDataType = getSourceDataType(dataType)
        val constraint = sourceDataType.hasMinConstraint()
        if (sourceDataType == dataType || !constraint) {
            return constraint
        }

        return targetTypes.supports(validations.DECIMAL_MIN, dataType)
    }

    private fun hasMaxConstraint(dataType: DataType): Boolean {
        val sourceDataType = getSourceDataType(dataType)
        val constraint = sourceDataType.hasMaxConstraint()
        if (sourceDataType == dataType || !constraint) {
            return constraint
        }

        if(!sourceDataType.hasMaxConstraint()) {
            return false
        }

        return targetTypes.supports(validations.DECIMAL_MAX, dataType)
    }

    private fun hasPatternConstraint(dataType: DataType): Boolean {
        val sourceDataType = getSourceDataType(dataType)
        val constraint = sourceDataType.patternConstraint()
        if (sourceDataType == dataType || !constraint) {
            return constraint
        }

        return targetTypes.supports(validations.PATTERN, dataType)
    }

    private fun hasEmailConstraint(dataType: DataType): Boolean {
        val sourceDataType = getSourceDataType(dataType)
        val constraint = sourceDataType.emailConstraint()
        if (sourceDataType == dataType || !constraint) {
            return constraint
        }

        return targetTypes.supports(validations.EMAIL, dataType)
    }

    private fun createDecimalMinAnnotation(dataType: DataType): Annotation {
        val parameters = linkedMapOf<String, ParameterValue>()

        val minimum = dataType.constraints?.minimumConstraint!!
        parameters["value"] = SimpleParameterValue(""""${minimum.value}"""")

        if (minimum.exclusive) {
            parameters["inclusive"] = SimpleParameterValue("false")
        }

        return Annotation(validations.DECIMAL_MIN, parameters)
    }

    private fun createDecimalMaxAnnotation(dataType: DataType): Annotation {
        val parameters = linkedMapOf<String, ParameterValue>()

        val maximum = dataType.constraints?.maximumConstraint!!
        parameters["value"] = SimpleParameterValue(""""${maximum.value}"""")

        if (maximum.exclusive) {
            parameters["inclusive"] = SimpleParameterValue("false")
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
        val parameters = linkedMapOf<String, ParameterValue>()

        if (size.hasMin) {
            parameters["min"] = SimpleParameterValue("${size.min}")
        }

        if (size.hasMax) {
            parameters["max"] = SimpleParameterValue("${size.max}")
        }

        return Annotation(validations.SIZE, parameters)
    }

    private fun createPatternAnnotation(dataType: DataType): Annotation {
        val parameters = linkedMapOf<String, ParameterValue>()
        parameters["regexp"] = SimpleParameterValue(""""${escapeJava(dataType.constraints?.pattern!!)}"""")
        return Annotation(validations.PATTERN, parameters)
    }

    private fun createEmailAnnotation(): Annotation {
        return Annotation(validations.EMAIL)
    }

    private fun createValuesAnnotation(dataType: DataType): Annotation {
        val parameters = linkedMapOf<String, ParameterValue>()

        val params = StringBuilder()
        params.append("{")
        params.append(dataType.constraints!!.values.joinToString(", ") { """"$it"""" })
        params.append("}")

        parameters["values"] = SimpleParameterValue(params.toString())
        return Annotation("${options.packageName}.${validations.VALUES}", parameters)
    }

    private fun getValidationFormat(): BeanValidationFormat {
        val format = options.beanValidationFormat
        return if (format != null)
            BeanValidationFormat.valueOf(format.uppercase())
        else
            BeanValidationFormat.JAVAX
    }
}

private fun DataType.shouldHaveValid(options: ApiOptions): Boolean {
    return when {
        this is SingleDataType && options.beanValidationValidOnReactive -> true
        this is ModelDataType -> true
        this is ArrayDataType -> item is ModelDataType
        this is InterfaceDataType -> true
        this is MappedCollectionDataType && options.beanValidationValidOnReactive -> multi
        this is MappedCollectionDataType -> false
        this is SourceDataType -> sourceDataType?.shouldHaveValid(options) ?: false
        else -> false
    }
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

private fun DataType.valuesConstraint(): Boolean = isString() && constraints?.values?.isNotEmpty() == true
