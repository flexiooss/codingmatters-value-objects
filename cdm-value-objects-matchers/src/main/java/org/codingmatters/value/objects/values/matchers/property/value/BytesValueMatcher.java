package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Factory;

import java.util.Arrays;

public final class BytesValueMatcher extends CustomTypeSafeMatcher<PropertyValue.Value> {
    private final byte[] value;

    public BytesValueMatcher(byte[] value) {
        super(Arrays.toString(value));
        this.value = value;
    }

    @Override
    protected boolean matchesSafely(PropertyValue.Value item) {
        return item.isa(PropertyValue.Type.BYTES) && !item.isNull() && Arrays.equals(item.bytesValue(), value);
    }

    @Factory
    public static BytesValueMatcher bytesValue(byte[] value) {
        return new BytesValueMatcher(value);
    }
}
