package org.codingmatters.value.objects.values.matchers;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.Matcher;
import org.junit.Test;

import static org.codingmatters.value.objects.values.matchers.HasPropertiesMatcher.hasProperty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class HasPropertyMatcherTest {
    public static final String PROPERTY_NAME = "testProperty";

    @Test
    public void nullSingleStringValue() {
        final ObjectValue value = ObjectValue.builder()
                .property(PROPERTY_NAME, PropertyValue.Builder::build)
                .property("anotherOne", v -> v.stringValue("bite the dust"))
                .build();

        final Matcher<ObjectValue> hasPropertyMatcher = hasProperty(PROPERTY_NAME);
        assertThat(hasPropertyMatcher.matches(value), is(true));
    }

    @Test
    public void nullMultipleDoubleValue() {
        final PropertyValue.Value[] emptyValue = null;
        final ObjectValue value = ObjectValue.builder()
                .property(PROPERTY_NAME, PropertyValue.multiple(PropertyValue.Type.DOUBLE, emptyValue))
                .property("anotherOne", v -> v.stringValue("bite the dust"))
                .build();

        final Matcher<ObjectValue> hasPropertyMatcher = hasProperty(PROPERTY_NAME);
        assertThat(hasPropertyMatcher.matches(value), is(true));
    }

    @Test
    public void nonNullSingleStringValue() {
        final ObjectValue value = ObjectValue.builder()
                .property(PROPERTY_NAME, v -> v.stringValue("IT works !"))
                .property("anotherOne", v -> v.stringValue("bite the dust"))
                .build();

        final Matcher<ObjectValue> hasPropertyMatcher = hasProperty(PROPERTY_NAME);
        assertThat(hasPropertyMatcher.matches(value), is(true));
    }

    @Test
    public void nonNullMultipleDoubleValue() {
        final PropertyValue.Value[] doubleValues = {PropertyValue.builder().doubleValue(4d).buildValue()};
        final ObjectValue value = ObjectValue.builder()
                .property(PROPERTY_NAME, PropertyValue.multiple(PropertyValue.Type.DOUBLE, doubleValues))
                .property("anotherOne", v -> v.stringValue("bite the dust"))
                .build();

        final Matcher<ObjectValue> hasPropertyMatcher = hasProperty(PROPERTY_NAME);
        assertThat(hasPropertyMatcher.matches(value), is(true));
    }
}