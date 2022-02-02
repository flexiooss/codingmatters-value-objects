package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.CustomTypeSafeMatcher;

import java.time.LocalDateTime;

public final class DateTimeValueMatcher extends CustomTypeSafeMatcher<PropertyValue.Value> {
    private final LocalDateTime value;

    public DateTimeValueMatcher(LocalDateTime value) {
        super(String.valueOf(value));
        this.value = value;
    }

    @Override
    protected boolean matchesSafely(PropertyValue.Value item) {
        return item.datetimeValue().equals(value);
    }

    public static DateTimeValueMatcher dateTimeValue(LocalDateTime value) {
        return new DateTimeValueMatcher(value);
    }
}
