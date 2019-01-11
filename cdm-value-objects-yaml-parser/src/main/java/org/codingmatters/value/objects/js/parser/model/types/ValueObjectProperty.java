package org.codingmatters.value.objects.js.parser.model.types;

import java.util.Objects;

public class ValueObjectProperty {

    private final String name;
    private final ValueObjectType type;

    public ValueObjectProperty( String name, ValueObjectType type ) {
        this.name = name;
        this.type = type;
    }

    public String name() {
        return name;
    }

    public ValueObjectType type() {
        return type;
    }

    @Override
    public boolean equals( Object o ) {
        if( this == o ) { return true; }
        if( o == null || getClass() != o.getClass() ) { return false; }
        ValueObjectProperty that = (ValueObjectProperty) o;
        return name.equals( that.name ) && type.equals( that.type );
    }

    @Override
    public int hashCode() {

        return Objects.hash( name, type );
    }
}
