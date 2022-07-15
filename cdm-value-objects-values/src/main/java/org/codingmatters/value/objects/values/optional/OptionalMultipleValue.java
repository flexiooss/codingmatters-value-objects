package org.codingmatters.value.objects.values.optional;

import org.codingmatters.value.objects.values.PropertyValue;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class OptionalMultipleValue {

    private final Optional<PropertyValue.Value[]> values;

    public OptionalMultipleValue(PropertyValue.Value[] values) {
        this.values = Optional.ofNullable(values);
    }

    public OptionalValue get(int index) {
        if(this.values.isEmpty() || this.values.get().length <= index) return new OptionalValue(null);
        return new OptionalValue(this.values.get()[index]);
    }

    public PropertyValue.Value[] get() {
        return values.get();
    }

    public boolean isPresent() {
        return values.isPresent();
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public void ifPresent(Consumer<? super PropertyValue.Value[]> consumer) {
        values.ifPresent(consumer);
    }

    public Optional<PropertyValue.Value[]> filter(Predicate<? super PropertyValue.Value[]> predicate) {
        return values.filter(predicate);
    }

    public <U> Optional<U> map(Function<? super PropertyValue.Value[], ? extends U> function) {
        return values.map(function);
    }

    public <U> Optional<U> flatMap(Function<? super PropertyValue.Value[], Optional<U>> function) {
        return values.flatMap(function);
    }

    public PropertyValue.Value[] orElse(PropertyValue.Value[] objectValue) {
        return values.orElse(objectValue);
    }

    public PropertyValue.Value[] orElseGet(Supplier<? extends PropertyValue.Value[]> supplier) {
        return values.orElseGet(supplier);
    }

    public <X extends Throwable> PropertyValue.Value[] orElseThrow(Supplier<? extends X> supplier) throws X {
        return values.orElseThrow(supplier);
    }
}
