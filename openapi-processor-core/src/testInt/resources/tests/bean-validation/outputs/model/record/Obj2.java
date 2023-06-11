package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import javax.validation.constraints.Size;

@Generated(value = "openapi-processor-core", version = "test")
public record Obj2(
    @Size(max = 10)
    @JsonProperty("prop4")
    String prop4
) {}
