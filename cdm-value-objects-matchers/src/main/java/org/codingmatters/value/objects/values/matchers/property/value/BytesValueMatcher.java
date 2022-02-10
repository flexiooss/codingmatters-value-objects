package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.Matcher;

import static org.hamcrest.core.IsEqual.equalTo;

public final class BytesValueMatcher extends ValueMatcher<byte[]> {
    public BytesValueMatcher(Matcher<byte[]> matcher) {
        super(PropertyValue.Type.BYTES, matcher);
    }

    @Override
    protected byte[] internalValue(PropertyValue.Value value) {
        return value.bytesValue();
    }

    public static BytesValueMatcher bytesValue(Matcher<byte[]> value) {
        return new BytesValueMatcher(value);
    }

    public static BytesValueMatcher bytesValue(byte[] value) {
        return new BytesValueMatcher(equalTo(value));
    }
}
