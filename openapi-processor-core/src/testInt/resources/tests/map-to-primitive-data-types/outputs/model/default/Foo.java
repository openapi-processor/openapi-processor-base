package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public class Foo {

    @JsonProperty("bar")
    private byte[] bar;

    public byte[] getBar() {
        return bar;
    }

    public void setBar(byte[] bar) {
        this.bar = bar;
    }

}
