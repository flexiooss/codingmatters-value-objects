package org.codingmatters.tests.reflect;

import org.junit.Test;

import java.lang.reflect.Field;

import static org.codingmatters.tests.reflect.ReflectMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/11/16.
 */
public class FieldMatcherTest {

    static public class TestClass {
        public String name;

        public String publicField;
        private String privateField;
        protected String protectedField;
        String packagePrivateField;

        public String instanceField;
        static public String staticField;
    }

    @Test
    public void namedField() throws Exception {
        assertThat(field("name"), aField().named("name"));
    }

    @Test
    public void publicField() throws Exception {
        assertThat(field("publicField"), aField().public_());
    }

    @Test
    public void privateField() throws Exception {
        assertThat(field("privateField"), aField().private_());
    }

    @Test
    public void protectedField() throws Exception {
        assertThat(field("protectedField"), aField().protected_());
    }

    @Test
    public void packagePrivateField() throws Exception {
        assertThat(field("packagePrivateField"), aField().packagePrivate());
    }

    @Test
    public void instanceField() throws Exception {
        assertThat(field("instanceField"), anInstanceField());
    }

    @Test
    public void staticField() throws Exception {
        assertThat(field("staticField"), aStaticField());
    }



    private Field field(String name, Class ... args) throws NoSuchFieldException {
        return TestClass.class.getDeclaredField(name);
    }
}
