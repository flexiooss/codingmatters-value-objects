package org.codingmatters.value.objects.values.matchers.property;

import org.codingmatters.value.objects.values.PropertyValue;
import org.junit.Test;

import static org.codingmatters.value.objects.values.matchers.property.ContainsMultipleValuesInOrderMatcher.multipleInOrder;
import static org.codingmatters.value.objects.values.matchers.property.value.StringValueMatcher.stringValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ContainsMultipleValuesInOrderMatcherTest {
    private static final PropertyValue.Value[] values = {
            PropertyValue.builder().stringValue("value1").buildValue(),
            PropertyValue.builder().stringValue("value2").buildValue(),
            PropertyValue.builder().stringValue("value3").buildValue()
    };

    @Test
    public void singleProperty__DoNotMatch() {
        final PropertyValue property = PropertyValue.builder().stringValue("whatever").build();
        assertThat(multipleInOrder(values).matches(property), is(false));
    }

    @Test
    public void nullProperty__DoNotMatch() {
        final PropertyValue property = null;
        assertThat(multipleInOrder(values).matches(property), is(false));
    }

    @Test
    public void noProperty__DoNotMatch() {
        final PropertyValue property = PropertyValue.builder().build();
        assertThat(multipleInOrder(values).matches(property), is(false));
    }

    @Test
    public void multiplePropertiesInOrder__ThatMatch() {
        final PropertyValue property = PropertyValue.multiple(PropertyValue.Type.STRING,
                v -> v.stringValue("value1"),
                v -> v.stringValue("value2"),
                v -> v.stringValue("value3")
        );
        assertThat(multipleInOrder(values).matches(property), is(true));
    }

    @Test
    public void multiplePropertiesInReverseOrder__ThatMatch() {
        final PropertyValue property = PropertyValue.multiple(PropertyValue.Type.STRING,
                v -> v.stringValue("value3"),
                v -> v.stringValue("value2"),
                v -> v.stringValue("value1")
        );
        assertThat(multipleInOrder(values).matches(property), is(false));
    }

    @Test
    public void multipleProperties__ThatDoNotMatch() {
        final PropertyValue property = PropertyValue.multiple(PropertyValue.Type.DOUBLE,
                v -> v.doubleValue(3d),
                v -> v.doubleValue(5d),
                v -> v.doubleValue(8d)
        );
        assertThat(multipleInOrder(values).matches(property), is(false));
    }

    @Test
    public void multiplePropertiesWithMatchers__ThatMatch() {
        final PropertyValue property = PropertyValue.multiple(PropertyValue.Type.STRING,
                v -> v.stringValue("value1"),
                v -> v.stringValue("value2"),
                v -> v.stringValue("value3")
        );
        assertThat(
                multipleInOrder(
                        stringValue("value1"),
                        stringValue("value2"),
                        stringValue("value3")
                ).matches(property)
                , is(true)
        );
    }
}