package org.codingmatters.value.objects.json;

import com.fasterxml.jackson.core.JsonGenerator;
import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.value.objects.exception.LowLevelSyntaxException;
import org.codingmatters.value.objects.exception.SpecSyntaxException;
import org.codingmatters.value.objects.generation.SpecCodeGenerator;
import org.codingmatters.value.objects.reader.SpecReader;
import org.codingmatters.value.objects.spec.Spec;
import org.generated.ExampleValue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

import static org.codingmatters.tests.reflect.ReflectMatchers.aClass;
import static org.codingmatters.tests.reflect.ReflectMatchers.aPublic;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 3/30/17.
 */
public class JsonWriterGenerationTest {
    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    private final Spec spec = loadSpec();

    static private Spec loadSpec() {
        try {
            return new SpecReader().read(Thread.currentThread().getContextClassLoader().getResourceAsStream("spec.yaml"));
        } catch (IOException | SpecSyntaxException | LowLevelSyntaxException e) {
            throw new RuntimeException("error loading spec", e);
        }
    }

    private CompiledCode compiled;

    @Before
    public void setUp() throws Exception {
        new SpecCodeGenerator(this.spec, "org.generated", dir.getRoot()).generate();
        new JsonFrameworkGenerator(this.spec, "org.generated", dir.getRoot()).generate();
        this.compiled = new CompiledCode.Builder()
                .classpath(CompiledCode.findInClasspath(".*jackson-core-.*.jar"))
                .source(this.dir.getRoot())
                .compile();
    }

    @Test
    public void writerMethods() throws Exception {
        assertThat(
                this.compiled.getClass("org.generated.json.ExampleValueWriter"),
                is(aClass()
                        .with(aPublic().method().named("write").withParameters(ExampleValue.class).returning(String.class))
                        // public void write(JsonGenerator generator, ExampleValue value) throws IOException
                        .with(aPublic().method().named("write").withParameters(JsonGenerator.class, ExampleValue.class).returningVoid())
                )
        );
    }

    @Ignore
    @Test
    public void writeSimpleProperties() throws Exception {
        ExampleValue value = ExampleValue.Builder.builder()
                .prop("a value")
                .listProp("a", "b", "c")
                .build();

        Object writer = this.compiled.getClass("org.generated.json.ExampleValueWriter").newInstance();
        String json = this.compiled.on(writer).invoke("write", ExampleValue.class).with(value);
        assertThat(
                json,
                is("{" +
                        "\"prop\":\"a value\"," +
                        "\"listProp\":[\"a\",\"b\",\"c\"]," +
                        "\"complex\":null," +
                        "\"complexList\":null" +
                        "}")
        );
    }

}
