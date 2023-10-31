package generated.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValueValidator.class)
@Documented
public @interface Values {
    String message() default "Invalid value. Should be one of values.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String[] values() default {};
}
