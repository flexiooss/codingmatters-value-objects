package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.junit.Test;

import java.time.LocalTime;

import static org.codingmatters.value.objects.values.matchers.property.value.TimeValueMatcher.timeValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TimeValueMatcherTest {
    @Test
    public void noon_Match_Noon() {
        final LocalTime noon = LocalTime.NOON;
        final PropertyValue.Value value = PropertyValue.builder().timeValue(noon).buildValue();
        assertThat(timeValue(noon).matches(value), is(true));
    }

    @Test
    public void noon_DoesNotMatch_Midnight() {
        final PropertyValue.Value value = PropertyValue.builder().timeValue(LocalTime.NOON).buildValue();
        assertThat(timeValue(LocalTime.MIDNIGHT).matches(value), is(false));
    }

    @Test
    public void noValue__DoNotMatch() {
        final PropertyValue.Value value = PropertyValue.builder().buildValue();
        assertThat(timeValue(LocalTime.MIDNIGHT).matches(value), is(false));
    }

    @Test
    public void nullValue__DoNotMatch() {
        final PropertyValue.Value value = null;
        assertThat(timeValue(LocalTime.MIDNIGHT).matches(value), is(false));
    }

    @Test
    public void stringValue__DoNotMatch() {
        final PropertyValue.Value value = PropertyValue.builder().stringValue("whatever").buildValue();
        assertThat(timeValue(LocalTime.MIDNIGHT).matches(value), is(false));
    }
}