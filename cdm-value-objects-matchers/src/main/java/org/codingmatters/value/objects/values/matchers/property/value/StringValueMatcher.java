package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static org.hamcrest.Matchers.equalTo;

public final class StringValueMatcher extends TypeSafeMatcher<PropertyValue.Value> {
    private final Matcher<String> valueMatcher;

    public StringValueMatcher(Matcher<String> valueMatcher) {
        this.valueMatcher = valueMatcher;
    }

    public StringValueMatcher(String valueMatcher) {
        this(equalTo(valueMatcher));
    }

    @Override
    protected boolean matchesSafely(PropertyValue.Value item) {
        return valueMatcher.matches(item.stringValue());
    }

    @Override
    public void describeTo(Description description) {
        description.appendDescriptionOf(valueMatcher);
    }

    public static StringValueMatcher stringValue(String value) {
        return new StringValueMatcher(value);
    }

    public static StringValueMatcher stringValueMatching(Matcher<String> matcher) {
        return new StringValueMatcher(matcher);
    }
}
