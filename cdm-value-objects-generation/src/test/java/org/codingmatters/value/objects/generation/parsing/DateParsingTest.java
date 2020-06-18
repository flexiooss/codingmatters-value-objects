package org.codingmatters.value.objects.generation.parsing;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DateParsingTest {

    private final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().appendPattern("yyyy[-MM[-dd['T'HH[:mm[:ss[.SSSSSS][.SSS]]]]]]")
                    .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
                    .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                    .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                    .toFormatter();

    private final DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder().appendPattern("HH[:mm[:ss[.SSSSSS][.SSS]]]")
                    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                    .toFormatter();

    @Test
    public void givenFullDateString__when__then() throws Exception {
        TemporalAccessor t = dateTimeFormatter.parse("2012-03-25T12:43:22.123456");
        assertThat(LocalDateTime.from(t).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), is("2012-03-25T12:43:22.123456"));
        assertThat(LocalDate.from(t).format(DateTimeFormatter.ISO_LOCAL_DATE), is("2012-03-25"));
        assertThat(LocalTime.from(t).format(DateTimeFormatter.ISO_LOCAL_TIME), is("12:43:22.123456"));
    }

    @Test
    public void givenDateWithoutMicrosString__when__then() throws Exception {
        TemporalAccessor t = dateTimeFormatter.parse("2012-03-25T12:43:22.123");
        assertThat(LocalDateTime.from(t).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), is("2012-03-25T12:43:22.123"));
        assertThat(LocalDate.from(t).format(DateTimeFormatter.ISO_LOCAL_DATE), is("2012-03-25"));
        assertThat(LocalTime.from(t).format(DateTimeFormatter.ISO_LOCAL_TIME), is("12:43:22.123"));
    }

    @Test
    public void givenDateWithoutMillisString__when__then() throws Exception {
        TemporalAccessor t = dateTimeFormatter.parse("2012-03-25T12:43:22");
        assertThat(LocalDateTime.from(t).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), is("2012-03-25T12:43:22"));
        assertThat(LocalDate.from(t).format(DateTimeFormatter.ISO_LOCAL_DATE), is("2012-03-25"));
        assertThat(LocalTime.from(t).format(DateTimeFormatter.ISO_LOCAL_TIME), is("12:43:22"));
    }

    @Test
    public void givenDateWithoutSecondsString__when__then() throws Exception {
        TemporalAccessor t = dateTimeFormatter.parse("2012-03-25T12:43");
        assertThat(LocalDateTime.from(t).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), is("2012-03-25T12:43:00"));
        assertThat(LocalDate.from(t).format(DateTimeFormatter.ISO_LOCAL_DATE), is("2012-03-25"));
        assertThat(LocalTime.from(t).format(DateTimeFormatter.ISO_LOCAL_TIME), is("12:43:00"));
    }

    @Test
    public void givenDateWithoutMinutesString__when__then() throws Exception {
        TemporalAccessor t = dateTimeFormatter.parse("2012-03-25T12");
        assertThat(LocalDateTime.from(t).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), is("2012-03-25T12:00:00"));
        assertThat(LocalDate.from(t).format(DateTimeFormatter.ISO_LOCAL_DATE), is("2012-03-25"));
        assertThat(LocalTime.from(t).format(DateTimeFormatter.ISO_LOCAL_TIME), is("12:00:00"));
    }

    @Test
    public void givenDateWithoutHoursString__when__then() throws Exception {
        TemporalAccessor t = dateTimeFormatter.parse("2012-03-25");
        assertThat(LocalDateTime.from(t).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), is("2012-03-25T00:00:00"));
        assertThat(LocalDate.from(t).format(DateTimeFormatter.ISO_LOCAL_DATE), is("2012-03-25"));
        assertThat(LocalTime.from(t).format(DateTimeFormatter.ISO_LOCAL_TIME), is("00:00:00"));
    }

    @Test
    public void givenDateWithoutDayString__when__then() throws Exception {
        TemporalAccessor t = dateTimeFormatter.parse("2012-03");
        assertThat(LocalDateTime.from(t).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), is("2012-03-01T00:00:00"));
        assertThat(LocalDate.from(t).format(DateTimeFormatter.ISO_LOCAL_DATE), is("2012-03-01"));
        assertThat(LocalTime.from(t).format(DateTimeFormatter.ISO_LOCAL_TIME), is("00:00:00"));
    }

    @Test
    public void givenDateWithoutMonthString__when__then() throws Exception {
        TemporalAccessor t = dateTimeFormatter.parse("2012");
        assertThat(LocalDateTime.from(t).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), is("2012-01-01T00:00:00"));
        assertThat(LocalDate.from(t).format(DateTimeFormatter.ISO_LOCAL_DATE), is("2012-01-01"));
        assertThat(LocalTime.from(t).format(DateTimeFormatter.ISO_LOCAL_TIME), is("00:00:00"));
    }


    @Test
    public void givenFullTimeString__when__then() throws Exception {
        TemporalAccessor t = timeFormatter.parse("12:43:22.123456");
        assertThat(LocalTime.from(t).format(DateTimeFormatter.ISO_LOCAL_TIME), is("12:43:22.123456"));
    }

    @Test
    public void givenTimeWithoutMicrosString__when__then() throws Exception {
        TemporalAccessor t = timeFormatter.parse("12:43:22.123");
        assertThat(LocalTime.from(t).format(DateTimeFormatter.ISO_LOCAL_TIME), is("12:43:22.123"));
    }

    @Test
    public void givenTimeWithoutMillisString__when__then() throws Exception {
        TemporalAccessor t = timeFormatter.parse("12:43:22");
        assertThat(LocalTime.from(t).format(DateTimeFormatter.ISO_LOCAL_TIME), is("12:43:22"));
    }

    @Test
    public void givenTimeWithoutSecondsString__when__then() throws Exception {
        TemporalAccessor t = timeFormatter.parse("12:43");
        assertThat(LocalTime.from(t).format(DateTimeFormatter.ISO_LOCAL_TIME), is("12:43:00"));
    }

    @Test
    public void givenTimeWithoutMinutesString__when__then() throws Exception {
        TemporalAccessor t = timeFormatter.parse("12");
        assertThat(LocalTime.from(t).format(DateTimeFormatter.ISO_LOCAL_TIME), is("12:00:00"));
    }
}
