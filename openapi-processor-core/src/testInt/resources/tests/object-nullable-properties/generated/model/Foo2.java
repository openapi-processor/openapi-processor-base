/*
 * This class is auto generated by https://github.com/hauner/openapi-processor-core.
 * TEST ONLY.
 */

package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.openapitools.jackson.nullable.JsonNullable;

public class Foo2 {

    @JsonProperty("bar")
    private JsonNullable<String> bar = JsonNullable.undefined();

    public JsonNullable<String> getBar() {
        return bar;
    }

    public void setBar(JsonNullable<String> bar) {
        this.bar = bar;
    }

}