package org.codingmatters.value.objects.values.matchers;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.*;

public class HasNonNullPropertyWithValueMatcher extends TypeSafeDiagnosingMatcher<ObjectValue> {
    private final String propertyName;
    private final Matcher<PropertyValue> propertyValueMatcher;

    public HasNonNullPropertyWithValueMatcher(String propertyName, Matcher<PropertyValue> propertyValueMatcher) {
        this.propertyName = propertyName;
        this.propertyValueMatcher = propertyValueMatcher;
    }

    @Override
    protected boolean matchesSafely(ObjectValue item, Description mismatch) {
        return correspondingProperty(item, mismatch).matching(propertyValueMatcher, String.format("property \"%s\" ", this.propertyName));
    }

    private Condition<PropertyValue> correspondingProperty(ObjectValue item, Description mismatch) {
        final PropertyValue property = item.property(this.propertyName);

        if (property == null) {
            mismatch.appendText(String.format("ObjectValue lacks property \"%s\"", propertyName));
            return Condition.notMatched();
        }

        return Condition.matched(property, mismatch);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("having property ").appendValue(propertyName)
                .appendText(" matching ").appendDescriptionOf(propertyValueMatcher);
    }

    @Factory
    public static HasNonNullPropertyWithValueMatcher hasProperty(String propertyName, Matcher<PropertyValue> withMatcher) {
        return new HasNonNullPropertyWithValueMatcher(propertyName, withMatcher);
    }
}
