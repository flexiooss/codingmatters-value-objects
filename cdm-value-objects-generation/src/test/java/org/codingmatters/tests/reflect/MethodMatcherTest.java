package org.codingmatters.tests.reflect;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Method;
import java.util.List;

import static org.codingmatters.tests.reflect.ReflectMatchers.aMethod;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/8/16.
 */
public class MethodMatcherTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    class TestClass {
        public void publicMethod() {}
        private void privateMethod() {}
        protected void protectedMethod() {}
        void packagePrivateMethod() {}

        public String returnsString() {return "";}
    }

    @Test
    public void isAMethod() throws Exception {
        String name = "publicMethod";
        assertThat(method(name), is(aMethod()));
    }

    @Test
    public void aMethodWithName() throws Exception {
        assertThat(method("publicMethod"), is(aMethod().named("publicMethod")));
    }

    @Test
    public void aMethodWithName_failure() throws Exception {
        exception.expect(AssertionError.class);

        assertThat(method("publicMethod"), is(aMethod().named("noNamedLikeThat")));
    }

    @Test
    public void isAPublicMethod() throws Exception {
        assertThat(
                method("publicMethod"),
                is(aMethod().thatIsPublic())
        );
    }

    @Test
    public void isAPublicMethod_failure() throws Exception {
        exception.expect(AssertionError.class);
        assertThat(
                method("privateMethod"),
                is(aMethod().thatIsPublic())
        );
    }

    @Test
    public void isAPrivateMethod() throws Exception {
        assertThat(
                method("privateMethod"),
                is(aMethod().thatIsPrivate())
        );
    }

    @Test
    public void isAProtectedMethod() throws Exception {
        assertThat(
                method("protectedMethod"),
                is(aMethod().thatIsProtected())
        );
    }

    @Test
    public void isAPackagePrivateMethod() throws Exception {
        assertThat(
                method("packagePrivateMethod"),
                is(aMethod().thatIsPackagePrivateMethod())
        );
    }

    @Test
    public void returnsType() throws Exception {
        assertThat(method("returnsString"), is(aMethod().returning(String.class)));
    }

    @Test
    public void returnsType_failure() throws Exception {
        exception.expect(AssertionError.class);

        assertThat(method("returnsString"), is(aMethod().returning(List.class)));
    }

    @Test
    public void returnsVoid() throws Exception {
        assertThat(method("publicMethod"), is(aMethod().returningVoid()));
        assertThat(method("publicMethod"), is(aMethod().returning(void.class)));
    }

    private Method method(String name) throws NoSuchMethodException {
        return TestClass.class.getDeclaredMethod(name);
    }
}