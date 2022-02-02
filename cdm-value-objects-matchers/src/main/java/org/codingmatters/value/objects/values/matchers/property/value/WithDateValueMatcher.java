package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.CustomTypeSafeMatcher;

import java.time.LocalDate;

public final class WithDateValueMatcher extends CustomTypeSafeMatcher<PropertyValue.Value> {
    private final LocalDate value;

    public WithDateValueMatcher(LocalDate value) {
        super(String.valueOf(value));
        this.value = value;
    }

    @Override
    protected boolean matchesSafely(PropertyValue.Value item) {
        return item.dateValue().equals(this.value);
    }

    public static WithDateValueMatcher dateValue(LocalDate value) {
        return new WithDateValueMatcher(value);
    }
}
