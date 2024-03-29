package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import generated.model.User;
import generated.model.UserSearch;
import generated.support.Generated;
import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Generated(value = "openapi-processor-core", version = "test")
public interface UserApi {

    @Mapping("/user")
    ResponseEntity<User> postUser(@Parameter User body);

    @Mapping("/users/{userId}")
    ResponseEntity<Void> deleteUserByUserId(@Parameter Long userId);

    @Mapping("/users/{userId}")
    ResponseEntity<User> getUserByUserId(@Parameter Long userId);

    @Mapping("/users/{userId}")
    ResponseEntity<User> putUserByUserId(
            @Parameter Long userId,
            @Parameter User body);

    @Mapping("/users")
    ResponseEntity<Collection<User>> getAllUsers();

    @Mapping("/user-page")
    ResponseEntity<Page<User>> getUserPage(
            @Parameter Pageable pageable,
            @Parameter UserSearch body);

}
