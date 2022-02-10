package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.junit.Test;

import static org.codingmatters.value.objects.values.matchers.property.value.ObjectValueMatcher.objectValue;
import static org.codingmatters.value.objects.values.matchers.property.value.ObjectValueMatcher.objectValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ObjectValueMatcherTest {
    private static final ObjectValue inside = ObjectValue.builder()
            .property("another", v->v.stringValue("one"))
            .property("bite", v->v.stringValue("the dust"))
            .build();

    @Test
    public void propertyObjectMatch() {
        final PropertyValue.Value value = PropertyValue.builder().objectValue(inside).buildValue();
        assertThat(objectValue(inside).matches(value), is(true));
    }

    @Test
    public void differentObjects__DontMatch() {
        final PropertyValue.Value value = PropertyValue.builder().objectValue(o -> o
                .property("the show", v->v.stringValue("Must go on"))
        ).buildValue();

        assertThat(objectValue(inside).matches(value), is(false));
    }

    @Test
    public void insideObject__MatchMatcher() {
        final PropertyValue.Value value = PropertyValue.builder().objectValue(inside).buildValue();
        final Matcher<ObjectValue> anotherPropertyPresence = new CustomTypeSafeMatcher<>("whatever") {
            @Override
            protected boolean matchesSafely(ObjectValue item) {
                return item.has("another");
            }
        };

        assertThat(ObjectValueMatcher.objectValue(anotherPropertyPresence).matches(value), is(true));
    }

    @Test
    public void noValue__DontMatch() {
        final PropertyValue.Value value = PropertyValue.builder().buildValue();
        assertThat(objectValue(inside).matches(value), is(false));
    }

    @Test
    public void nullValue__DontMatch() {
        final PropertyValue.Value value = null;
        assertThat(objectValue(inside).matches(value), is(false));
    }

    @Test
    public void stringValue__DontMatch() {
        final PropertyValue.Value value = PropertyValue.builder().stringValue("whatever").buildValue();
        assertThat(objectValue(inside).matches(value), is(false));
    }
}