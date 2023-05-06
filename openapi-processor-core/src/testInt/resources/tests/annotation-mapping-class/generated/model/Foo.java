package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import io.oap.Annotation;

@Generated(value = "openapi-processor-core", version = "test")
public class Foo {

    @Annotation(value = io.oap.Bar.class)
    @JsonProperty("bar")
    private String bar;

    public String getBar() {
        return bar;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }

}
