package org.codingmatters.value.objects.demo.books.fake;

import org.codingmatters.value.objects.demo.books.Person;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class OptionalPerson {

    static public OptionalPerson of(Person person) {
        return new OptionalPerson(person);
    }

    private final Optional<Person> optional;

    private OptionalPerson(Person person) {
        this.optional = Optional.ofNullable(person);
    }

    public Optional<String> name() {
        return Optional.ofNullable(this.optional.isPresent() ? this.optional.get().name() : null);
    }

    public Optional<String> email() {
        return Optional.ofNullable(this.optional.isPresent() ? this.optional.get().email() : null);
    }

    public OptionalAddress address() {
        return OptionalAddress.of(this.optional.isPresent() ? this.optional.get().address() : null);
    }

    public Person get() {
        return optional.get();
    }

    public boolean isPresent() {
        return optional.isPresent();
    }

    public void ifPresent(Consumer<? super Person> consumer) {
        optional.ifPresent(consumer);
    }

    public Optional<Person> filter(Predicate<? super Person> predicate) {
        return optional.filter(predicate);
    }

    public <U> Optional<U> map(Function<? super Person, ? extends U> function) {
        return optional.map(function);
    }

    public <U> Optional<U> flatMap(Function<? super Person, Optional<U>> function) {
        return optional.flatMap(function);
    }

    public Person orElse(Person person) {
        return optional.orElse(person);
    }

    public Person orElseGet(Supplier<? extends Person> supplier) {
        return optional.orElseGet(supplier);
    }

    public <X extends Throwable> Person orElseThrow(Supplier<? extends X> supplier) throws X {
        return optional.orElseThrow(supplier);
    }
}
