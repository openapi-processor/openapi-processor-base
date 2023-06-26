package org.openapitools.jackson.nullable;

public class JsonNullable<T> {
    public static <T> JsonNullable<T> undefined() {
        return null;
    }
}
