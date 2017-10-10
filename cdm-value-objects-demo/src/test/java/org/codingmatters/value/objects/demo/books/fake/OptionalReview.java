package org.codingmatters.value.objects.demo.books.fake;

import org.codingmatters.value.objects.demo.books.Review;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class OptionalReview {
    static public OptionalReview of(Review review) {
        return new OptionalReview(review);
    }

    private final Optional<Review> optional;

    private OptionalReview(Review review) {
        this.optional = Optional.ofNullable(review);
    }


    public OptionalPerson author() {
        return OptionalPerson.of(this.isPresent() ? this.get().author() : null);
    }

    public Optional<LocalDateTime> datePublished() {
        return Optional.ofNullable(this.isPresent() ? this.get().datePublished() : null);
    }

    public OptionalBook itemReviewed() {
        return OptionalBook.of(this.isPresent() ? this.get().itemReviewed() : null);
    }

    public Optional<String> reviewBody() {
        return Optional.ofNullable(this.isPresent() ? this.get().reviewBody() : null);
    }

    public OptionalReviewRating reviewRating() {
        return OptionalReviewRating.of(this.isPresent() ? this.get().reviewRating() : null);
    }


    public Review get() {
        return optional.get();
    }

    public boolean isPresent() {
        return optional.isPresent();
    }

    public void ifPresent(Consumer<? super Review> consumer) {
        optional.ifPresent(consumer);
    }

    public Optional<Review> filter(Predicate<? super Review> predicate) {
        return optional.filter(predicate);
    }

    public <U> Optional<U> map(Function<? super Review, ? extends U> function) {
        return optional.map(function);
    }

    public <U> Optional<U> flatMap(Function<? super Review, Optional<U>> function) {
        return optional.flatMap(function);
    }

    public Review orElse(Review review) {
        return optional.orElse(review);
    }

    public Review orElseGet(Supplier<? extends Review> supplier) {
        return optional.orElseGet(supplier);
    }

    public <X extends Throwable> Review orElseThrow(Supplier<? extends X> supplier) throws X {
        return optional.orElseThrow(supplier);
    }
}
