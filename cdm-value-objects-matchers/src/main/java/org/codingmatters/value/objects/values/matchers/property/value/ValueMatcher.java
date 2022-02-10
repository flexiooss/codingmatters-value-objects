package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.Condition;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

abstract class ValueMatcher<T> extends TypeSafeDiagnosingMatcher<PropertyValue.Value> {
    private final PropertyValue.Type type;
    private final Matcher<T> matcher;

    public ValueMatcher(PropertyValue.Type type, Matcher<T> matcher) {
        this.type = type;
        this.matcher = matcher;
    }

    protected abstract T internalValue(PropertyValue.Value value);

    private Condition<T> extractValue(PropertyValue.Value value, Description mismatch) {
        return Condition.matched(internalValue(value), mismatch);
    }

    @Override
    protected final boolean matchesSafely(PropertyValue.Value value, Description mismatch) {
        return this.isA(value, mismatch).and(this::isNonNull).and(this::extractValue).matching(matcher);
    }

    @Override
    public void describeTo(Description description) {
        description.appendDescriptionOf(matcher);
    }

    private Condition<PropertyValue.Value> isA(PropertyValue.Value item, Description mismatch) {
        if (item.isa(type)) {
            return Condition.matched(item, mismatch);
        }

        mismatch.appendText(" is not a " + type + " but a " + item.type());
        return Condition.notMatched();
    }

    private Condition<PropertyValue.Value> isNonNull(PropertyValue.Value item, Description mismatch) {
        if (item.isNull()) {
            mismatch.appendText(" is null");
            return Condition.notMatched();
        }

        return Condition.matched(item, mismatch);
    }
}
