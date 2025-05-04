package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import io.openapiprocessor.AnnotationA;
import io.openapiprocessor.AnnotationC;
import jakarta.validation.constraints.DecimalMin;
import java.util.UUID;

@AnnotationC(UUID.class)
@Generated(value = "openapi-processor-core", version = "test")
public record FooResource(
    @DecimalMin(value = "0") @AnnotationA @JsonProperty("foo1") Integer foo1,
    @DecimalMin(value = "-10") @AnnotationA @JsonProperty("foo2") Integer foo2) {
}
