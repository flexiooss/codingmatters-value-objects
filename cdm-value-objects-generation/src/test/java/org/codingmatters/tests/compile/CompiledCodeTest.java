package org.codingmatters.tests.compile;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.FileWriter;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/6/16.
 */
public class CompiledCodeTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private File dir;
    private CompiledCode compiled;

    @Before
    public void setUp() throws Exception {
        this.dir = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
        dir.mkdirs();
        File packageDir = new File(this.dir, "org/codingmatters");
        packageDir.mkdirs();
        File helloWorld = new File(packageDir, "HelloWorld.java");

        try(FileWriter writer = new FileWriter(helloWorld)) {
            writer.write(
                    "package org.codingmatters;\n" +
                    "\n" +
                    "public class HelloWorld {\n" +
                    "    public HelloWorld() {}\n" +
                    "\n" +
                    "    public String sayHello() {return \"Hello, World\";}\n" +
                    "\n" +
                    "    public static void main(String[] args) {\n" +
                    "        System.out.println(new HelloWorld().sayHello());\n" +
                    "    }\n" +
                    "}"
            );
            writer.flush();
        }

        this.compiled = CompiledCode.compile(this.dir);
    }

    @After
    public void tearDown() throws Exception {
        this.delete(this.dir);
    }

    private void delete(File file) {
        if(file.isDirectory()) {
            for (File child : file.listFiles()) {
                this.delete(child);
            }
        }
        file.delete();
    }

    @Test
    public void getCompiledClass() throws Exception {
        assertThat(compiled.getClass("org.codingmatters.HelloWorld"), is(notNullValue()));
        assertThat(compiled.getClass("org.codingmatters.NoSuchClass"), is(nullValue()));
    }

    @Test
    public void classMatcher_methodNameMatches() throws Exception {
        assertThat(compiled.getClass("org.codingmatters.HelloWorld"), ClassMatchers.hasMethod("sayHello"));
    }

    @Test
    public void classMatcher_methodNameDosentMatch_throwsAssertionError() throws Exception {
        this.exception.expect(AssertionError.class);
        this.exception.expectMessage(
                "Expected: class has method named \"undefined\"\n" +
                "     but: class has the following methods "
        );
        assertThat(compiled.getClass("org.codingmatters.HelloWorld"), ClassMatchers.hasMethod("undefined"));
    }
}
