package org.codingmatters.value.objects.values.matchers;

import org.codingmatters.value.objects.values.ObjectValue;
import org.hamcrest.Matcher;
import org.junit.Test;

import static org.codingmatters.value.objects.values.matchers.ContainsValueObjectMatcher.containsObject;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ContainsValueObjectMatcherTest {
    @Test
    public void objectValueContainItself() {
        final ObjectValue actual = ObjectValue.builder()
                .property("anotherOne", v -> v.stringValue("bite the dust"))
                .property("smoothness", v -> v.longValue(100L))
                .build();

        final Matcher<ObjectValue> matcher = containsObject(
                ObjectValue.builder()
                        .property("smoothness", v -> v.longValue(100L))
                        .property("anotherOne", v -> v.stringValue("bite the dust"))
                        .build()
        );

        assertThat(matcher.matches(actual), is(true));
    }
}