package org.codingmatters.value.objects.js.parser.model.types;

import java.util.Objects;

public class ListType implements ValueObjectType {

    private final ValueObjectType type;

    public ListType( ValueObjectType type ) {
        this.type = type;
    }

    public ValueObjectType type() {
        return type;
    }

    @Override
    public boolean equals( Object o ) {
        if( this == o ) { return true; }
        if( o == null || getClass() != o.getClass() ) { return false; }
        ListType listType = (ListType) o;
        return Objects.equals( type, listType.type );
    }

    @Override
    public int hashCode() {

        return Objects.hash( type );
    }
}
