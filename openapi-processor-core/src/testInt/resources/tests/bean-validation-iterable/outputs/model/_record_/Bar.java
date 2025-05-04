package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import javax.validation.constraints.Size;

@Generated(value = "openapi-processor-core", version = "test")
public record Bar(
    @Size(max = 10)
    @JsonProperty("bar")
    String bar
) {}
