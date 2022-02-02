package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.CustomTypeSafeMatcher;

import java.time.LocalDateTime;

public final class WithDateTimeValueMatcher extends CustomTypeSafeMatcher<PropertyValue.Value> {
    private final LocalDateTime value;

    public WithDateTimeValueMatcher(LocalDateTime value) {
        super(String.valueOf(value));
        this.value = value;
    }

    @Override
    protected boolean matchesSafely(PropertyValue.Value item) {
        return item.datetimeValue().equals(value);
    }

    public static WithDateTimeValueMatcher dateTimeValue(LocalDateTime value) {
        return new WithDateTimeValueMatcher(value);
    }
}
