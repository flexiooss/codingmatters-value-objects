package org.codingmatters.value.objects.demo.books;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.ion.IonFactory;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import org.codingmatters.value.objects.demo.books.json.BookReader;
import org.codingmatters.value.objects.demo.books.json.BookWriter;
import org.codingmatters.value.objects.demo.books.review.ReviewRating;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.codingmatters.value.objects.demo.books.BookTest.ENGLISH_DATE_FORMATTER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BookSerializationFormatsTest {

    static private final Book CLEAN_CODE = Book.builder()
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

    public TemporaryFolder dir = new TemporaryFolder();

    private final JsonFactory jsonFactory = new JsonFactory();
    private final CBORFactory cborFactory = new CBORFactory();
    private final IonFactory ionFactory = new IonFactory();
    private final SmileFactory smileFactory = new SmileFactory();

    @Test
    public void formatStorageSizeComparison() throws Exception {
        byte[] jsonBytes = this.serializeBookWith(CLEAN_CODE, this.jsonFactory);

        byte[] cborBytes = this.serializeBookWith(CLEAN_CODE, this.cborFactory);
        byte[] ionBytes = this.serializeBookWith(CLEAN_CODE, this.ionFactory);
        byte[] smileBytes = this.serializeBookWith(CLEAN_CODE, this.smileFactory);

        System.out.println("JSON size : " + jsonBytes.length);

        System.out.printf("CBOR   size : %8d bytes - %5.2f %%%n", cborBytes.length, this.percentage(jsonBytes.length, cborBytes.length));
        System.out.printf("ION    size : %8d bytes - %5.2f %%%n", cborBytes.length, this.percentage(jsonBytes.length, ionBytes.length));
        System.out.printf("SMILE  size : %8d bytes - %5.2f %%%n", cborBytes.length, this.percentage(jsonBytes.length, smileBytes.length));
    }

    @Test
    public void givenUsingCborFactory__whenSerializingThenDeserializing__thenIdempotent() throws Exception {
        assertThat(
                deserialiseBookWith(serializeBookWith(CLEAN_CODE, this.cborFactory), this.cborFactory),
                is(CLEAN_CODE)
        );
    }

    @Test
    public void givenUsingIonFactory__whenSerializingThenDeserializing__thenIdempotent() throws Exception {
        assertThat(
                deserialiseBookWith(serializeBookWith(CLEAN_CODE, this.ionFactory), this.ionFactory),
                is(CLEAN_CODE)
        );
    }

    @Test
    public void givenUsingSmileFactory__whenSerializingThenDeserializing__thenIdempotent() throws Exception {
        assertThat(
                deserialiseBookWith(serializeBookWith(CLEAN_CODE, this.smileFactory), this.smileFactory),
                is(CLEAN_CODE)
        );
    }

    private double percentage(double reference, double length) {
        return 100 * (1 - length / reference);
    }

    private byte[] serializeBookWith(Book book, JsonFactory factory) throws IOException {
        byte[] cborBytes;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); JsonGenerator generator = factory.createGenerator(out);) {
            new BookWriter().write(generator, book);
            generator.close();
            cborBytes = out.toByteArray();
        }
        return cborBytes;
    }

    private Book deserialiseBookWith(byte[] bytes, JsonFactory factory) throws IOException {
        try(JsonParser parser = factory.createParser(bytes)) {
            return new BookReader().read(parser);
        }
    }
}
