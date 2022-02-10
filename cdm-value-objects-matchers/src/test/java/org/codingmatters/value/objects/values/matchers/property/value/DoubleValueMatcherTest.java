package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.junit.Test;

import static org.codingmatters.value.objects.values.matchers.property.value.DoubleValueMatcher.doubleValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DoubleValueMatcherTest {
    @Test
    public void sameDouble_Match() {
        final PropertyValue.Value value = PropertyValue.builder().doubleValue(Math.PI).buildValue();
        assertThat(doubleValue(Math.PI).matches(value), is(true));
    }

    @Test
    public void differentDouble_Match() {
        final PropertyValue.Value value = PropertyValue.builder().doubleValue(42d).buildValue();
        assertThat(doubleValue(69d).matches(value), is(false));
    }

    @Test
    public void NAN_Match() {
        final PropertyValue.Value value = PropertyValue.builder().doubleValue(Double.NaN).buildValue();
        assertThat(doubleValue(Double.NaN).matches(value), is(true));
    }

    @Test
    public void noValue_DoNotMatch() {
        final PropertyValue.Value value = PropertyValue.builder().buildValue();
        assertThat(doubleValue(Math.E).matches(value), is(false));
    }

    @Test
    public void nullValue_DoNotMatch() {
        final PropertyValue.Value value = null;
        assertThat(doubleValue(Math.E).matches(value), is(false));
    }

    @Test
    public void stringValue_DoNotMatch() {
        final PropertyValue.Value value = PropertyValue.builder().stringValue("whatever").buildValue();
        assertThat(doubleValue(Math.E).matches(value), is(false));
    }
}