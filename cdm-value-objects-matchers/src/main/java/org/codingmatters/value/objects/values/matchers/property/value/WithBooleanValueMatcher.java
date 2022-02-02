package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.CustomTypeSafeMatcher;

public final class WithBooleanValueMatcher extends CustomTypeSafeMatcher<PropertyValue.Value> {
    private final Boolean value;

    public WithBooleanValueMatcher(Boolean value) {
        super(String.valueOf(value));
        this.value = value;
    }

    @Override
    protected boolean matchesSafely(PropertyValue.Value item) {
        return item.booleanValue().equals(value);
    }

    public static WithBooleanValueMatcher booleanValue(Boolean value) {
        return new WithBooleanValueMatcher(value);
    }
}
