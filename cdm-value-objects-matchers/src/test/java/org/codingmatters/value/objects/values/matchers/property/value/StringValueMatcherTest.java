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
        assertThat(value, is(stringValue("value")));
    }

    @Test(expected = AssertionError.class)
    public void stringDoesNotMatch() {
        final PropertyValue.Value value = PropertyValue.builder().stringValue("value").buildValue();
        assertThat(value, is(stringValue("AnotherValue")));
    }

    @Test
    public void matchingCaseInsensitive() {
        final PropertyValue.Value value = PropertyValue.builder().stringValue("value").buildValue();
        assertThat(value, stringValueMatching(equalToIgnoringCase("VaLuE")));
    }

    @Test(expected = AssertionError.class)
    public void matchingIgnoringWhiteSpaces() {
        final PropertyValue.Value value = PropertyValue.builder().stringValue("value").buildValue();
        assertThat(value, stringValueMatching(equalToIgnoringWhiteSpace("c'est non")));
    }
}