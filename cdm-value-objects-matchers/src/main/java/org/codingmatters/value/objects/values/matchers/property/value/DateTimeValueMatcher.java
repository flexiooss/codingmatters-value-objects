package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import java.time.LocalDateTime;

import static org.hamcrest.core.IsEqual.equalTo;

public final class DateTimeValueMatcher extends ValueMatcher<LocalDateTime> {
    public DateTimeValueMatcher(Matcher<LocalDateTime> matcher) {
        super(PropertyValue.Type.DATETIME, matcher);
    }

    @Override
    protected LocalDateTime internalValue(PropertyValue.Value value) {
        return value.datetimeValue();
    }

    @Factory
    public static DateTimeValueMatcher dateTimeValue(LocalDateTime value) {
        return new DateTimeValueMatcher(equalTo(value));
    }

    @Factory
    public static DateTimeValueMatcher dateTimeValue(Matcher<LocalDateTime> value) {
        return new DateTimeValueMatcher(value);
    }
}
