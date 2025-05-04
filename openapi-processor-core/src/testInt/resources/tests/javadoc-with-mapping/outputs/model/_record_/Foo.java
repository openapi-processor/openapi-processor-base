package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

/**
 * schema level description.
 *
 * @param bar property level description.
 */
@Generated(value = "openapi-processor-core", version = "test")
public record Foo(@JsonProperty("bar") String bar) {
}
