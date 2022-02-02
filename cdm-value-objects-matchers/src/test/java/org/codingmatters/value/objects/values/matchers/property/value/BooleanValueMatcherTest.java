package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.junit.Test;

import static org.codingmatters.value.objects.values.matchers.property.value.BooleanValueMatcher.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BooleanValueMatcherTest {
    @Test
    public void trueValue__andCheckTrue() {
        final PropertyValue.Value value = PropertyValue.builder().booleanValue(true).buildValue();
        assertThat(value, is(trueValue()));
    }

    @Test(expected = AssertionError.class)
    public void falseValue__andCheckTrue() {
        final PropertyValue.Value value = PropertyValue.builder().booleanValue(false).buildValue();
        assertThat(value, is(booleanValue(true)));
    }

    @Test
    public void falseValue__andCheckFalse() {
        final PropertyValue.Value value = PropertyValue.builder().booleanValue(false).buildValue();
        assertThat(value, is(booleanValue(false)));
    }

    @Test(expected = AssertionError.class)
    public void trueValue__andCheckFalse() {
        final PropertyValue.Value value = PropertyValue.builder().booleanValue(true).buildValue();
        assertThat(value, is(falseValue()));
    }
}