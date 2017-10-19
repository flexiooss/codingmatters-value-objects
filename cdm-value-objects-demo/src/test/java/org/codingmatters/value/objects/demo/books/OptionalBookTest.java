package org.codingmatters.value.objects.demo.books;

import org.codingmatters.value.objects.demo.books.optional.OptionalBook;
import org.codingmatters.value.objects.demo.books.person.Address;
import org.codingmatters.value.objects.demo.books.review.ReviewRating;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.codingmatters.value.objects.demo.books.BookTest.ENGLISH_DATE_FORMATTER;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class OptionalBookTest {
    private Book book = Book.builder()
            .name("Clean Code: A Handbook of Agile Software Craftsmanship")
            .author(Person.builder()
                    .name("Robert C. Martin")
                    .build())
            .bookFormat("Paperback")
            .kind(Book.Kind.TEXTBOOK)
            .tags(Book.Tags.LITERATURE, Book.Tags.SCIENCE)
            .datePublished(LocalDate.parse("August 11, 2008", ENGLISH_DATE_FORMATTER))
            .isbn("978-0132350884")
            .numberOfPages(464)
            .reviews(
                    Review.builder()
                            .author(Person.builder()
                                    .name("John Doe")
                                    .build())
                            .datePublished(LocalDateTime.parse("September 23, 2008", ENGLISH_DATE_FORMATTER))
                            .reviewBody(
                                    "I enjoyed reading this book and after finishing it, " +
                                            "I decided to apply the Boy Scout Rule."
                            )
                            .reviewRating(ReviewRating.builder()
                                    .ratingValue(5)
                                    .build())
                            .build()
            )
            .build();

    @Test
    public void optionalLeafs() throws Exception {
        OptionalBook oBook = this.book.opt();

        assertThat(oBook.author().name().isPresent(), is(true));
        assertThat(oBook.author().name().get(), is("Robert C. Martin"));

        assertThat(oBook.author().address().isPresent(), is(false));
    }

    @Test
    public void optionalEnum() throws Exception {
        assertThat(this.book.opt().kind().isPresent(), is(true));
    }

    @Test
    public void complexPropertiesHaveOptionalBehaviour() throws Exception {
        assertThat(
                this.book.opt().author().address().orElse(Address.builder()
                        .streetAddress("1000 5th Ave")
                        .addressLocality("New York")
                        .addressRegion("NY")
                        .postalCode("10028")
                        .addressCountry("USA")
                        .build()).toString(),
                is("Address{streetAddress=1000 5th Ave, postalCode=10028, addressLocality=New York, addressRegion=NY, addressCountry=USA}")
        );
    }

    @Test
    public void trainWithoutWreck() throws Exception {
        assertThat(OptionalBook.of(this.book).author().address().streetAddress().isPresent(), is(false));
        assertThat(OptionalBook.of(null).author().address().streetAddress().isPresent(), is(false));
    }

    @Test
    public void optionalCollection() throws Exception {
        assertThat(this.book.opt().reviews().isPresent(), is(true));
        assertThat(this.book.opt().reviews().get(0).author().name().isPresent(), is(true));
        assertThat(this.book.opt().reviews().get(0).author().name().get(), is("John Doe"));

        assertThat(this.book.opt().reviews().get(1).isPresent(), is(false));
        assertThat(this.book.opt().reviews().get(1).author().name().isPresent(), is(false));
    }

    @Test
    public void optionalEnums() throws Exception {
        assertThat(this.book.opt().kind().isPresent(), is(true));
        assertThat(this.book.opt().kind().get(), is(Book.Kind.TEXTBOOK));

        assertThat(this.book.opt().tags().isPresent(), is(true));
    }
}
