package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.CustomTypeSafeMatcher;

import java.util.Arrays;

public final class BytesValueMatcher extends CustomTypeSafeMatcher<PropertyValue.Value> {
    private final byte[] value;

    public BytesValueMatcher(byte[] value) {
        super(String.valueOf(value));
        this.value = value;
    }

    @Override
    protected boolean matchesSafely(PropertyValue.Value item) {
        return Arrays.equals(item.bytesValue(), value);
    }

    public static BytesValueMatcher bytesValue(byte[] value) {
        return new BytesValueMatcher(value);
    }
}
