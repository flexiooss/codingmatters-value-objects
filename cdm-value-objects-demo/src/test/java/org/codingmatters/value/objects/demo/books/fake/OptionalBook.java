package org.codingmatters.value.objects.demo.books.fake;

import org.codingmatters.value.objects.demo.books.Book;
import org.codingmatters.value.objects.demo.books.Review;
import org.codingmatters.value.objects.demo.books.ValueList;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class OptionalBook {

    static public OptionalBook of(Book book) {
        return new OptionalBook(book);
    }

    private final Optional<Book> optional;

    private OptionalBook(Book book) {
        this.optional = Optional.ofNullable(book);
    }


    public Optional<String> name() {
        return Optional.ofNullable(this.isPresent() ? this.get().name() : null);
    }

    public OptionalPerson author() {
        return OptionalPerson.of(this.isPresent() ? this.get().author() : null);
    }


    public Optional<String> bookFormat() {
        return Optional.ofNullable(this.isPresent() ? this.get().bookFormat() : null);
    }

    public Optional<LocalDate> datePublished() {
        return Optional.ofNullable(this.isPresent() ? this.get().datePublished() : null);
    }

    public Optional<String> isbn() {
        return Optional.ofNullable(this.isPresent() ? this.get().isbn() : null);
    }

    public Optional<Integer> numberOfPages() {
        return Optional.ofNullable(this.isPresent() ? this.get().numberOfPages() : null);
    }

    public ValueList<OptionalReview> reviews() {
        ValueList.Builder<OptionalReview> builder = new ValueList.Builder<OptionalReview>();
        for (Review review : this.isPresent() ? this.get().reviews() : Collections.<Review>emptyList()) {
            builder.with(OptionalReview.of(review));
        }
        return builder.build();
    }


    public Book get() {
        return optional.get();
    }

    public boolean isPresent() {
        return optional.isPresent();
    }

    public void ifPresent(Consumer<? super Book> consumer) {
        optional.ifPresent(consumer);
    }

    public Optional<Book> filter(Predicate<? super Book> predicate) {
        return optional.filter(predicate);
    }

    public <U> Optional<U> map(Function<? super Book, ? extends U> function) {
        return optional.map(function);
    }

    public <U> Optional<U> flatMap(Function<? super Book, Optional<U>> function) {
        return optional.flatMap(function);
    }

    public Book orElse(Book book) {
        return optional.orElse(book);
    }

    public Book orElseGet(Supplier<? extends Book> supplier) {
        return optional.orElseGet(supplier);
    }

    public <X extends Throwable> Book orElseThrow(Supplier<? extends X> supplier) throws X {
        return optional.orElseThrow(supplier);
    }
}
