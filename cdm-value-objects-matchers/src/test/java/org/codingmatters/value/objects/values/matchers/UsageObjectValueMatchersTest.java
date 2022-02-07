package org.codingmatters.value.objects.values.matchers;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.codingmatters.value.objects.values.matchers.ObjectValueMatchers.*;
import static org.codingmatters.value.objects.values.matchers.property.PropertyValueMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

public class UsageObjectValueMatchersTest {
    private static final ObjectValue MARCO_SANCHEZ = ObjectValue.builder()
            .property("name", v -> v.stringValue("Marco Sanchez"))
            .property("birthday", v -> v.dateValue(LocalDate.of(1984, Month.JUNE, 25)))
            .property("bankAccounts", PropertyValue.multiple(PropertyValue.Type.STRING,
                    v -> v.stringValue("8ed7dd78-9d69-46b4-8a92-f58b9bce680c"),
                    v -> v.stringValue("43ce36f6-2d2b-4e25-9743-29974c042b94"),
                    v -> v.stringValue("bb4b431d-bb10-4d94-b042-26ef8ce047b9")
            ))
            .property("homeowner", v -> v.booleanValue(true))
            .property("children" , v -> v.longValue(3L))
            .property("assets", v -> v.doubleValue(354_877_032.16))
            .property("hash", v -> v.bytesValue("81eab250cc6fdfb971eaf0309ff3fae27890344deac751376e".getBytes()))
            .property("dogs", v -> PropertyValue.multiple(PropertyValue.Type.OBJECT,
                    dog -> dog.objectValue(o -> o
                            .property("name", n -> n.stringValue("Jessy"))
                            .property("age", a -> a.longValue(7L))
                    ),
                    dog -> dog.objectValue(o -> o
                            .property("name", n -> n.stringValue("Joy"))
                            .property("age", a -> a.longValue(3L))
                    )
            ))
            .build();

    @Test
    public void objectContainsProperties() {
        assertThat(MARCO_SANCHEZ, containsProperties("name", "birthday", "bankAccounts", "homeowner", "children", "assets", "hash", "dogs"));
    }

    @Test
    public void objectDoesNotHavePropertyVerbotenProperties() {
        assertThat(MARCO_SANCHEZ, not(hasProperty("VERBOTEN")));
    }

    @Test
    public void testName() {
        assertThat(MARCO_SANCHEZ, hasProperty("name", withValue("Marco Sanchez")));
    }
}