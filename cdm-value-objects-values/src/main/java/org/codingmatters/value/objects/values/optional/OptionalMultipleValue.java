package org.codingmatters.value.objects.values.optional;

import org.codingmatters.value.objects.values.PropertyValue;

import java.util.Optional;

public class OptionalMultipleValue {

    private final Optional<PropertyValue.Value[]> values;

    public OptionalMultipleValue(PropertyValue.Value[] values) {
        this.values = Optional.ofNullable(values);
    }

    public OptionalValue get(int index) {
        if(this.values.isEmpty() || this.values.get().length <= index) return new OptionalValue(null);
        return new OptionalValue(this.values.get()[index]);
    }
}
