package org.codingmatters.tests.reflect;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Method;
import java.util.List;

import static org.codingmatters.tests.reflect.ReflectMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/8/16.
 */
public class MethodMatcherTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    static class TestClass {
        public void publicMethod() {}
        private void privateMethod() {}
        protected void protectedMethod() {}
        void packagePrivateMethod() {}

        public String returnsString() {return "";}
        public void returnsVoid() {}

        public void withParameters(String s, int i) {}
        public void withoutParameters() {}

        static public void staticMethod() {}
        public void instanceMethod() {}

        public String complete(String arg1, String arg2) {return "";}
    }

    @Test
    public void completeSignature() throws Exception {
        assertThat(method("complete", String.class, String.class),
                is(
                        aMethod().named("complete").public_().withParameters(String.class, String.class).returning(String.class)
                )
        );
    }

    @Test
    public void isAMethod() throws Exception {
        assertThat(method("publicMethod"), is(aMethod()));
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
                is(aMethod().public_())
        );
    }

    @Test
    public void isAPublicMethod_failure() throws Exception {
        exception.expect(AssertionError.class);
        assertThat(
                method("privateMethod"),
                is(aMethod().public_())
        );
    }

    @Test
    public void isAPrivateMethod() throws Exception {
        assertThat(
                method("privateMethod"),
                is(aMethod().private_())
        );
    }

    @Test
    public void isAProtectedMethod() throws Exception {
        assertThat(
                method("protectedMethod"),
                is(aMethod().protected_())
        );
    }

    @Test
    public void isAPackagePrivateMethod() throws Exception {
        assertThat(
                method("packagePrivateMethod"),
                is(aMethod().packagePrivate())
        );
    }

    @Test
    public void returnsType() throws Exception {
        assertThat(method("returnsString"), is(aMethod().returning(String.class)));
    }

    @Test
    public void returnsVoid() throws Exception {
        assertThat(method("returnsVoid"), is(aMethod().returningVoid()));
        assertThat(method("returnsVoid"), is(aMethod().returning(void.class)));
    }

    @Test
    public void returnsType_failure() throws Exception {
        exception.expect(AssertionError.class);

        assertThat(method("returnsString"), is(aMethod().returning(List.class)));
    }

    @Test
    public void withParameters() throws Exception {
        assertThat(method("withParameters", String.class, int.class), is(aMethod().withParameters(String.class, int.class)));
    }

    @Test
    public void withoutParameters() throws Exception {
        assertThat(method("withoutParameters"), is(aMethod().withParameters()));
    }

    @Test
    public void withParameters_fails() throws Exception {
        exception.expect(AssertionError.class);
        assertThat(method("withParameters", String.class, int.class), is(aMethod().withParameters(String.class)));

        exception.expect(AssertionError.class);
        assertThat(method("withParameters", String.class, int.class), is(aMethod().withParameters()));

        exception.expect(AssertionError.class);
        assertThat(method("withoutParameters"), is(aMethod().withParameters(String.class)));
    }

    @Test
    public void staticMethod() throws Exception {
        assertThat(method("staticMethod"), is(aStaticMethod()));
        assertThat(method("staticMethod"), is(aMethod().static_()));
    }

    @Test
    public void staticMethod_fails() throws Exception {
        exception.expect(AssertionError.class);

        assertThat(method("instanceMethod"), is(aStaticMethod()));
    }

    @Test
    public void instanceMethod() throws Exception {
        assertThat(method("instanceMethod"), is(anInstanceMethod()));
        assertThat(method("instanceMethod"), is(aMethod().notStatic()));
    }

    @Test
    public void instanceMethod_fails() throws Exception {
        exception.expect(AssertionError.class);

        assertThat(method("staticMethod"), is(anInstanceMethod()));
    }

    private Method method(String name, Class ... args) throws NoSuchMethodException {
        return TestClass.class.getDeclaredMethod(name, args);
    }
}