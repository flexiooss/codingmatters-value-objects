package org.codingmatters.value.objects.values;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ObjectValueUsabilityTest {

    @Test
    public void string() throws Exception {
        ObjectValue value = ObjectValue.builder()
                .property("prop", PropertyValue.builder().stringValue("str"))
                .property("listProp", PropertyValue.multiple(PropertyValue.Type.STRING, PropertyValue.builder().stringValue("str")))
                .build();

        assertThat(value.toString(), is("{listProp=[str (STRING)], prop=str (STRING)}"));

        assertThat(value.property("prop").single().stringValue(), is("str"));
    }

    @Test
    public void singleNonNullProperty() {
        final ObjectValue singlePropertyObjectValue = ObjectValue.builder()
                .property("prop", v -> v.stringValue("value"))
                .property("nullProp", PropertyValue.builder().build())
                .build();

        assertFalse(singlePropertyObjectValue.singleNonNullProperty("anotherOne", PropertyValue.Type.STRING).isPresent());
        assertFalse(singlePropertyObjectValue.singleNonNullProperty("prop", PropertyValue.Type.OBJECT).isPresent());
        assertFalse(singlePropertyObjectValue.singleNonNullProperty("nullProp", PropertyValue.Type.STRING).isPresent());
        assertThat(singlePropertyObjectValue.singleNonNullProperty("prop", PropertyValue.Type.STRING).get(),
                is(PropertyValue.builder().stringValue("value").build())
        );

        final ObjectValue multipleStringPropertyObjectValue = ObjectValue.builder()
                .property("prop", PropertyValue.multiple(PropertyValue.Type.STRING,
                        v -> v.stringValue("value1"),
                        v -> v.stringValue("value2")
                ))
                .build();

        assertFalse(multipleStringPropertyObjectValue.singleNonNullProperty("prop", PropertyValue.Type.STRING).isPresent());

        final ObjectValue multipleNumberPropertyObjectValue = ObjectValue.builder()
                .property("prop", PropertyValue.multiple(PropertyValue.Type.DOUBLE,
                        v -> v.doubleValue(0d),
                        v -> v.doubleValue(1d)
                ))
                .build();

        assertFalse(multipleNumberPropertyObjectValue.singleNonNullProperty("prop", PropertyValue.Type.STRING).isPresent());
    }

    @Test
    public void multipleNonNullProperty() {
        final ObjectValue multiplePropertyObjectValue = ObjectValue.builder()
                .property("prop", PropertyValue.multiple(PropertyValue.Type.STRING,
                        v -> v.stringValue("value1"),
                        v -> v.stringValue("value2")
                ))
                .build();

        assertFalse(multiplePropertyObjectValue.multipleNonNullProperty("anotherOne", PropertyValue.Type.STRING).isPresent());
        assertFalse(multiplePropertyObjectValue.multipleNonNullProperty("prop", PropertyValue.Type.OBJECT).isPresent());
        assertThat(multiplePropertyObjectValue.multipleNonNullProperty("prop", PropertyValue.Type.STRING).get(),
                is(PropertyValue.multiple(PropertyValue.Type.STRING,
                        v -> v.stringValue("value1"),
                        v -> v.stringValue("value2")
                ))
        );

        final ObjectValue singlePropertyObjectValue = ObjectValue.builder()
                .property("prop", v -> v.objectValue(ObjectValue.builder().build()))
                .build();

        assertFalse(singlePropertyObjectValue.multipleNonNullProperty("prop", PropertyValue.Type.STRING).isPresent());
    }

    @Test
    public void object() throws Exception {
        ObjectValue value = ObjectValue.builder()
                .property("prop", PropertyValue.builder().objectValue(ObjectValue.builder().property("str", builder -> builder.stringValue("str")).build()))
                .build();

        assertThat(value.toString(), is("{prop={str=str (STRING)} (OBJECT)}"));
    }

    @Test
    public void deepObject() throws Exception {
        ObjectValue value = ObjectValue.builder()
                .property("prop", PropertyValue.builder().objectValue(ObjectValue.builder()
                        .property("deep", prop -> prop.objectValue(
                                object -> object.property("str", builder -> builder.stringValue("str"))
                        ))
                        .build())

                )
                .build();

        assertThat(value.toString(), is("{prop={deep={str=str (STRING)} (OBJECT)} (OBJECT)}"));
    }

    @Test
    public void changer() throws Exception {
        ObjectValue value = ObjectValue.builder()
                .property("nested", v -> v.objectValue(o -> o
                        .property("prop", pv -> pv.stringValue("unchanged"))))
                .build();

        ObjectValue changed = value.withChangedProperty("nested", v -> v.objectValue(o -> o.property("prop", pv -> pv.stringValue("changed"))));

        assertThat(changed.toString(), is("{nested={prop=changed (STRING)} (OBJECT)}"));
    }

    @Test
    public void givenMultiplePropertyValue__whenGettingValueFromIndex_andBuildingPropertyValueFromValue__thenPropertyValueProperlyBuilt() throws Exception {
        ObjectValue o = ObjectValue.builder()
                .property("toto", PropertyValue.multiple(PropertyValue.Type.STRING, va -> va.stringValue("tutu"), va -> va.stringValue("tata")))
                .build();
        int index = 1;

        PropertyValue.Value val;
        if (o.property("toto").cardinality().equals(PropertyValue.Cardinality.MULTIPLE)) {
            val = o.property("toto").multiple()[index];
        } else {
            val = o.property("toto").single();
        }

        PropertyValue value = PropertyValue.single(val);

        assertThat(value.type(), is(PropertyValue.Type.STRING));
        assertThat(value.cardinality(), is(PropertyValue.Cardinality.SINGLE));
        assertThat(value.single().stringValue(), is("tata"));
    }

    @Test
    public void givenNullProperty() {
        ObjectValue value = ObjectValue.builder().property("prop", (PropertyValue) null).build();
        assertTrue(value.singleNonNullProperty("prop", PropertyValue.Type.OBJECT).isEmpty());
    }

    @Test
    public void givenListEmpty() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("plok", List.of());
        ObjectValue value = ObjectValue.fromMap(map).build();
        assertThat(value.multipleNonNullProperty("plok", PropertyValue.Type.STRING).isPresent(), is(true));
        assertThat(value.multipleNonNullProperty("plok", PropertyValue.Type.OBJECT).isPresent(), is(true));
    }

    @Test
    public void givenTypedList() {
        ObjectValue multipleString = ObjectValue.builder()
                .property("plok", PropertyValue.multiple(PropertyValue.Type.STRING, v -> v.stringValue("plok")))
                .build();
        assertThat(multipleString.multipleNonNullProperty("plok", PropertyValue.Type.STRING).isPresent(), is(true));
        assertThat(multipleString.multipleNonNullProperty("plok", PropertyValue.Type.OBJECT).isPresent(), is(false));

        multipleString = ObjectValue.builder()
                .property("plok", PropertyValue.multiple(PropertyValue.Type.STRING, PropertyValue.builder().buildValue()))
                .build();
        // build a value multiple null string

        assertThat(multipleString.multipleNonNullProperty("plok", PropertyValue.Type.STRING).isPresent(), is(true));
        assertThat(multipleString.multipleNonNullProperty("plok", PropertyValue.Type.OBJECT).isPresent(), is(false));

        ObjectValue multipleObject = ObjectValue.builder()
                .property("plok", PropertyValue.multiple(PropertyValue.Type.OBJECT,
                        v -> v.objectValue(ObjectValue.builder().build())
                ))
                .build();
        assertThat(multipleObject.multipleNonNullProperty("plok", PropertyValue.Type.STRING).isPresent(), is(false));
        assertThat(multipleObject.multipleNonNullProperty("plok", PropertyValue.Type.OBJECT).isPresent(), is(true));

        multipleObject = ObjectValue.builder()
                .property("plok", PropertyValue.multiple(PropertyValue.Type.OBJECT, PropertyValue.builder().buildValue()))
                .build();
        assertThat(multipleObject.multipleNonNullProperty("plok", PropertyValue.Type.STRING).isPresent(), is(false));
        assertThat(multipleObject.multipleNonNullProperty("plok", PropertyValue.Type.OBJECT).isPresent(), is(true));
    }

    @Test
    public void withoutPropertyCopy() throws Exception {
        ObjectValue value = ObjectValue.builder()
                .property("p1", v -> v.stringValue("v1"))
                .property("p2", v -> v.stringValue("v2"))
                .build();

        ObjectValue copy = value.withoutProperty("p2");
        assertThat(copy.propertyNames(), arrayContaining("p1"));
    }
}
