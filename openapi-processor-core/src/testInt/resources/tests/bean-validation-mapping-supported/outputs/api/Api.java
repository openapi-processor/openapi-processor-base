package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import annotation.Status;
import generated.support.Generated;
import io.oap.CustomInteger;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.time.Year;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Status("204")
    @Mapping("/foo")
    void getFoo(
            @Parameter Year year,
            @Parameter @DecimalMin(value = "1970") @DecimalMax(value = "2099") CustomInteger other);

}
