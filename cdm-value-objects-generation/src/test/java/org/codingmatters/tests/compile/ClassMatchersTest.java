package org.codingmatters.tests.compile;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/6/16.
 */
public class ClassMatchersTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private void delete(File file) {
        if(file.isDirectory()) {
            for (File child : file.listFiles()) {
                this.delete(child);
            }
        }
        file.delete();
    }

    @Test
    public void aClass() throws Exception {
        assertThat(String.class, is(ClassMatchers.isAClass()));
    }

    @Test
    public void classWithName() throws Exception {
        assertThat(String.class, ClassMatchers.isAClass().withName("java.lang.String"));
    }

    @Test
    public void classHasNotName() throws Exception {
        exception.expect(AssertionError.class);
        exception.expectMessage(
                "Expected: (classname is \"NotThisName\")\n" +
                "     but: classname is \"NotThisName\" <class java.lang.String> classname was \"java.lang.String\""
        );

        assertThat(String.class, ClassMatchers.isAClass().withName("NotThisName"));
    }


}