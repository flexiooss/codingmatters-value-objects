package org.codingmatters.value.objects.json;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.value.objects.exception.LowLevelSyntaxException;
import org.codingmatters.value.objects.exception.SpecSyntaxException;
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
import static org.codingmatters.tests.reflect.ReflectMatchers.aMethod;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 3/30/17.
 */
@Ignore
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
        new JsonFrameworkGenerator(this.spec, "org.generated", dir.getRoot()).generate();
    }

    @Test
    public void writerMethods() throws Exception {
        assertThat(
                this.compiled.getClass("org.generated.json.ExampleValueJsonWriter"),
                is(aClass()
                        .with(aMethod().named("write").withoutParameters().returning(String.class))
                )
        );
    }

    @Test
    public void writeSimpleProperties() throws Exception {
        ExampleValue value = ExampleValue.Builder.builder()
                .prop("a value")
                .listProp("a", "b", "c")
                .build();

        Object writer = this.compiled.getClass("org.generated.json.ExampleValueWriter").newInstance();
        String json = this.compiled.on(writer).invoke("writer", ExampleValue.class).with(value);
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
