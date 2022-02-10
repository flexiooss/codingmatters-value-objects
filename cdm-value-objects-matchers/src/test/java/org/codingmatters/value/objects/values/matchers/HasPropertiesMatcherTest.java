package org.codingmatters.value.objects.values.matchers;

import org.codingmatters.value.objects.values.ObjectValue;
import org.hamcrest.Matcher;
import org.junit.Test;

import static org.codingmatters.value.objects.values.matchers.HasPropertiesMatcher.hasProperties;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class HasPropertiesMatcherTest {
    private final ObjectValue OBJECT = ObjectValue.builder()
            .property("property1", v -> v.stringValue("whatever"))
            .property("property2", v -> v.stringValue("whenever"))
            .property("property3", v -> v.stringValue("wherever"))
            .property("property4", v -> v.stringValue("whoever"))
            .build();

    @Test
    public void hasAllPropertiesInOrder() {
        final Matcher<ObjectValue> propertiesMatcher = hasProperties("property1", "property2", "property3", "property4");
        assertThat(OBJECT, hasProperties("property1", "property2", "property3", "property4"));
        assertThat(propertiesMatcher.matches(OBJECT), is(true));
    }

    @Test
    public void hasAllPropertiesInReverseOrder() {
        final Matcher<ObjectValue> propertiesMatcher = hasProperties("property4", "property3", "property2", "property1");
        assertThat(propertiesMatcher.matches(OBJECT), is(true));
    }

    @Test
    public void hasOnePropertyLess() {
        final Matcher<ObjectValue> propertiesMatcher = hasProperties("property3", "property2", "property1");
        assertThat(propertiesMatcher.matches(OBJECT), is(true));
    }

    @Test
    public void hasImaginaryProperty__DoNotMatch() {
        final Matcher<ObjectValue> propertiesMatcher = hasProperties("property4", "properties5");
        assertThat(propertiesMatcher.matches(OBJECT), is(false));
    }
}