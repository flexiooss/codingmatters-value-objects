package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.Matcher;

import static org.hamcrest.core.IsEqual.equalTo;

public final class ObjectValueMatcher extends ValueMatcher<ObjectValue> {
    public ObjectValueMatcher(Matcher<ObjectValue> matcher) {
        super(PropertyValue.Type.OBJECT, matcher);
    }

    @Override
    protected ObjectValue internalValue(PropertyValue.Value value) {
        return value.objectValue();
    }

    public static ObjectValueMatcher objectValue(Matcher<ObjectValue> matcher) {
        return new ObjectValueMatcher(matcher);
    }

    public static ObjectValueMatcher objectValue(ObjectValue value) {
        return new ObjectValueMatcher(equalTo(value));
    }
}
