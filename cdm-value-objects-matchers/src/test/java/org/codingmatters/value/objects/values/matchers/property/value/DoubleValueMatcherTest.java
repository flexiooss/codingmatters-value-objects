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
        assertThat(value, is(doubleValue(Math.PI)));
    }

    @Test(expected = AssertionError.class)
    public void differentDouble_Match() {
        final PropertyValue.Value value = PropertyValue.builder().doubleValue(42d).buildValue();
        assertThat(value, is(doubleValue(69d)));
    }

    @Test
    public void NAN_Match() {
        final PropertyValue.Value value = PropertyValue.builder().doubleValue(Double.NaN).buildValue();
        assertThat(value, is(doubleValue(Double.NaN)));
    }
}