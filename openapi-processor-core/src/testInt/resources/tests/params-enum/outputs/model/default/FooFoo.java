package generated.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public enum FooFoo {
    FOO("foo"),
    FOO_2("foo-2"),
    FOO_FOO("foo-foo");

    private final String value;

    FooFoo(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

    @JsonCreator
    public static FooFoo fromValue(String value) {
        for (FooFoo val : FooFoo.values()) {
            if (val.value.equals(value)) {
                return val;
            }
        }
        throw new IllegalArgumentException(value);
    }

}
