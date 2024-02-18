package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import generated.model.Props;
import generated.support.Generated;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/prop/{id}")
    ResponseEntity<Mono<Props>> getPropId(@Parameter Integer id);

    @Mapping("/props")
    ResponseEntity<Flux<Props>> getProps();

}
