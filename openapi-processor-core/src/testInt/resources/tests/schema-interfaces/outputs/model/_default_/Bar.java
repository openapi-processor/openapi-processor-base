package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import io.openapiprocessor.InterfaceA;
import io.openapiprocessor.InterfaceB;

@Generated(value = "openapi-processor-core", version = "test")
public class Bar implements InterfaceA, InterfaceB {

    @JsonProperty("bar")
    private String bar;

    public String getBar() {
        return bar;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }

}
