package org.codingmatters.value.objects.values.optional;

import org.codingmatters.value.objects.values.PropertyValue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class OptionalValue {
    private final Optional<PropertyValue.Value> value;

    public OptionalValue(PropertyValue.Value value) {
        this.value = Optional.ofNullable(value);
    }

    public Optional<String> stringValue() {
        if(this.value.isEmpty()) return Optional.empty();
        return Optional.ofNullable(this.value.get().stringValue());
    }
    public Optional<Long> longValue() {
        if(this.value.isEmpty()) return Optional.empty();
        return Optional.ofNullable(this.value.get().longValue());
    }
    public Optional<Double> doubleValue() {
        if(this.value.isEmpty()) return Optional.empty();
        return Optional.ofNullable(this.value.get().doubleValue());
    }
    public Optional<Boolean> booleanValue() {
        if(this.value.isEmpty()) return Optional.empty();
        return Optional.ofNullable(this.value.get().booleanValue());
    }
    public Optional<byte[]> bytesValue() {
        if(this.value.isEmpty()) return Optional.empty();
        return Optional.ofNullable(this.value.get().bytesValue());
    }
    public OptionalObjectValue objectValue() {
        if(this.value.isEmpty()) return new OptionalObjectValue(null);
        return new OptionalObjectValue(this.value.get().objectValue());
    }
    public Optional<Object> rawValue() {
        if(this.value.isEmpty()) return Optional.empty();
        return Optional.ofNullable(this.value.get().rawValue());
    }
    public Optional<LocalDate> dateValue() {
        if(this.value.isEmpty()) return Optional.empty();
        return Optional.ofNullable(this.value.get().dateValue());
    }
    public Optional<LocalTime> timeValue() {
        if(this.value.isEmpty()) return Optional.empty();
        return Optional.ofNullable(this.value.get().timeValue());
    }
    public Optional<LocalDateTime> datetimeValue() {
        if(this.value.isEmpty()) return Optional.empty();
        return Optional.ofNullable(this.value.get().datetimeValue());
    }

    public Optional<PropertyValue.Type> type() {
        if(this.value.isEmpty()) return Optional.empty();
        return Optional.ofNullable(this.value.get().type());
    }

    public boolean isa(PropertyValue.Type type) {
        if(this.value.isEmpty()) return false;
        return this.value.get().isa(type);
    }
    public boolean isNull() {
        if(this.value.isEmpty()) return true;
        return this.value.get().isNull();
    }


    public PropertyValue.Value get() {
        return value.get();
    }

    public boolean isPresent() {
        return value.isPresent();
    }

    public boolean isEmpty() {
        return ! value.isEmpty();
    }

    public void ifPresent(Consumer<? super PropertyValue.Value> consumer) {
        value.ifPresent(consumer);
    }

    public Optional<PropertyValue.Value> filter(Predicate<? super PropertyValue.Value> predicate) {
        return value.filter(predicate);
    }

    public <U> Optional<U> map(Function<? super PropertyValue.Value, ? extends U> function) {
        return value.map(function);
    }

    public <U> Optional<U> flatMap(Function<? super PropertyValue.Value, Optional<U>> function) {
        return value.flatMap(function);
    }

    public PropertyValue.Value orElse(PropertyValue.Value objectValue) {
        return value.orElse(objectValue);
    }

    public PropertyValue.Value orElseGet(Supplier<? extends PropertyValue.Value> supplier) {
        return value.orElseGet(supplier);
    }

    public <X extends Throwable> PropertyValue.Value orElseThrow(Supplier<? extends X> supplier) throws X {
        return value.orElseThrow(supplier);
    }
}
