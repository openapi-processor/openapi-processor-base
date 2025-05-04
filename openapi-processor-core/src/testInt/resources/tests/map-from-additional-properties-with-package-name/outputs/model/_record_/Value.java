package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import javax.validation.Valid;

@Generated(value = "openapi-processor-core", version = "test")
public record Value(
    @JsonProperty("text") String text, @Valid @JsonProperty("nested") NestedValue nested) {
}
