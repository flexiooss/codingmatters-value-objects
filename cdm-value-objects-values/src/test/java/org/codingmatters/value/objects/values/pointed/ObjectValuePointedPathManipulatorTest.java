package org.codingmatters.value.objects.values.pointed;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class ObjectValuePointedPathManipulatorTest {
    @Test
    public void empty() {
        ObjectValue value = ObjectValue.builder().build();
        ObjectValue res = new ObjectValuePointedPathManipulator(value).updateValueAt("", ObjectValue.builder().build());
        assertThat(res, is(value));

        res = new ObjectValuePointedPathManipulator(value).updateValueAt("plok", null);
        assertThat(res, is(value));
    }

    @Test
    public void updateSimpleObject() {
        ObjectValue value = ObjectValue.builder().build();
        ObjectValue res = new ObjectValuePointedPathManipulator(value).updateValueAt("toto", ObjectValue.builder()
                .property("a", v -> v.stringValue("plok"))
                .build());
        assertThat(res, is(ObjectValue.builder()
                .property("toto", v -> v.objectValue(
                        ObjectValue.builder()
                                .property("a", a -> a.stringValue("plok"))
                                .build()
                ))
                .build()));
    }

    @Test
    public void updateSimpleNestedObject() {
        ObjectValue value = ObjectValue.builder().build();
        ObjectValue res = new ObjectValuePointedPathManipulator(value).updateValueAt("toto.plok", ObjectValue.builder()
                .property("a", v -> v.stringValue("plok"))
                .build());
        assertThat(res, is(
                ObjectValue.builder().property("toto", t -> t.objectValue(
                        ObjectValue.builder().property("plok", v -> v.objectValue(
                                        ObjectValue.builder()
                                                .property("a", a -> a.stringValue("plok"))
                                                .build()
                                ))
                                .build()
                )).build()
        ));

        ObjectValue nonEmptyValue = ObjectValue.builder()
                .property("toto", t -> t.objectValue(
                        ObjectValue.builder()
                                .property("tata", ta -> ta.stringValue("tata value"))
                                .property("plok", p -> p.objectValue(
                                        ObjectValue.builder()
                                                .property("a", a -> a.stringValue("plok old value"))
                                                .property("b", b -> b.stringValue("b value"))
                                                .build()
                                ))
                                .build()
                ))
                .build();
        ObjectValue resWithNonEmpty = new ObjectValuePointedPathManipulator(nonEmptyValue).updateValueAt("toto.plok", ObjectValue.builder()
                .property("a", v -> v.stringValue("plok"))
                .build());
        assertThat(resWithNonEmpty, is(
                ObjectValue.builder().property("toto", t -> t.objectValue(
                        ObjectValue.builder()
                                .property("tata", ta -> ta.stringValue("tata value"))
                                .property("plok", v -> v.objectValue(
                                        ObjectValue.builder()
                                                .property("a", a -> a.stringValue("plok"))
                                                .build()
                                ))
                                .build()
                )).build()
        ));
    }

    @Test
    public void updateMultiple() {
        ObjectValue value = ObjectValue.builder().build();
        ObjectValue res = new ObjectValuePointedPathManipulator(value).updateValueAt("toto[0]", ObjectValue.builder()
                .property("a", v -> v.stringValue("plok"))
                .build());
        assertThat(res, is(ObjectValue.builder()
                .property("toto", PropertyValue.multiple(PropertyValue.Type.OBJECT, v -> v.objectValue(
                        ObjectValue.builder()
                                .property("a", a -> a.stringValue("plok"))
                                .build()
                )))
                .build()));
    }

    @Test
    public void updateBeforeNestedMultiple() {
        ObjectValue value = ObjectValue.builder().build();
        ObjectValue res = new ObjectValuePointedPathManipulator(value).updateValueAt("tutu.toto[0]", ObjectValue.builder()
                .property("a", v -> v.stringValue("plok"))
                .build());
        assertThat(res, is(
                ObjectValue.builder().property("tutu", t -> t.objectValue(
                                ObjectValue.builder().property("toto", PropertyValue.multiple(PropertyValue.Type.OBJECT, v -> v.objectValue(
                                        ObjectValue.builder()
                                                .property("a", a -> a.stringValue("plok"))
                                                .build()
                                )))
                        ))
                        .build()));
    }

    @Test
    public void updateBeforeNestedMultipleIndex3() {
        ObjectValue value = ObjectValue.builder().build();
        ObjectValue res = new ObjectValuePointedPathManipulator(value).updateValueAt("tutu.toto[2]", ObjectValue.builder()
                .property("a", v -> v.stringValue("plok"))
                .build());
        assertThat(res, is(
                ObjectValue.builder().property("tutu", t -> t.objectValue(
                                ObjectValue.builder().property("toto", PropertyValue.multiple(PropertyValue.Type.OBJECT,
                                        v -> v.objectValue(ObjectValue.builder().build()),
                                        v -> v.objectValue(ObjectValue.builder().build()),
                                        v -> v.objectValue(
                                                ObjectValue.builder()
                                                        .property("a", a -> a.stringValue("plok"))
                                                        .build()
                                        )))
                        ))
                        .build()));
    }

    @Test
    public void updateAfterNestedMultiple() {
        ObjectValue value = ObjectValue.builder().build();
        ObjectValue res = new ObjectValuePointedPathManipulator(value).updateValueAt("toto[0].tutu", ObjectValue.builder()
                .property("a", v -> v.stringValue("plok"))
                .build());
        assertThat(res, is(
                ObjectValue.builder().property("toto", PropertyValue.multiple(PropertyValue.Type.OBJECT, v -> v.objectValue(
                                ObjectValue.builder().property("tutu", t -> t.objectValue(
                                        ObjectValue.builder()
                                                .property("a", a -> a.stringValue("plok"))
                                                .build()
                                )).build()
                        )))
                        .build()));
    }

    @Test
    public void emptyValueAtPath() {
        PropertyValue res = new ObjectValuePointedPathManipulator(null).valueAtPath("plok");
        assertThat(res, is(nullValue()));
    }

    @Test
    public void simpleValueAtPath() {
        ObjectValue value = ObjectValue.builder()
                .property("a", v -> v.stringValue("a"))
                .build();
        ObjectValue base = ObjectValue.builder()
                .property("plok", v -> v.objectValue(value))
                .build();
        PropertyValue res = new ObjectValuePointedPathManipulator(base).valueAtPath("plok");
        assertThat(res, is(PropertyValue.builder().objectValue(value).build()));
    }

    @Test
    public void simpleNestedValueAtPath() {
        ObjectValue value = ObjectValue.builder().property("a", v -> v.stringValue("b")).build();
        ObjectValue tutu = ObjectValue.builder().property("tutu", v -> v.objectValue(value)).build();
        ObjectValue base = ObjectValue.builder().property("plok", v -> v.objectValue(tutu)).build();
        PropertyValue res = new ObjectValuePointedPathManipulator(base).valueAtPath("plok.tutu.a");
        assertThat(res, is(PropertyValue.builder().stringValue("b").build()));
    }

    @Test
    public void simpleNestedNullValueAtPath() {
        ObjectValue value = null;
        ObjectValue tutu = ObjectValue.builder().property("tutu", v -> v.objectValue(value)).build();
        ObjectValue base = ObjectValue.builder().property("plok", v -> v.objectValue(tutu)).build();
        PropertyValue res = new ObjectValuePointedPathManipulator(base).valueAtPath("plok.tutu");
        assertThat(res, is(PropertyValue.builder().build()));
    }

    @Test
    public void multipleValueAtPath() {
        ObjectValue value = ObjectValue.builder().property("a", v -> v.stringValue("b")).build();
        ObjectValue base = ObjectValue.builder().property("plok", PropertyValue.multiple(PropertyValue.Type.OBJECT, v -> v.objectValue(value))).build();
        PropertyValue res = new ObjectValuePointedPathManipulator(base).valueAtPath("plok[0]");
        assertThat(res, is(PropertyValue.builder().objectValue(value).build()));
    }

    @Test
    public void multipleNullValueAtPath() {
        ObjectValue value = null;
        ObjectValue base = ObjectValue.builder().property("plok", PropertyValue.multiple(PropertyValue.Type.OBJECT, v -> v.objectValue(value))).build();
        PropertyValue res = new ObjectValuePointedPathManipulator(base).valueAtPath("plok[0]");
        assertThat(res, is(PropertyValue.builder().build()));
    }

    @Test
    public void multipleNestedValueAtPath() {
        ObjectValue value = ObjectValue.builder().property("a", v -> v.stringValue("b")).build();
        ObjectValue tutu = ObjectValue.builder().property("tutu", v -> v.objectValue(value)).build();
        ObjectValue base = ObjectValue.builder().property("plok", PropertyValue.multiple(PropertyValue.Type.OBJECT, v -> v.objectValue(tutu))).build();
        PropertyValue res = new ObjectValuePointedPathManipulator(base).valueAtPath("plok[0].tutu");
        assertThat(res, is(PropertyValue.builder().objectValue(value).build()));
    }

    @Test
    public void multipleNullValue() {
        PropertyValue prop = new ObjectValuePointedPathManipulator(ObjectValue.builder().property("prop", (PropertyValue) null).build())
                .valueAtPath("prop[0]");
        assertThat(prop, nullValue());
    }
}
