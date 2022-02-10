package org.codingmatters.value.objects.values.matchers.property;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.junit.Test;

import static org.codingmatters.value.objects.values.matchers.property.SingleValueMatcher.single;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SingleValueMatcherTest {
    final Matcher<PropertyValue.Value> nonNullStringValue = new CustomTypeSafeMatcher<>("whatever") {
        @Override
        protected boolean matchesSafely(PropertyValue.Value item) {
            return item.isa(PropertyValue.Type.STRING) && ! item.isNull() && item.stringValue().equals("value");
        }
    };

    @Test
    public void singlePropertyValue_Match() {
        final PropertyValue value = PropertyValue.builder().stringValue("value").build();
        assertThat(single(nonNullStringValue).matches(value), is(true));
    }

    @Test
    public void multiplePropertyValue_DoesNotMatch() {
        final PropertyValue value = PropertyValue.multiple(PropertyValue.Type.STRING, v ->v.stringValue("value"));
        assertThat(single(nonNullStringValue).matches(value), is(false));
    }

    @Test
    public void nullValueInProperty_DoesNotMatch() {
        final PropertyValue value = PropertyValue.builder().build();
        assertThat(single(nonNullStringValue).matches(value), is(false));
    }
}