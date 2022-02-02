package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;

public class WithObjectValueMatchingMatcher extends CustomTypeSafeMatcher<PropertyValue.Value> {
    private final Matcher<ObjectValue> matcher;

    public WithObjectValueMatchingMatcher(Matcher<ObjectValue> matcher) {
        super("Match ObjectValue matcher");
        this.matcher = matcher;
    }

    @Override
    protected boolean matchesSafely(PropertyValue.Value item) {
        return matcher.matches(item.objectValue());
    }

    public static WithObjectValueMatchingMatcher objectValueMatching(Matcher<ObjectValue> matcher) {
        return new WithObjectValueMatchingMatcher(matcher);
    }
}
