package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Factory;

import java.time.LocalDateTime;

public final class DateTimeValueMatcher extends CustomTypeSafeMatcher<PropertyValue.Value> {
    private final LocalDateTime value;

    public DateTimeValueMatcher(LocalDateTime value) {
        super(String.valueOf(value));
        this.value = value;
    }

    @Override
    protected boolean matchesSafely(PropertyValue.Value item) {
        return item.isa(PropertyValue.Type.DATETIME) && ! item.isNull() && item.datetimeValue().equals(value);
    }

    @Factory
    public static DateTimeValueMatcher dateTimeValue(LocalDateTime value) {
        return new DateTimeValueMatcher(value);
    }
}
