package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import annotation.Status;
import generated.support.Generated;
import java.util.UUID;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Status("204")
    @Mapping("/uuid")
    void getUuid(
            @Parameter UUID uuid,
            @Parameter UUID uuidex);

}
