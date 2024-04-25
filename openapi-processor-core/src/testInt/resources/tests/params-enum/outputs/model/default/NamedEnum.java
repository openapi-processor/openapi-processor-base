package generated.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public enum NamedEnum {
    ONE("1"),
    TWO("2"),
    THREE("3");

    private final String value;

    NamedEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

    @JsonCreator
    public static NamedEnum fromValue(String value) {
        for (NamedEnum val : NamedEnum.values()) {
            if (val.value.equals(value)) {
                return val;
            }
        }
        throw new IllegalArgumentException(value);
    }
}
