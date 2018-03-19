package org.codingmatters.value.objects.values.optional;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class OptionalObjectValue {

    public static OptionalObjectValue of(ObjectValue value) {
        return new OptionalObjectValue(value);
    }

    private final Optional<ObjectValue> value;

    public OptionalObjectValue(ObjectValue value) {
        this.value = Optional.ofNullable(value);
    }


    public boolean has(String property) {
        return this.value.isPresent() && this.value.get().has(property);
    }
    public String [] propertyNames() {
        return this.value.isPresent() ? this.value.get().propertyNames() : new String[0];
    }

    public Optional<PropertyValue> property(String property) {
        if(this.has(property)) {
            return Optional.of(this.value.get().property(property));
        } else {
            return Optional.empty();
        }
    }


    public ObjectValue get() {
        return value.get();
    }

    public boolean isPresent() {
        return value.isPresent();
    }

    public void ifPresent(Consumer<? super ObjectValue> consumer) {
        value.ifPresent(consumer);
    }

    public Optional<ObjectValue> filter(Predicate<? super ObjectValue> predicate) {
        return value.filter(predicate);
    }

    public <U> Optional<U> map(Function<? super ObjectValue, ? extends U> function) {
        return value.map(function);
    }

    public <U> Optional<U> flatMap(Function<? super ObjectValue, Optional<U>> function) {
        return value.flatMap(function);
    }

    public ObjectValue orElse(ObjectValue objectValue) {
        return value.orElse(objectValue);
    }

    public ObjectValue orElseGet(Supplier<? extends ObjectValue> supplier) {
        return value.orElseGet(supplier);
    }

    public <X extends Throwable> ObjectValue orElseThrow(Supplier<? extends X> supplier) throws X {
        return value.orElseThrow(supplier);
    }
}
