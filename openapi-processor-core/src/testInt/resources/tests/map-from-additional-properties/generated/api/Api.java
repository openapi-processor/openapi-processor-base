package generated.api;

import annotation.Mapping;
import generated.support.Generated;
import java.util.List;
import java.util.Map;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/foo")
    Map<String, String> getFoo();

    @Mapping("/foo2")
    Map<String, List<String>> getFoo2();

}
