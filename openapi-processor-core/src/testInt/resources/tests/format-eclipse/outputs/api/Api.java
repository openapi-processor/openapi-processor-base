package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import generated.model.Foo;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    /**
     * a <em>markdown</em> description with <strong>text</strong>
     * <ul>
     * <li>one list item</li>
     * <li>second list item</li>
     * </ul>
     *
     * <pre>
     * <code>code block
     * </code>
     * </pre>
     *
     * more
     *
     * @param fOO  this is a <em>parameter</em> description
     * @param bar  this is another <em>parameter</em> description
     * @param body this is the request body
     * @return this is a <em>response</em> description
     */
    @Mapping("/foo")
    Foo getFoo(@Parameter Foo fOO, @Parameter String bar, @Parameter Foo[] body);

}
