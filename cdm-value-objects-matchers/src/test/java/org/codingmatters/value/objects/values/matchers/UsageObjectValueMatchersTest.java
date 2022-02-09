package org.codingmatters.value.objects.values.matchers;

import org.codingmatters.value.objects.generated.Dad;
import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.codingmatters.value.objects.values.matchers.ContainsValueObjectMatcher.containsValueObject;
import static org.codingmatters.value.objects.values.matchers.ObjectValueMatchers.containsProperties;
import static org.codingmatters.value.objects.values.matchers.ObjectValueMatchers.hasProperty;
import static org.codingmatters.value.objects.values.matchers.property.PropertyValueMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.hamcrest.number.OrderingComparison.greaterThan;

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
            .property("dogs", PropertyValue.multiple(PropertyValue.Type.OBJECT,
                    dog -> dog.objectValue(o -> o
                            .property("name", n -> n.stringValue("Jessy"))
                            .property("age", a -> a.longValue(7L))
                    ),
                    dog -> dog.objectValue(o -> o
                            .property("name", n -> n.stringValue("Joy"))
                            .property("age", a -> a.longValue(3L))
                    )
            ))
            .property("nullProperty", PropertyValue.builder().build())
            .build();

    @Test
    public void MarcoSanchez_IsAnObjectWithProperties() {
        assertThat(MARCO_SANCHEZ, containsProperties("name", "birthday", "bankAccounts", "homeowner", "children", "assets", "hash", "dogs", "nullProperty"));
    }

    @Test
    public void MarcoSanchez_DoNotHAve_VerbotenProperty() {
        assertThat(MARCO_SANCHEZ, not(hasProperty("VERBOTEN")));
    }

    @Test
    public void MarcoSanchez_hasAName() {
        assertThat(MARCO_SANCHEZ, hasProperty("name", withStringValue(startsWith("Marco"))));
    }

    @Test
    public void MarcoSanchez_IsBorn_25_June_1984() {
        assertThat(MARCO_SANCHEZ, hasProperty("birthday", withValue(LocalDate.of(1984, Month.JUNE, 25))));
    }

    @Test
    public void MarcoSanchez_Is_HomeOwner() {
        assertThat(MARCO_SANCHEZ, hasProperty("homeowner", withValue(true)));
    }

    @Test
    public void MarcoSanchez_HasMultipleBankAccounts() {
        assertThat(MARCO_SANCHEZ, hasProperty("bankAccounts", withValuesInAnyOrder(
                "43ce36f6-2d2b-4e25-9743-29974c042b94", "bb4b431d-bb10-4d94-b042-26ef8ce047b9", "8ed7dd78-9d69-46b4-8a92-f58b9bce680c")
        ));
    }

    @Test
    public void MarcoSanchez_HasMultipleBankAccounts_statedInOrder() {
        assertThat(MARCO_SANCHEZ, hasProperty("bankAccounts", withValues(
                "8ed7dd78-9d69-46b4-8a92-f58b9bce680c", "43ce36f6-2d2b-4e25-9743-29974c042b94", "bb4b431d-bb10-4d94-b042-26ef8ce047b9")
        ));
    }

    @Test
    public void MarcoSanchez_hasDogsMatchingNames() {
        assertThat(MARCO_SANCHEZ, hasProperty("dogs", withValues(
                hasProperty("name", withValue("Jessy")),
                hasProperty("name", withValue("Joy"))
        )));
    }

    @Test
    public void MarcoSanchez_isMillionaire() {
        assertThat(MARCO_SANCHEZ, hasProperty("assets", withDoubleValue(greaterThan(1_000_000d))));
    }

    @Test
    public void MarcoSanchez_isDaddy() {
        final Dad daddy = Dad.builder()
                .name("Marco Sanchez")
                .birthday(LocalDate.of(1984, Month.JUNE, 25))
                .children(3L)
                .build();

        assertThat(MARCO_SANCHEZ, containsValueObject(daddy));
    }
}