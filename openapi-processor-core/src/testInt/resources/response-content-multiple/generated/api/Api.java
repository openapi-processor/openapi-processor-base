/*
 * This class is auto generated by https://github.com/hauner/openapi-processor-spring.
 * DO NOT EDIT.
 */

package generated.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

public interface Api {

    @GetMapping(
            path = "/foo",
            produces = {"application/json", "application/xml"})
    ResponseEntity<?> getFooApplicationJson();

    @GetMapping(
            path = "/foo",
            produces = {"text/plain", "application/xml"})
    ResponseEntity<?> getFooTextPlain();

}
