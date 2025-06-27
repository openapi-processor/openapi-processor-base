package generated.validation;

import generated.support.Generated;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Set;

@Generated(value = "openapi-processor-core", version = "test")
public class ValueValidator implements ConstraintValidator<Values, String> {
    private Set<String> values;

    @Override
    public void initialize (Values constraintAnnotation) {
        values = Set.copyOf(Arrays.asList(constraintAnnotation.values()));
    }

    @Override
    public boolean isValid (String value, ConstraintValidatorContext context) {
        return value != null && values.contains(value);
    }
}
