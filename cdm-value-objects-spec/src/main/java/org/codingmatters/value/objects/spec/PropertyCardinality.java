package org.codingmatters.value.objects.spec;

/**
 * Created by nelt on 10/11/16.
 */
public enum PropertyCardinality {
    SINGLE(false), LIST(true), SET(true);

    private final boolean collection;

    PropertyCardinality(boolean collection) {
        this.collection = collection;
    }

    public boolean isCollection() {
        return collection;
    }
}
