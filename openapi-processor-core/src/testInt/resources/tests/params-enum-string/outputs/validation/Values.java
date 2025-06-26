package generated.validation;

import generated.support.Generated;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValueValidator.class)
@Documented
@Generated(value = "openapi-processor-core", version = "test")
public @interface Values {
    String message() default "Invalid value. Should be one of values.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String[] values() default {};
}
