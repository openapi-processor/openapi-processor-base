package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import jarkata.validation.constraints.Size;

@Generated(value = "openapi-processor-core", version = "test")
public class Foo {

    @Size(max = 10)
    @JsonProperty("bar")
    private String bar;

    public String getBar() {
        return bar;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }

}
