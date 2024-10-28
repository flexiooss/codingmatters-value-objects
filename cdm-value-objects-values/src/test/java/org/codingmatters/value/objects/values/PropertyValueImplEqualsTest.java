package org.codingmatters.value.objects.values;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.hamcrest.Matchers.*;

class PropertyValueImplEqualsTest {

    @Test
    void givenMultipleEmptyArray__whenOneTyped_oneUntyped__thenEquals() throws Exception {
        PropertyValueImpl typed = new PropertyValueImpl(PropertyValue.Type.STRING, PropertyValue.Cardinality.MULTIPLE, new PropertyValue.Value[0]);
        PropertyValueImpl untyped = new PropertyValueImpl(null, PropertyValue.Cardinality.MULTIPLE, new PropertyValue.Value[0]);

        assertThat(typed, is(untyped));
        assertThat(untyped, is(typed));
    }
    @Test
    void givenMultipleEmptyArray__whenSameType__thenEquals() throws Exception {
        PropertyValueImpl typed = new PropertyValueImpl(PropertyValue.Type.STRING, PropertyValue.Cardinality.MULTIPLE, new PropertyValue.Value[0]);
        PropertyValueImpl untyped = new PropertyValueImpl(PropertyValue.Type.STRING, PropertyValue.Cardinality.MULTIPLE, new PropertyValue.Value[0]);

        assertThat(typed, is(untyped));
        assertThat(untyped, is(typed));
    }
    @Test
    void givenMultipleEmptyArray__whenDifferentType__thenNotEqual() throws Exception {
        PropertyValueImpl typed = new PropertyValueImpl(PropertyValue.Type.STRING, PropertyValue.Cardinality.MULTIPLE, new PropertyValue.Value[0]);
        PropertyValueImpl untyped = new PropertyValueImpl(PropertyValue.Type.DATE, PropertyValue.Cardinality.MULTIPLE, new PropertyValue.Value[0]);

        assertThat(typed, is(not(untyped)));
        assertThat(untyped, is(not(typed)));
    }
}