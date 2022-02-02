package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.CustomTypeSafeMatcher;

public final class WithLongValueMatcher extends CustomTypeSafeMatcher<PropertyValue.Value> {
    private final Long value;

    public WithLongValueMatcher(Long value) {
        super(String.valueOf(value));
        this.value = value;
    }

    @Override
    protected boolean matchesSafely(PropertyValue.Value item) {
        return item.longValue().equals(value);
    }

    public static WithLongValueMatcher longValue(Long value) {
        return new WithLongValueMatcher(value);
    }
}
