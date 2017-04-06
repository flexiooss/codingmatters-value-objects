package org.codingmatters.value.objects.json;

import com.fasterxml.jackson.core.JsonParser;
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
 * Created by nelt on 4/6/17.
 */
public class JsonReaderGenerationTest {
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
//        System.setProperty("spec.code.generator.debug", "true");
        new SpecCodeGenerator(this.spec, "org.generated", dir.getRoot()).generate();
        new JsonFrameworkGenerator(this.spec, "org.generated", dir.getRoot()).generate();
        this.compiled = new CompiledCode.Builder()
                .classpath(CompiledCode.findInClasspath(".*jackson-core-.*.jar"))
                .source(this.dir.getRoot())
                .compile();
//        System.setProperty("spec.code.generator.debug", "false");
    }


    @Test
    public void readerSignature() throws Exception {
        assertThat(
                this.compiled.getClass("org.generated.json.ExampleValueReader"),
                is(aClass()
                        .with(aPublic().method().named("read")
                                .withParameters(String.class)
                                .returning(ExampleValue.class)
                                .throwing(IOException.class)
                        )
                        .with(aPublic().method().named("read")
                                .withParameters(JsonParser.class)
                                .returning(ExampleValue.class)
                                .throwing(IOException.class)
                        )
                )
        );
    }

    @Ignore
    @Test
    public void readStringProperty() throws Exception {
        String json = "{" +
                "\"prop\":\"a value\"," +
                "\"listProp\":null," +
                "\"complex\":null," +
                "\"complexList\":null" +
                "}";

        Object writer = this.compiled.getClass("org.generated.json.ExampleValueReader").newInstance();
        ExampleValue value = this.compiled.on(writer).invoke("read", String.class).with(json);

        assertThat(
                value,
                is(ExampleValue.Builder.builder()
                        .prop("a value")
                        .build())
        );
    }
}
