package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Factory;

public final class LongValueMatcher extends CustomTypeSafeMatcher<PropertyValue.Value> {
    private final Long value;

    public LongValueMatcher(Long value) {
        super(String.valueOf(value));
        this.value = value;
    }

    @Override
    protected boolean matchesSafely(PropertyValue.Value item) {
        return item.isa(PropertyValue.Type.LONG) && ! item.isNull() && item.longValue().equals(value);
    }

    @Factory
    public static LongValueMatcher longValue(Long value) {
        return new LongValueMatcher(value);
    }
}
