package org.codingmatters.value.objects.js.parser.model.types;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class InSpecEnum implements YamlEnum {
    private final List<String> values;

    public InSpecEnum( List<String> values ) {
        this.values = values;
    }

    public InSpecEnum( String... values ) {
        this.values = Arrays.stream( values ).collect( Collectors.toList() );
    }

    public List<String> values() {
        return values;
    }

    @Override
    public boolean equals( Object o ) {
        if( this == o ) { return true; }
        if( o == null || getClass() != o.getClass() ) { return false; }
        InSpecEnum that = (InSpecEnum) o;
        if( this.values.size() != that.values.size() ) { return false;}
        boolean valuesEquals = true;
        for( int i = 0; i < this.values.size(); i++ ) {
            valuesEquals = valuesEquals && this.values.get( i ).equals( that.values.get( i ) );
        }
        return valuesEquals;
    }

    @Override
    public int hashCode() {

        return Objects.hash( values );
    }
}
