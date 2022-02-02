package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.CustomTypeSafeMatcher;

public final class WithDoubleValueMatcher extends CustomTypeSafeMatcher<PropertyValue.Value> {
    private final Double value;

    public WithDoubleValueMatcher(Double value) {
        super(String.valueOf(value));
        this.value = value;
    }

    @Override
    protected boolean matchesSafely(PropertyValue.Value item) {
        return item.doubleValue().equals(value);
    }

    public static WithDoubleValueMatcher doubleValue(Double value) {
        return new WithDoubleValueMatcher(value);
    }
}
