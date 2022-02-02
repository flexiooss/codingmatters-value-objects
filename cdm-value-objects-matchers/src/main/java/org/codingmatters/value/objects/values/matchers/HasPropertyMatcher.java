package org.codingmatters.value.objects.values.matchers;

import org.codingmatters.value.objects.values.ObjectValue;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;


public class HasPropertyMatcher extends TypeSafeMatcher<ObjectValue> {
    private final String property;

    public HasPropertyMatcher(String property) {
        this.property = property;
    }

    @Override
    protected boolean matchesSafely(ObjectValue value) {
        return value.has(property);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("no ").appendValue(property).appendText(" property");
    }

    public static Matcher<ObjectValue> hasProperty(String property) {
        return new HasPropertyMatcher(property);
    }
}
