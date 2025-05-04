package generated.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import generated.support.Generated;

/**
 * this is an <em>enum</em> description
 */
@Generated(value = "openapi-processor-core", version = "test")
public enum FooEnum {
    FOO("foo"),
    BAR("bar");

    private final String value;

    FooEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

    @JsonCreator
    public static FooEnum fromValue(String value) {
        for (FooEnum val : FooEnum.values()) {
            if (val.value.equals(value)) {
                return val;
            }
        }
        throw new IllegalArgumentException(value);
    }
}
