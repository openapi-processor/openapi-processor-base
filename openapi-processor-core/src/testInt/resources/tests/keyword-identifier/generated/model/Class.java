package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public class Class {

    @JsonProperty("class")
    private String aClass;

    public String getAClass() {
        return aClass;
    }

    public void setAClass(String aClass) {
        this.aClass = aClass;
    }

}
