package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.codingmatters.value.objects.values.matchers.property.value.DateValueMatcher.dateValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DateValueMatcherTest {
    @Test
    public void epoch_Match_epoch() {
        final LocalDate epoch = LocalDate.EPOCH;
        final PropertyValue.Value value = PropertyValue.builder().dateValue(epoch).buildValue();
        assertThat(dateValue(epoch).matches(value), is(true));
    }

    @Test
    public void epoch_Match_1stOfJanuary2022() {
        final LocalDate epoch = LocalDate.EPOCH;
        final LocalDate january1stOf2022 = LocalDate.of(2022, Month.JANUARY, 1);
        final PropertyValue.Value value = PropertyValue.builder().dateValue(epoch).buildValue();
        assertThat(dateValue(january1stOf2022).matches(value), is(false));
    }

    @Test
    public void noValue__DoesNotMatch() {
        final LocalDate epoch = LocalDate.EPOCH;
        final PropertyValue.Value value = PropertyValue.builder().buildValue();
        assertThat(dateValue(epoch).matches(value), is(false));
    }

    @Test
    public void nullValue__DoesNotMatch() {
        final LocalDate epoch = LocalDate.EPOCH;
        final PropertyValue.Value value = null;
        assertThat(dateValue(epoch).matches(value), is(false));
    }

    @Test
    public void stringValue__DoesNotMatch() {
        final LocalDate epoch = LocalDate.EPOCH;
        final PropertyValue.Value value = PropertyValue.builder().stringValue("whatever").buildValue();
        assertThat(dateValue(epoch).matches(value), is(false));
    }
}