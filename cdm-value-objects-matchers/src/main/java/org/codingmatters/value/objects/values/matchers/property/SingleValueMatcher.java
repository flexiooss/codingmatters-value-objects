package org.codingmatters.value.objects.values.matchers.property;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.*;

public class SingleValueMatcher extends TypeSafeDiagnosingMatcher<PropertyValue> {
    private final Matcher<PropertyValue.Value> valueMatcher;

    public SingleValueMatcher(Matcher<PropertyValue.Value> valueMatcher) {
        this.valueMatcher = valueMatcher;
    }

    @Override
    protected boolean matchesSafely(PropertyValue item, Description mismatch) {
        return isSingle(item, mismatch).and(this::itemIsNotNull).matching(valueMatcher);
    }

    private static Condition<PropertyValue> isSingle(PropertyValue item, Description mismatch) {
        if (PropertyValue.Cardinality.SINGLE.equals(item.cardinality())) {
            return Condition.matched(item, mismatch);
        }
        mismatch.appendText("is not single");
        return Condition.notMatched();
    }

    private Condition<PropertyValue.Value> itemIsNotNull(PropertyValue item, Description mismatch) {
        if (item.isNullValue()) {
            mismatch.appendText("is null");
            return Condition.notMatched();
        }

        return Condition.matched(item.single(), mismatch);
    }

    @Override
    public void describeTo(Description description) {
        description.appendDescriptionOf(valueMatcher);
    }

    @Factory
    public static SingleValueMatcher single(Matcher<PropertyValue.Value> valueMatcher) {
        return new SingleValueMatcher(valueMatcher);
    }
}
