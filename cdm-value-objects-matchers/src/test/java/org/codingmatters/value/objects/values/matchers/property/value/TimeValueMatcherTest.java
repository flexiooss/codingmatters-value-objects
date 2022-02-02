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
        assertThat(value, is(timeValue(noon)));
    }

    @Test(expected = AssertionError.class)
    public void noon_DoesNotMatch_Midnight() {
        final PropertyValue.Value value = PropertyValue.builder().timeValue(LocalTime.NOON).buildValue();
        assertThat(value, is(timeValue(LocalTime.MIDNIGHT)));
    }
}