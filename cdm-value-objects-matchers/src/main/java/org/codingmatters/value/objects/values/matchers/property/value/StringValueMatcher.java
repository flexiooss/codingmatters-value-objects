package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import static org.hamcrest.core.IsEqual.equalTo;

public final class StringValueMatcher extends ValueMatcher<String> {
    public StringValueMatcher(Matcher<String> matcher) {
        super(PropertyValue.Type.STRING, matcher);
    }

    @Override
    protected String internalValue(PropertyValue.Value value) {
        return value.stringValue();
    }

    @Factory
    public static StringValueMatcher stringValueMatching(Matcher<String> matcher) {
        return new StringValueMatcher(matcher);
    }

    @Factory
    public static StringValueMatcher stringValue(String value) {
        return new StringValueMatcher(equalTo(value));
    }
}
