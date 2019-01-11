package org.codingmatters.value.objects.js.parser.model;

import org.codingmatters.value.objects.js.parser.model.types.ValueObjectProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ParsedValueObject {

    private final String name;
    private final List<ValueObjectProperty> properties;

    public ParsedValueObject( String name ) {
        this.properties = new ArrayList<>();
        this.name = name;
    }

    public String name() {
        return this.name;
    }

    public List<ValueObjectProperty> properties() {
        return this.properties;
    }

    @Override
    public boolean equals( Object o ) {
        if( this == o ) { return true; }
        if( o == null || getClass() != o.getClass() ) { return false; }
        ParsedValueObject that = (ParsedValueObject) o;
        boolean propEquals = true;
        if( properties.size() != that.properties.size() ) { return false; }
        for( int i = 0; i < properties.size(); i++ ) {
            propEquals = propEquals && properties.get( i ).equals( that.properties.get( i ) );
        }
        return Objects.equals( name, that.name ) && propEquals;
    }

    @Override
    public int hashCode() {
        return Objects.hash( name, properties );
    }
}
