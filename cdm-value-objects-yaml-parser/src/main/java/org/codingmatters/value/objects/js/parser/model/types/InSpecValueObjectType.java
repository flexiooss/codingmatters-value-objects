package org.codingmatters.value.objects.js.parser.model.types;

import java.util.Objects;

public class InSpecValueObjectType implements ObjectType {

    private final String inSpecValueObjectName;

    public InSpecValueObjectType( String inSpecValueObjectName ) {
        this.inSpecValueObjectName = inSpecValueObjectName;
    }

    public String inSpecValueObjectName() {
        return inSpecValueObjectName;
    }

    @Override
    public boolean equals( Object o ) {
        if( this == o ) { return true; }
        if( o == null || getClass() != o.getClass() ) { return false; }
        InSpecValueObjectType that = (InSpecValueObjectType) o;
        return Objects.equals( inSpecValueObjectName, that.inSpecValueObjectName );
    }

    @Override
    public int hashCode() {

        return Objects.hash( inSpecValueObjectName );
    }
}
