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
        assertThat(bytesValue(bytes).matches(value), is(true));
    }

    @Test
    public void matching_DoesNotMatch_ByteArrays() {
        final byte[] bytes = {0x1, 0x2, 0x3};
        final PropertyValue.Value value = PropertyValue.builder().bytesValue(bytes).buildValue();
        assertThat(bytesValue(new byte[] {0x4, 0x5, 0x6}).matches(value), is(false));
    }

    @Test
    public void noValue__DoesNotMatch() {
        final PropertyValue.Value value = PropertyValue.builder().buildValue();
        assertThat(bytesValue(new byte[] {0x4, 0x5, 0x6}).matches(value), is(false));
    }

    @Test
    public void nullValue__DoesNotMatch() {
        final PropertyValue.Value value = null;
        assertThat(bytesValue(new byte[] {0x4, 0x5, 0x6}).matches(value), is(false));
    }

    @Test
    public void stringValue__DoesNotMatch() {
        final PropertyValue.Value value = PropertyValue.builder().stringValue("whatever").buildValue();
        assertThat(bytesValue(new byte[] {0x4, 0x5, 0x6}).matches(value), is(false));
    }
}