package generated.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import java.time.Year;

@Generated(value = "openapi-processor-core", version = "test")
public record Foo(
    @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "yyyy")
    @JsonProperty("year")
    Year year
) {}
