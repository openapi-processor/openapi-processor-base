package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import javax.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;

@Generated(value = "openapi-processor-core", version = "test")
public class Foo2 {

    @Size(max = 4)
    @JsonProperty("bar")
    private JsonNullable<String> bar = JsonNullable.undefined();

    public JsonNullable<String> getBar() {
        return bar;
    }

    public void setBar(JsonNullable<String> bar) {
        this.bar = bar;
    }

}
