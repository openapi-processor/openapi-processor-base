package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import generated.model.Foo;
import generated.support.Generated;
import java.time.Year;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/foo")
    Foo getFoo(@Parameter Year year);

}
