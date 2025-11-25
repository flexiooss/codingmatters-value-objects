package org.codingmatters.value.objects.demo.books;

import com.fasterxml.jackson.core.JsonFactory;
import org.codingmatters.value.objects.demo.books.json.BookReader;
import org.codingmatters.value.objects.demo.books.json.BookWriter;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BookJsonTest {
    @Test
    public void writerReaderLoop() throws Exception {
        Book book = Book.builder()
                .name("Clean Code: A Handbook of Agile Software Craftsmanship")
                .author(Person.builder()
                        .name("Robert C. Martin")
                        .build())
                .bookFormat("Paperback")
                .kind(Book.Kind.TEXTBOOK)
                .tags(Book.Tags.LITERATURE, Book.Tags.SCIENCE)
                .build();
        JsonFactory jsonFactory = new JsonFactory();

        Book actual = new BookReader().readString(jsonFactory, new BookWriter().writeString(jsonFactory, book));

        assertThat(actual, is(book));
    }
}
