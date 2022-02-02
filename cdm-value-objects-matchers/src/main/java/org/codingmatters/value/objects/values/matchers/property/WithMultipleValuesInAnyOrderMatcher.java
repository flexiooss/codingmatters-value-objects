package org.codingmatters.value.objects.values.matchers.property;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.Collection;

public class WithMultipleValuesInAnyOrderMatcher extends TypeSafeDiagnosingMatcher<PropertyValue.Value[]> {
    private final Collection<Matcher<PropertyValue.Value>> matchers;

    public WithMultipleValuesInAnyOrderMatcher(Collection<Matcher<PropertyValue.Value>> matchers) {
        this.matchers = matchers;
    }

    @Override
    protected boolean matchesSafely(PropertyValue.Value[] item, Description mismatchDescription) {
        return false;
    }

    @Override
    public void describeTo(Description description) {

    }
}
