package generated.api;

import annotation.Mapping;
import generated.support.Generated;
import io.oap.Wrap;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/foo")
    String getFoo();

    @Mapping("/bar")
    Wrap<String> getBar();

}
