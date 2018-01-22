package org.codingmatters.value.objects.values.optional;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;

import java.util.Optional;

public class OptionalObjectValue {

    public static OptionalObjectValue of(ObjectValue value) {
        return new OptionalObjectValue(value);
    }

    private final ObjectValue value;

    public OptionalObjectValue(ObjectValue value) {
        this.value = value;
    }

    public boolean has(String property) {
        return this.value.has(property);
    }
    public String [] propertyNames() {
        return this.value.propertyNames();
    }

    public Optional<PropertyValue> property(String property) {
        if(this.has(property)) {
            return Optional.of(this.value.property(property));
        } else {
            return Optional.empty();
        }
    }
}
