package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public record AllOfSiblingGetResponse200(
    @JsonProperty(value = "foo", access = JsonProperty.Access.READ_ONLY)
    Foo foo
) {}
