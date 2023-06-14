package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public class Class {

    @JsonProperty("class")
    private String aClass;

    public String getClass() {
        return aClass;
    }

    public void setClass(String aClass) {
        this.aClass = aClass;
    }

}
