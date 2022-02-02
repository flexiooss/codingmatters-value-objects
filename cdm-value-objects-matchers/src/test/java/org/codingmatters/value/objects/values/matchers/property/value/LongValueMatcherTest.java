package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.junit.Test;

import static org.codingmatters.value.objects.values.matchers.property.value.LongValueMatcher.longValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class LongValueMatcherTest {
    @Test
    public void sameValue_Match() {
        final PropertyValue.Value value = PropertyValue.builder().longValue(420L).buildValue();
        assertThat(value, is(longValue(420L)));
    }

    @Test(expected = AssertionError.class)
    public void differentValue_DoesNotMatch() {
        final PropertyValue.Value value = PropertyValue.builder().longValue(420L).buildValue();
        assertThat(value, is(longValue(42L)));
    }

    @Test
    public void maxValue() {
        final PropertyValue.Value value = PropertyValue.builder().longValue(Long.MAX_VALUE).buildValue();
        assertThat(value, is(longValue(Long.MAX_VALUE)));
    }
}