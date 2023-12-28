package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import io.oap.FooA;
import io.oap.FooB;
import io.oap.FooC;

@Generated(value = "openapi-processor-core", version = "test")
public class Foo {

    @FooA(value = "any")
    @FooB
    @FooC
    @JsonProperty("bar")
    private String bar;

    public String getBar() {
        return bar;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }

}
