package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import annotation.Status;
import generated.model.Book;
import generated.support.Generated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/books")
    Flux<Book> getBooks();

    @Status("201")
    @Mapping("/books")
    Mono<Void> postBooks(@Parameter Flux<Book> body);

    @Mapping("/books/{isbn}")
    Mono<Book> getBooksIsbn(@Parameter String isbn);

    @Status("201")
    @Mapping("/books/{isbn}")
    Mono<Void> putBooksIsbn(
            @Parameter String isbn,
            @Parameter Mono<Book> body);

}
