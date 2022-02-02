package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.CustomTypeSafeMatcher;

import java.time.LocalTime;

public final class WithTimeValueMatcher extends CustomTypeSafeMatcher<PropertyValue.Value> {
    private final LocalTime value;

    public WithTimeValueMatcher(LocalTime value) {
        super(String.valueOf(value));
        this.value = value;
    }

    @Override
    protected boolean matchesSafely(PropertyValue.Value item) {
        return item.timeValue().equals(this.value);
    }

    public static WithTimeValueMatcher timeValue(LocalTime value) {
        return new WithTimeValueMatcher(value);
    }
}
