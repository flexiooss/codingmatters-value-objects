package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.CustomTypeSafeMatcher;

public final class WithStringValueMatcher extends CustomTypeSafeMatcher<PropertyValue.Value> {
    private final String value;

    public WithStringValueMatcher(String value) {
        super(value);
        this.value = value;
    }

    @Override
    protected boolean matchesSafely(PropertyValue.Value item) {
        return item.stringValue().equals(value);
    }

    public static WithStringValueMatcher stringValue(String value) {
        return new WithStringValueMatcher(value);
    }
}
