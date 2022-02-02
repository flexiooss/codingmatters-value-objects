package org.codingmatters.value.objects.values.matchers.property;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;

public class WithSingleValueMatcher extends CustomTypeSafeMatcher<PropertyValue> {
    private final Matcher<PropertyValue.Value> valueMatcher;

    public WithSingleValueMatcher(Matcher<PropertyValue.Value> valueMatcher) {
        super("single ");
        this.valueMatcher = valueMatcher;
    }

    @Override
    protected boolean matchesSafely(PropertyValue item) {
        return valueMatcher.matches(item);
    }

    public static WithSingleValueMatcher withSingle(Matcher<PropertyValue.Value> valueMatcher) {
        return new WithSingleValueMatcher(valueMatcher);
    }
}
