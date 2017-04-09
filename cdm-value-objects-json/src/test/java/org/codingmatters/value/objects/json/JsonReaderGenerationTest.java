package org.codingmatters.value.objects.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.value.objects.exception.LowLevelSyntaxException;
import org.codingmatters.value.objects.exception.SpecSyntaxException;
import org.codingmatters.value.objects.generation.SpecCodeGenerator;
import org.codingmatters.value.objects.reader.SpecReader;
import org.codingmatters.value.objects.spec.Spec;
import org.generated.ExampleValue;
import org.generated.examplevalue.Complex;
import org.generated.examplevalue.ComplexList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.List;

import static org.codingmatters.tests.reflect.ReflectMatchers.*;
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
                this.compiled.getClass("org.generated.json.ExampleValueReader$Reader"),
                is(aPrivate().static_().interface_()
                        .withParameter(variableType().named("T"))
                        .with(aPublic().method().named("read")
                                .withParameters(JsonParser.class)
                                .returning(variableType().named("T"))
                                .throwing(IOException.class)
                        )
                )
        );
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
                        .with(aPrivate().method().named("readValue")
                                .withVariable(variableType().named("T"))
                                .withParameters(
                                        classType(JsonParser.class),
                                        classType(JsonToken.class),
                                        genericType().named("org.generated.json.ExampleValueReader$Reader").withParameters(typeParameter().named("T")),
                                        classType(String.class)
                                )
                                .throwing(IOException.class)
                                .returning(variableType().named("T"))
                        )
                        .with(aPrivate().method().named("readListValue")
                                .withVariable(variableType().named("T"))
                                .withParameters(
                                        classType(JsonParser.class),
                                        genericType().named("org.generated.json.ExampleValueReader$Reader").withParameters(typeParameter().named("T")),
                                        classType(String.class)
                                )
                                .throwing(IOException.class)
                                .returning(genericType().baseClass(List.class).withParameters(typeParameter().named("T")))
                        )
                )
        );
    }

    @Test
    public void readStringProperty() throws Exception {
        String json = "{" +
                "\"prop\":\"a value\"," +
                "\"listProp\":null," +
                "\"complex\":null," +
                "\"complexList\":null" +
                "}";

        Object reader = this.compiled.getClass("org.generated.json.ExampleValueReader").newInstance();
        ExampleValue value = this.compiled.on(reader).invoke("read", String.class).with(json);

        assertThat(
                value,
                is(ExampleValue.Builder.builder()
                        .prop("a value")
                        .build())
        );
    }


    @Test
    public void readStringArrayProperty() throws Exception {
        String json = "{" +
                "\"prop\":null," +
                "\"listProp\":[\"a\",\"b\",\"c\"]," +
                "\"complex\":null," +
                "\"complexList\":null" +
                "}";
        Object reader = this.compiled.getClass("org.generated.json.ExampleValueReader").newInstance();
        ExampleValue value = this.compiled.on(reader).invoke("read", String.class).with(json);

        assertThat(
                value,
                is(ExampleValue.Builder.builder()
                        .listProp("a", "b", "c")
                        .build())
        );
    }


    @Test
    public void readComplexProperty() throws Exception {
        String json = "{" +
                "\"prop\":null," +
                "\"listProp\":null," +
                "\"complex\":{\"sub\":\"a value\"}," +
                "\"complexList\":null" +
                "}";
        Object reader = this.compiled.getClass("org.generated.json.ExampleValueReader").newInstance();
        ExampleValue value = this.compiled.on(reader).invoke("read", String.class).with(json);

        assertThat(
                value,
                is(ExampleValue.Builder.builder()
                        .complex(new Complex.Builder()
                                .sub("a value")
                                .build())
                        .build())
        );
    }


    @Test
    public void readComplexListProperty() throws Exception {
        String json = "{" +
                "\"prop\":null," +
                "\"listProp\":null," +
                "\"complex\":null," +
                "\"complexList\":[{\"sub\":\"a value\"}]" +
                "}";
        Object reader = this.compiled.getClass("org.generated.json.ExampleValueReader").newInstance();
        ExampleValue value = this.compiled.on(reader).invoke("read", String.class).with(json);

        assertThat(
                value,
                is(ExampleValue.Builder.builder()
                        .complexList(new ComplexList.Builder()
                                .sub("a value")
                                .build())
                        .build())
        );
    }
}
