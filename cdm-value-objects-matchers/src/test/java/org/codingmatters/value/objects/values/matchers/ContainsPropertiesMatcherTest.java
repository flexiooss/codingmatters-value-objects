package org.codingmatters.value.objects.values.matchers;

import org.codingmatters.value.objects.values.ObjectValue;
import org.hamcrest.Matcher;
import org.junit.Test;

import static org.codingmatters.value.objects.values.matchers.ContainsPropertiesMatcher.containsProperties;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ContainsPropertiesMatcherTest {
    private final ObjectValue OBJECT = ObjectValue.builder()
            .property("property1", v -> v.stringValue("whatever"))
            .property("property2", v -> v.stringValue("whenever"))
            .property("property3", v -> v.stringValue("wherever"))
            .property("property4", v -> v.stringValue("whoever"))
            .build();

    @Test
    public void hasAllPropertiesInOrder() {
        final Matcher<ObjectValue> propertiesMatcher = containsProperties("property1", "property2", "property3", "property4");
        assertThat(propertiesMatcher.matches(OBJECT), is(true));
    }

    @Test
    public void hasAllPropertiesInReverseOrder() {
        final Matcher<ObjectValue> propertiesMatcher = containsProperties("property4", "property3", "property2", "property1");
        assertThat(propertiesMatcher.matches(OBJECT), is(true));
    }

    @Test
    public void hasOnePropertyLess__DoNotMatch() {
        final Matcher<ObjectValue> propertiesMatcher = containsProperties("property3", "property2", "property1");
        assertThat(propertiesMatcher.matches(OBJECT), is(false));
    }
}