package org.codingmatters.value.objects.demo.books;

import org.codingmatters.value.objects.demo.books.person.Address;
import org.codingmatters.value.objects.demo.books.review.ReviewRating;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 11/26/16.
 */
public class BookTest {

    @Test
    public void chainedBuilderAndCompleteToString() throws Exception {
        Book cleanCode = Book.Builder.builder()
                .name("Clean Code: A Handbook of Agile Software Craftsmanship")
                .author(Person.Builder.builder()
                        .name("Robert C. Martin")
                        .build())
                .bookFormat("Paperback")
                .datePublished("August 11, 2008")
                .isbn("978-0132350884")
                .numberOfPages(464)
                .reviews(
                        Review.Builder.builder()
                                .author(Person.Builder.builder()
                                        .name("John Doe")
                                        .build())
                                .datePublished("September 23, 2008")
                                .reviewBody(
                                        "I enjoyed reading this book and after finishing it, " +
                                                "I decided to apply the Boy Scout Rule."
                                )
                                .reviewRating(ReviewRating.Builder.builder()
                                        .ratingValue(5)
                                        .build())
                                .build()
                )
                .build();

        assertThat(cleanCode.toString(), is(
                "Book{" +
                        "name=Clean Code: A Handbook of Agile Software Craftsmanship, " +
                        "author=Person{name=Robert C. Martin, email=null, address=null}, " +
                        "bookFormat=Paperback, " +
                        "datePublished=August 11, 2008, " +
                        "isbn=978-0132350884, " +
                        "numberOfPages=464, " +
                        "reviews=[Review{" +
                            "author=Person{name=John Doe, email=null, address=null}, " +
                            "datePublished=September 23, 2008, " +
                            "itemReviewed=null, " +
                            "reviewBody=I enjoyed reading this book and after finishing it, I decided to apply the Boy Scout Rule., " +
                            "reviewRating=ReviewRating{ratingValue=5}}" +
                        "]}"
        ));
    }

    @Test
    public void equalityOnValue() throws Exception {
        Person sameValueAsJohn = Person.Builder.builder()
                .name("John Doe")
                .build();
        Person john = Person.Builder.builder()
                .name("John Doe")
                .build();
        Person jane = Person.Builder.builder()
                .name("Jane Doe")
                .build();

        assertThat(john, is(sameValueAsJohn));
        assertThat(jane, is(not(sameValueAsJohn)));
    }

    @Test
    public void singlePropertyChange() throws Exception {
        Person john = Person.Builder.builder()
                .name("John")
                .address(Address.Builder.builder()
                        .postalCode("25000")
                        .build())
                .build();

        Person changed = john.withEmail("john@doe.com");

        assertThat("value as changed          ", changed, is(not(john)));
        assertThat("email property has changed", changed.email(), is("john@doe.com"));
        assertThat("others are left unchanged ", changed.name(), is(john.name()));
        assertThat("others are left unchanged ", changed.address(), is(john.address()));
    }

    @Test
    public void bulkChange() throws Exception {
        Person john = Person.Builder.builder()
                .name("John")
                .address(Address.Builder.builder()
                        .postalCode("25000")
                        .build())
                .build();


        Person changed = john.changed(builder -> builder
                .name(john.name() + " Doe")
                .email("john@doe.com")
        );

        assertThat("value as changed          ", changed, is(not(john)));
        assertThat("name has changed          ", changed.name(), is("John Doe"));
        assertThat("email property has changed", changed.email(), is("john@doe.com"));
        assertThat("others are left unchanged ", changed.address(), is(john.address()));
    }
}