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

    private Optional<String> name;
    private OptionalPerson author;
    private Optional<String> bookFormat;
    private Optional<LocalDate> localDate;
    private Optional<String> isbn;
    private Optional<Integer> numberOfPages;
    private ValueList<OptionalReview> reviews;

    static public OptionalBook of(Book book) {
        return new OptionalBook(book);
    }

    private final Optional<Book> optional;

    private OptionalBook(Book book) {
        this.optional = Optional.ofNullable(book);
        this.name = Optional.ofNullable(this.isPresent() ? this.get().name() : null);
        this.author = OptionalPerson.of(this.isPresent() ? this.get().author() : null);
        this.bookFormat = Optional.ofNullable(this.isPresent() ? this.get().bookFormat() : null);
        this.localDate = Optional.ofNullable(this.isPresent() ? this.get().datePublished() : null);
        this.isbn = Optional.ofNullable(this.isPresent() ? this.get().isbn() : null);
        this.numberOfPages = Optional.ofNullable(this.isPresent() ? this.get().numberOfPages() : null);
        ValueList.Builder<OptionalReview> builder = new ValueList.Builder<OptionalReview>();
        for (Review review : this.isPresent() ? this.get().reviews() : Collections.<Review>emptyList()) {
            builder.with(OptionalReview.of(review));
        }
        this.reviews = builder.build();
    }


    public Optional<String> name() {
        return this.name;
    }

    public OptionalPerson author() {
        return this.author;
    }


    public Optional<String> bookFormat() {
        return this.bookFormat;
    }

    public Optional<LocalDate> datePublished() {
        return this.localDate;
    }

    public Optional<String> isbn() {
        return this.isbn;
    }

    public Optional<Integer> numberOfPages() {
        return this.numberOfPages;
    }

    public ValueList<OptionalReview> reviews() {
        return this.reviews;
    }


    public Book get() {
        return optional.get();
    }

    public boolean isPresent() {
        return optional.isPresent();
    }

    public void ifPresent(Consumer<Book> consumer) {
        optional.ifPresent(consumer);
    }

    public Optional<Book> filter(Predicate<Book> predicate) {
        return optional.filter(predicate);
    }

    public <U> Optional<U> map(Function<Book, ? extends U> function) {
        return optional.map(function);
    }

    public <U> Optional<U> flatMap(Function<Book, Optional<U>> function) {
        return optional.flatMap(function);
    }

    public Book orElse(Book book) {
        return optional.orElse(book);
    }

    public Book orElseGet(Supplier<Book> supplier) {
        return optional.orElseGet(supplier);
    }

    public <X extends Throwable> Book orElseThrow(Supplier<? extends X> supplier) throws X {
        return optional.orElseThrow(supplier);
    }
}
