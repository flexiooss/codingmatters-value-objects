package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

import static org.codingmatters.value.objects.values.matchers.property.value.DateTimeValueMatcher.dateTimeValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DateTimeValueMatcherTest {
    @Test
    public void epoch_Match_Epoch() {
        final LocalDateTime epoch = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
        final PropertyValue.Value value = PropertyValue.builder().datetimeValue(epoch).buildValue();
        assertThat(value, is(dateTimeValue(epoch)));
    }

    @Test(expected = AssertionError.class)
    public void epoch_DoesNotMatch_1January2022atNoon() {
        final LocalDateTime januaryThe1st = LocalDateTime.of(LocalDate.ofYearDay(2022, 1), LocalTime.NOON);
        final LocalDateTime epoch = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
        final PropertyValue.Value value = PropertyValue.builder().datetimeValue(epoch).buildValue();
        assertThat(value, is(dateTimeValue(januaryThe1st)));
    }
}