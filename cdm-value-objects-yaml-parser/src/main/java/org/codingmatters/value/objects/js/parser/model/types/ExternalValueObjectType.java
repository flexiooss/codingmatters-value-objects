package org.codingmatters.value.objects.js.parser.model.types;

import java.util.Objects;

public class ExternalValueObjectType implements ObjectType {

    private final String objectReference;

    public ExternalValueObjectType( String objectReference ) {
        this.objectReference = objectReference;
    }

    public String objectReference() {
        return objectReference;
    }

    @Override
    public boolean equals( Object o ) {
        if( this == o ) { return true; }
        if( o == null || getClass() != o.getClass() ) { return false; }
        ExternalValueObjectType that = (ExternalValueObjectType) o;
        return Objects.equals( objectReference, that.objectReference );
    }

    @Override
    public int hashCode() {

        return Objects.hash( objectReference );
    }
}
