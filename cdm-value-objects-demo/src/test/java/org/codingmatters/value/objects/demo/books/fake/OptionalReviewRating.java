package org.codingmatters.value.objects.demo.books.fake;

import org.codingmatters.value.objects.demo.books.review.ReviewRating;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class OptionalReviewRating {
    static public OptionalReviewRating of(ReviewRating reviewRating) {
        return new OptionalReviewRating(reviewRating);
    }

    private final Optional<ReviewRating> optional;

    public OptionalReviewRating(ReviewRating reviewRating) {
        this.optional = Optional.ofNullable(reviewRating);
    }

    public Optional<Integer> ratingValue() {
        return Optional.ofNullable(this.isPresent() ? this.get().ratingValue() : null);
    }

    public ReviewRating get() {
        return optional.get();
    }

    public boolean isPresent() {
        return optional.isPresent();
    }

    public void ifPresent(Consumer<? super ReviewRating> consumer) {
        optional.ifPresent(consumer);
    }

    public Optional<ReviewRating> filter(Predicate<? super ReviewRating> predicate) {
        return optional.filter(predicate);
    }

    public <U> Optional<U> map(Function<? super ReviewRating, ? extends U> function) {
        return optional.map(function);
    }

    public <U> Optional<U> flatMap(Function<? super ReviewRating, Optional<U>> function) {
        return optional.flatMap(function);
    }

    public ReviewRating orElse(ReviewRating reviewRating) {
        return optional.orElse(reviewRating);
    }

    public ReviewRating orElseGet(Supplier<? extends ReviewRating> supplier) {
        return optional.orElseGet(supplier);
    }

    public <X extends Throwable> ReviewRating orElseThrow(Supplier<? extends X> supplier) throws X {
        return optional.orElseThrow(supplier);
    }
}
