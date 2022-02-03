package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.junit.Test;

import static org.codingmatters.value.objects.values.matchers.property.value.BooleanValueMatcher.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BooleanValueMatcherTest {
    @Test
    public void trueValue__CheckTrue__Match() {
        final PropertyValue.Value value = PropertyValue.builder().booleanValue(true).buildValue();
        assertThat(trueValue().matches(value), is(true));
    }

    @Test
    public void falseValue__CheckTrue__DoNotMatch() {
        final PropertyValue.Value value = PropertyValue.builder().booleanValue(false).buildValue();
        assertThat(booleanValue(true).matches(value), is(false));
    }

    @Test
    public void falseValue__CheckFalse__Match() {
        final PropertyValue.Value value = PropertyValue.builder().booleanValue(false).buildValue();
        assertThat(booleanValue(false).matches(value), is(true));
    }

    @Test
    public void trueValue__CheckFalse__DoNotMatch() {
        final PropertyValue.Value value = PropertyValue.builder().booleanValue(true).buildValue();
        assertThat(falseValue().matches(value), is(false));
    }

    @Test
    public void noValue__CheckTrue__DoNotMatch() {
        final PropertyValue.Value value = PropertyValue.builder().buildValue();
        assertThat(trueValue().matches(value), is(false));
    }

    @Test
    public void nullValue__CheckTrue__DoNotMatch() {
        final PropertyValue.Value value = null;
        assertThat(trueValue().matches(value), is(false));
    }

    @Test
    public void stringValue__CheckTrue__DoNotMatch() {
        final PropertyValue.Value value = PropertyValue.builder().stringValue("whatever").buildValue();
        assertThat(trueValue().matches(value), is(false));
    }
}