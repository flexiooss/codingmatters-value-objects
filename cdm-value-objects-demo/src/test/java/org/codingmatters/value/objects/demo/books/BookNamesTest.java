package org.codingmatters.value.objects.demo.books;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BookNamesTest {

    @Test
    public void names() throws Exception {
        assertThat(Book.names().name(), is("name"));
        assertThat(Book.names().author(), is("author"));
        assertThat(Book.names().authorNames().email(), is("email"));
        assertThat(Book.names().reviews(), is("reviews"));
        assertThat(Book.names().reviewsNames().authorNames().addressNames().postalCode(), is("postalCode"));
    }
}
