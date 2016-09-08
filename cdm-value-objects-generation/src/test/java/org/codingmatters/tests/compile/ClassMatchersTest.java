package org.codingmatters.tests.compile;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/6/16.
 */
public class ClassMatchersTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void aClass() throws Exception {
        assertThat(String.class, ClassMatcher.isAClass());
    }

    @Test
    public void classWithName() throws Exception {
        assertThat(String.class, ClassMatcher.isAClass().withName("java.lang.String"));
    }

    @Test
    public void classHasNotName() throws Exception {
        exception.expect(AssertionError.class);
        exception.expectMessage(
                "Expected: (class name is \"NotThisName\")\n" +
                "     but: class name is \"NotThisName\" <class java.lang.String> class name was \"java.lang.String\""
        );

        assertThat(String.class, ClassMatcher.isAClass().withName("NotThisName"));
    }


}