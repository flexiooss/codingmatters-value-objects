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
        assertThat(longValue(420L).matches(value), is(true));
    }

    @Test
    public void differentValue__DoNotMatch() {
        final PropertyValue.Value value = PropertyValue.builder().longValue(420L).buildValue();
        assertThat(longValue(42L).matches(value), is(false));
    }

    @Test
    public void maxValue_Match_MaxValue() {
        final PropertyValue.Value value = PropertyValue.builder().longValue(Long.MAX_VALUE).buildValue();
        assertThat(longValue(Long.MAX_VALUE).matches(value), is(true));
    }

    @Test
    public void noValue__DoNotMatch() {
        final PropertyValue.Value value = PropertyValue.builder().buildValue();
        assertThat(longValue(42L).matches(value), is(false));
    }

    @Test
    public void nullValue__DoNotMatch() {
        final PropertyValue.Value value = null;
        assertThat(longValue(42L).matches(value), is(false));
    }

    @Test
    public void stringValue__DoNotMatch() {
        final PropertyValue.Value value = PropertyValue.builder().stringValue("whatever").buildValue();
        assertThat(longValue(42L).matches(value), is(false));
    }
}