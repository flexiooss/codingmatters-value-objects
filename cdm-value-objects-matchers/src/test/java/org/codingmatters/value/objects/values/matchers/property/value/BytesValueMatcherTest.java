package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.junit.Test;

import static org.codingmatters.value.objects.values.matchers.property.value.BytesValueMatcher.bytesValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BytesValueMatcherTest {
    @Test
    public void matchingByteArrays() {
        final byte[] bytes = {0x1, 0x2, 0x3};
        final PropertyValue.Value value = PropertyValue.builder().bytesValue(bytes).buildValue();
        assertThat(value, is(bytesValue(bytes)));
    }

    @Test(expected = AssertionError.class)
    public void matching_DoesNotMatch_ByteArrays() {
        final byte[] bytes = {0x1, 0x2, 0x3};
        final PropertyValue.Value value = PropertyValue.builder().bytesValue(bytes).buildValue();
        assertThat(value, is(bytesValue(new byte[] {0x4, 0x5, 0x6})));
    }
}