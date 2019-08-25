/*
 * This class is auto generated by the original authors.
 * DO NOT EDIT.
 */

package generated.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

public interface EndpointApi {

    @GetMapping(path = "/endpoint")
    ResponseEntity<void> getEndpoint();

    @PutMapping(path = "/endpoint")
    ResponseEntity<void> putEndpoint();

    @PostMapping(path = "/endpoint")
    ResponseEntity<void> postEndpoint();

    @PatchMapping(path = "/endpoint")
    ResponseEntity<void> patchEndpoint();

}
