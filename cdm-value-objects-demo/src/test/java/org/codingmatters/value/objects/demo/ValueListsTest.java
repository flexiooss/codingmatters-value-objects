package org.codingmatters.value.objects.demo;

import org.codingmatters.value.objects.demo.books.Book;
import org.codingmatters.value.objects.demo.books.ValueList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.hamcrest.Matchers.*;

class ValueListsTest {

    @Test
    void given__whenName__then() throws Exception {
        Book book = Book.builder().build();
        ValueList.Builder<Book> booksBuilder = ValueList.builder();
        System.out.println(booksBuilder.with(book).build());
        System.out.println(booksBuilder.with((Book) null).build());
    }
}