package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import javax.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;

@Generated(value = "openapi-processor-core", version = "test")
public record Foo(
    @Size(max = 4)
    @JsonProperty("bar")
    JsonNullable<String> bar
) {}
