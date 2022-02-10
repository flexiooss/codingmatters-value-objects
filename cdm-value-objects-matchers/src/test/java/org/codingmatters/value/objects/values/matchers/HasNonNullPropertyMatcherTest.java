package org.codingmatters.value.objects.values.matchers;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.Matcher;
import org.junit.Test;

import static org.codingmatters.value.objects.values.matchers.HasNonNullPropertyMatcher.hasNonNullProperty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class HasNonNullPropertyMatcherTest {
    private static final Matcher<ObjectValue> matcher = hasNonNullProperty("property", PropertyValue.Type.STRING, PropertyValue.Cardinality.SINGLE);

    @Test
    public void nullPropertyWithName__DoNotMatch() {
        final ObjectValue value = ObjectValue.builder().property("property", PropertyValue.builder().build()).build();
        assertThat(matcher.matches(value), is(false));
    }

    @Test
    public void absentProperty__DoNotMatch() {
        final ObjectValue value = ObjectValue.builder().build();
        assertThat(matcher.matches(value), is(false));
    }

    @Test
    public void nullObject__DoNotMatch() {
        final ObjectValue value = null;
        assertThat(matcher.matches(value), is(false));
    }

    @Test
    public void multiplePropertyWithName__DoNotMatch() {
        final ObjectValue value = ObjectValue.builder()
                .property("property",
                        PropertyValue.multiple(PropertyValue.Type.STRING, v->v.stringValue("whatever")
                )
        ).build();
        assertThat(matcher.matches(value), is(false));
    }

    @Test
    public void wrongTypePropertyWithName__DoNotMatch() {
        final ObjectValue value = ObjectValue.builder().property("property", v->v.longValue(42L)).build();
        assertThat(matcher.matches(value), is(false));
    }

    @Test
    public void whenAllParametersWellSet__ThenMatch() {
        final ObjectValue value = ObjectValue.builder().property("property", v->v.stringValue("whatever")).build();
        assertThat(matcher.matches(value), is(true));
    }
}