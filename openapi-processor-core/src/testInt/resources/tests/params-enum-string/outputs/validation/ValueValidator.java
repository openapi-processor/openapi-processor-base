package generated.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

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
