package org.codingmatters.value.objects.values.matchers;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.Condition;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class HasNonNullPropertyWithValueMatcher extends TypeSafeDiagnosingMatcher<ObjectValue> {
    private final String propertyName;
    private final Matcher<PropertyValue> propertyValueMatcher;

    public HasNonNullPropertyWithValueMatcher(String propertyName, Matcher<PropertyValue> propertyValueMatcher) {
        this.propertyName = propertyName;
        this.propertyValueMatcher = propertyValueMatcher;
    }


    @Override
    protected boolean matchesSafely(ObjectValue item, Description mismatch) {
        return correspondingProperty(item, mismatch).matching(propertyValueMatcher);
    }

    private Condition<PropertyValue> correspondingProperty(ObjectValue item, Description mismatch) {
        final PropertyValue property = item.property(this.propertyName);

        if (property == null) {
            mismatch.appendText(String.format("No property \"%s\" in ObjectValue", propertyName));
            return Condition.notMatched();
        }

        return Condition.matched(property, mismatch);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("hasProperty ").appendValue(propertyName)
                .appendText(" matching ").appendDescriptionOf(propertyValueMatcher);
    }

}
