package org.codingmatters.value.objects.demo.books.fake;

import org.codingmatters.value.objects.demo.books.person.Address;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class OptionalAddress {

    static public OptionalAddress of(Address address) {
        return new OptionalAddress(address);
    }

    private final Optional<Address> optional;

    private OptionalAddress(Address address) {
        this.optional = Optional.ofNullable(address);
    }

    public Optional<String> streetAddress() {
        return Optional.ofNullable(this.isPresent() ? this.get().streetAddress() : null);
    }

    public Optional<String> postalCode() {
        return Optional.ofNullable(this.optional.isPresent() ? this.get().postalCode() : null);
    }

    public Optional<String> addressLocality() {
        return Optional.ofNullable(this.optional.isPresent() ? this.get().addressLocality() : null);
    }

    public Optional<String> addressRegion() {
        return Optional.ofNullable(this.optional.isPresent() ? this.get().addressRegion() : null);
    }

    public Optional<String> addressCountry() {
        return Optional.ofNullable(this.optional.isPresent() ? this.get().addressCountry() : null);
    }

    public Address get() {
        return optional.get();
    }

    public boolean isPresent() {
        return optional.isPresent();
    }

    public void ifPresent(Consumer<? super Address> consumer) {
        optional.ifPresent(consumer);
    }

    public Optional<Address> filter(Predicate<? super Address> predicate) {
        return optional.filter(predicate);
    }

    public <U> Optional<U> map(Function<? super Address, ? extends U> function) {
        return optional.map(function);
    }

    public <U> Optional<U> flatMap(Function<? super Address, Optional<U>> function) {
        return optional.flatMap(function);
    }

    public Address orElse(Address address) {
        return optional.orElse(address);
    }

    public Address orElseGet(Supplier<? extends Address> supplier) {
        return optional.orElseGet(supplier);
    }

    public <X extends Throwable> Address orElseThrow(Supplier<? extends X> supplier) throws X {
        return optional.orElseThrow(supplier);
    }
}
