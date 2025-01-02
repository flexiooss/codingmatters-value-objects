package org.codingmatters.value.objects.values.optional;

import org.codingmatters.value.objects.values.PropertyValue;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class OptionalPropertyValue {

    private final Optional<PropertyValue> value;

    public OptionalPropertyValue(PropertyValue value) {
        this.value = Optional.ofNullable(value);
    }

    public Optional<PropertyValue.Type> type() {
        if(this.value.isEmpty()) return Optional.empty();
        return Optional.ofNullable(this.value.get().type());
    }

    public Optional<PropertyValue.Cardinality> cardinality() {
        if(this.value.isEmpty()) return Optional.empty();
        return Optional.ofNullable(this.value.get().cardinality());

    }

    public OptionalValue single() {
        if(this.value.isEmpty()) return new OptionalValue(null);
        if(! PropertyValue.Cardinality.SINGLE.equals(this.value.get().cardinality())) {
            return new OptionalValue(null);
        } else {
            return new OptionalValue(this.value.get().single());
        }
    }

    public OptionalMultipleValue multiple() {
        if(this.value.isEmpty()) return new OptionalMultipleValue(null);
        if(! PropertyValue.Cardinality.MULTIPLE.equals(this.value.get().cardinality())) {
            return new OptionalMultipleValue(null);
        } else {
            return new OptionalMultipleValue(this.value.get().multiple());
        }
    }


    public PropertyValue get() {
        return value.get();
    }

    public boolean isPresent() {
        return ! this.isEmpty();
    }

    static PropertyValue EMPTY = PropertyValue.builder().build();

    public boolean isEmpty() {
        return value.isEmpty();
    }

    public void ifPresent(Consumer<? super PropertyValue> consumer) {
        value.ifPresent(consumer);
    }

    public Optional<PropertyValue> filter(Predicate<? super PropertyValue> predicate) {
        return value.filter(predicate);
    }

    public <U> Optional<U> map(Function<? super PropertyValue, ? extends U> function) {
        return value.map(function);
    }

    public <U> Optional<U> flatMap(Function<? super PropertyValue, Optional<U>> function) {
        return value.flatMap(function);
    }

    public PropertyValue orElse(PropertyValue objectValue) {
        return value.orElse(objectValue);
    }

    public PropertyValue orElseGet(Supplier<? extends PropertyValue> supplier) {
        return value.orElseGet(supplier);
    }

    public <X extends Throwable> PropertyValue orElseThrow(Supplier<? extends X> supplier) throws X {
        return value.orElseThrow(supplier);
    }
}
