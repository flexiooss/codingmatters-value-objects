package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Factory;

import java.time.LocalTime;

public final class TimeValueMatcher extends CustomTypeSafeMatcher<PropertyValue.Value> {
    private final LocalTime value;

    public TimeValueMatcher(LocalTime value) {
        super(String.valueOf(value));
        this.value = value;
    }

    @Override
    protected boolean matchesSafely(PropertyValue.Value item) {
        return item.isa(PropertyValue.Type.TIME) && ! item.isNull() && item.timeValue().equals(this.value);
    }

    @Factory
    public static TimeValueMatcher timeValue(LocalTime value) {
        return new TimeValueMatcher(value);
    }
}
