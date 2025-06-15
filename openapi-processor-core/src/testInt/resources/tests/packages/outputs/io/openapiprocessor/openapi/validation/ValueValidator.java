package io.openapiprocessor.openapi.validation;

import io.openapiprocessor.openapi.support.Generated;
import java.util.Arrays;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Generated(value = "openapi-processor-core", version = "test")
public class ValueValidator implements ConstraintValidator<Values, String> {
    private String[] values;

    @Override
    public void initialize (Values constraintAnnotation) {
        values = constraintAnnotation.values();
    }

    @Override
    public boolean isValid (String value, ConstraintValidatorContext context) {
        return value != null && Arrays.asList(values).contains(value);
    }
}
