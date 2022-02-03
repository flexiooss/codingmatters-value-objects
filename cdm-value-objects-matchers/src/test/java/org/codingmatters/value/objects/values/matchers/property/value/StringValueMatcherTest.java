package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.junit.Test;

import static org.codingmatters.value.objects.values.matchers.property.value.StringValueMatcher.stringValue;
import static org.codingmatters.value.objects.values.matchers.property.value.StringValueMatcher.stringValueMatching;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.text.IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace;

public class StringValueMatcherTest {
    @Test
    public void stringMatch() {
        final PropertyValue.Value value = PropertyValue.builder().stringValue("value").buildValue();
        assertThat(stringValue("value").matches(value), is(true));
    }

    @Test
    public void stringDoesNotMatch() {
        final PropertyValue.Value value = PropertyValue.builder().stringValue("value").buildValue();
        assertThat(stringValue("AnotherValue").matches(value), is(false));
    }

    @Test
    public void matchingCaseInsensitive__Match() {
        final PropertyValue.Value value = PropertyValue.builder().stringValue("value").buildValue();
        assertThat(stringValueMatching(equalToIgnoringCase("VaLuE")).matches(value), is(true));
    }

    @Test
    public void matchingIgnoringWhiteSpaces__DoNotMatch() {
        final PropertyValue.Value value = PropertyValue.builder().stringValue("value").buildValue();
        assertThat(stringValueMatching(equalToIgnoringWhiteSpace("c'est non")).matches(value), is(false));
    }

    @Test
    public void noValue__DoesNotMatch() {
        final PropertyValue.Value value = PropertyValue.builder().buildValue();
        assertThat(stringValue("c'est non").matches(value), is(false));
    }

    @Test
    public void nullValue__DoesNotMatch() {
        final PropertyValue.Value value = null;
        assertThat(stringValue("c'est non").matches(value), is(false));
    }

    @Test
    public void anotherValue__DoesNotMatch() {
        final PropertyValue.Value value = PropertyValue.builder().booleanValue(false).buildValue();
        assertThat(stringValue("c'est non").matches(value), is(false));
    }
}