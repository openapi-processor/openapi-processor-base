package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import javax.validation.constraints.NotNull;

@Generated(value = "openapi-processor-core", version = "test")
public class Query2GetResponse200 {

    @NotNull
    @JsonProperty("prop1")
    private String prop1;

    public String getProp1() {
        return prop1;
    }

    public void setProp1(String prop1) {
        this.prop1 = prop1;
    }

}
