package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Factory;

public final class DoubleValueMatcher extends CustomTypeSafeMatcher<PropertyValue.Value> {
    private final Double value;

    public DoubleValueMatcher(Double value) {
        super(String.valueOf(value));
        this.value = value;
    }

    @Override
    protected boolean matchesSafely(PropertyValue.Value item) {
        return item.isa(PropertyValue.Type.DOUBLE) && ! item.isNull() && item.doubleValue().equals(value);
    }

    @Factory
    public static DoubleValueMatcher doubleValue(Double value) {
        return new DoubleValueMatcher(value);
    }
}
