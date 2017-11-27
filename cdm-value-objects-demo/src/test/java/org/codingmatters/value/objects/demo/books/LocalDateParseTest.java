package org.codingmatters.value.objects.demo.books;

import org.junit.Test;

import java.time.LocalDate;

import static org.codingmatters.value.objects.demo.books.BookTest.ENGLISH_DATE_FORMATTER;

public class LocalDateParseTest {
    @Test
    public void englishDateFormatter() throws Exception {
        LocalDate.parse("August 11, 2008", ENGLISH_DATE_FORMATTER);
    }
}
