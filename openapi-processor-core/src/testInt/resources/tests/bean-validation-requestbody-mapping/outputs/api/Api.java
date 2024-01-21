package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import generated.support.Generated;
import io.oap.Bar;
import javax.validation.Valid;
import reactor.core.publisher.Flux;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/foo")
    void postFoo(@Parameter @Valid Bar body);

    @Mapping("/foo-flux")
    void postFooFlux(@Parameter @Valid Flux<Bar> body);

}
