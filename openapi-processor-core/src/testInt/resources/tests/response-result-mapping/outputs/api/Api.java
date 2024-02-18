package generated.api;

import annotation.Mapping;
import generated.support.Generated;
import org.springframework.http.ResponseEntity;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/foo")
    String getFoo();

    @Mapping("/bar")
    ResponseEntity<String> getBar();

}
