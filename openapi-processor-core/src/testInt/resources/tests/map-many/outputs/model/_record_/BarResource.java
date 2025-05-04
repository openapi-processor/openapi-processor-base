package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import io.openapiprocessor.AnnotationA;
import io.openapiprocessor.AnnotationB;

@Generated(value = "openapi-processor-core", version = "test")
public record BarResource(
    @AnnotationA @JsonProperty("bar1") Integer bar1,
    @AnnotationB @JsonProperty("bar2") Integer bar2) {
}
