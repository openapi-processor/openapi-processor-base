package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import javax.validation.constraints.Size;

@Generated(value = "openapi-processor-core", version = "test")
public record SpecificPropertiesTwo(
    @Size(max = 100)
    @JsonProperty("bar")
    String bar
) implements GenericProperties {}
