package generated.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import java.time.Year;

@Generated(value = "openapi-processor-core", version = "test")
public class Foo {

    @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "yyyy")
    @JsonProperty("year")
    private Year year;

    public Year getYear() {
        return year;
    }

    public void setYear(Year year) {
        this.year = year;
    }

}
