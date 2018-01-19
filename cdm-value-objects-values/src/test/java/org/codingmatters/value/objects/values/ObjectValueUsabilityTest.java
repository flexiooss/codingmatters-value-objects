package org.codingmatters.value.objects.values;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

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
}