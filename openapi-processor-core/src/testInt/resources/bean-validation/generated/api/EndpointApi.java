/*
 * This class is auto generated by https://github.com/hauner/openapi-generatr-spring.
 * DO NOT EDIT.
 */

package generated.api;

import generated.model.Obj1;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.*;

public interface EndpointApi {

    @GetMapping(path = "/endpoint/nullable")
    ResponseEntity<Void> getEndpointNullable(
            @NotNull @RequestParam(name = "nullable-false", required = false) String nullableFalse,
            @RequestParam(name = "nullable-true", required = false) String nullableTrue);

    @GetMapping(path = "/endpoint/length")
    ResponseEntity<Void> getEndpointLength(
            @Size(min = 2) @RequestParam(name = "min-length", required = false) String minLength,
            @Size(max = 4) @RequestParam(name = "max-length", required = false) String maxLength,
            @Size(min = 2, max = 4) @RequestParam(name = "min-max-length", required = false) String minMaxLength);

    @GetMapping(path = "/endpoint/minmax")
    ResponseEntity<Void> getEndpointMinmax(
            @DecimalMin(value = 10) @RequestParam(name = "min", required = false) Integer min,
            @DecimalMin(value = 10, inclusive = false) @RequestParam(name = "min-ex", required = false) Integer minEx,
            @DecimalMax(value = 20) @RequestParam(name = "max", required = false) Integer max,
            @DecimalMax(value = 20, inclusive = false) @RequestParam(name = "max-ex", required = false) Integer maxEx,
            @DecimalMin(value = 10) @DecimalMax(value = 20) @RequestParam(name = "min-max", required = false) Integer minMax,
            @DecimalMin(value = 10, inclusive = false) @DecimalMax(value = 20, inclusive = false) @RequestParam(name = "min-max-ex", required = false) Integer minMaxEx);

    @GetMapping(path = "/endpoint/items")
    ResponseEntity<Void> getEndpointItems(
            @Size(min = 2) @RequestParam(name = "min", required = false) List<String> min,
            @Size(max = 4) @RequestParam(name = "max", required = false) List<String> max,
            @Size(min = 2, max = 4) @RequestParam(name = "min-max", required = false) List<String> minMax);

    @PostMapping(
            path = "/endpoint/obj",
            consumes = {"application/json"})
    ResponseEntity<Void> postEndpointObj(@Valid @RequestBody(required = false) Obj1 body);

}
