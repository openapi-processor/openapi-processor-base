package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import java.time.LocalDate;

@Generated(value = "openapi-processor-core", version = "test")
public record UserSearch(
    @JsonProperty("id")
    Long id,

    @JsonProperty("firstName")
    String firstName,

    @JsonProperty("lastName")
    String lastName,

    @JsonProperty("birthDate")
    LocalDate birthDate,

    @JsonProperty("email")
    String email
) {}
