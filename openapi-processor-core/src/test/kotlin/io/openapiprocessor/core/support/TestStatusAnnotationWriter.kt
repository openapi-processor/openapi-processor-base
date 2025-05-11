package io.openapiprocessor.core.support

import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import io.openapiprocessor.core.writer.java.StatusAnnotationWriter
import java.io.Writer

class TestStatusAnnotationWriter : StatusAnnotationWriter {
    override fun write(
        target: Writer,
        endpoint: Endpoint,
        endpointResponse: EndpointResponse
    ) {
        target.write ("""@Status""")
    }
}
