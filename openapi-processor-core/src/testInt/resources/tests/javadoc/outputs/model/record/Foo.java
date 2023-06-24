package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

/**
 * this is the <em>Foo</em> schema description
 */
@Generated(value = "openapi-processor-core", version = "test")
public record Foo(
    /**
     * <em>property</em> description
     */
    @JsonProperty("foo-bar")
    String fooBar
) {}
