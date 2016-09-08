package org.codingmatters.tests.reflect;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.codingmatters.tests.reflect.ReflectMatchers.isAClass;
import static org.codingmatters.tests.reflect.ReflectMatchers.isAMethod;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/6/16.
 */
public class ClassMatchersTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void aClass() throws Exception {
        assertThat(String.class, isAClass());
    }

    @Test
    public void classWithName() throws Exception {
        assertThat(String.class, isAClass().withName("java.lang.String"));
    }

    @Test
    public void classWithName_failure() throws Exception {
        exception.expect(AssertionError.class);
        exception.expectMessage(
                "Expected: (class name is \"NotThisName\")\n" +
                "     but: class name is \"NotThisName\" <class java.lang.String> class name was \"java.lang.String\""
        );

        assertThat(String.class, isAClass().withName("NotThisName"));
    }

    @Test
    public void classWithMethod() throws Exception {
        assertThat(String.class, isAClass().withMethod(isAMethod().withName("toUpperCase")));
    }

    @Test
    public void classWithManyMethod() throws Exception {
        assertThat(String.class, isAClass()
                .withMethod(isAMethod().withName("toUpperCase"))
                .withMethod(isAMethod().withName("toLowerCase")));
    }

    @Test
    public void classWithMethod_failure() throws Exception {
        exception.expect(AssertionError.class);
        assertThat(String.class, isAClass().withMethod(isAMethod().withName("noSuchMeth")));
    }
}