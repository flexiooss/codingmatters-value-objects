package org.codingmatters.value.objects.js.parser.model.types;

import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;

import java.util.Objects;

public class NestedObjectType implements ObjectType {

    private final ParsedValueObject nestValueObject;

    public NestedObjectType( ParsedValueObject nestValueObject ) {
        this.nestValueObject = nestValueObject;
    }

    public ParsedValueObject nestValueObject() {
        return nestValueObject;
    }

    @Override
    public boolean equals( Object o ) {
        if( this == o ) { return true; }
        if( o == null || getClass() != o.getClass() ) { return false; }
        NestedObjectType that = (NestedObjectType) o;
        return Objects.equals( nestValueObject, that.nestValueObject );
    }

    @Override
    public int hashCode() {

        return Objects.hash( nestValueObject );
    }
}
