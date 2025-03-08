package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

/**
 * this is the <em>Foo</em> schema description
 *
 * @param fooBar <em>property</em> description
 * @param enum enum <em>property</em> description
 */
@Generated(value = "openapi-processor-core", version = "test")
public record Foo(
    @JsonProperty("foo-bar")
    String fooBar,

    @JsonProperty("enum")
    FooEnum aEnum
) {}
