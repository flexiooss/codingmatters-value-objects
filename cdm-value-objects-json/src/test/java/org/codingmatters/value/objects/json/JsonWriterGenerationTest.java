package org.codingmatters.value.objects.json;

import com.fasterxml.jackson.core.JsonGenerator;
import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.value.objects.exception.LowLevelSyntaxException;
import org.codingmatters.value.objects.exception.SpecSyntaxException;
import org.codingmatters.value.objects.generation.SpecCodeGenerator;
import org.codingmatters.value.objects.reader.SpecReader;
import org.codingmatters.value.objects.spec.Spec;
import org.generated.*;
import org.generated.examplevalue.Complex;
import org.generated.examplevalue.ComplexList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

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
    public void writerMethods() throws Exception {
        assertThat(
                this.compiled.getClass("org.generated.json.ExampleValueWriter"),
                is(aClass()
                        .with(aPublic().method().named("write")
                                .withParameters(ExampleValue.class)
                                .returning(String.class)
                                .throwing(IOException.class)
                        )
                        // public void write(JsonGenerator generator, ExampleValue value) throws IOException
                        .with(aPublic().method().named("write")
                                .withParameters(JsonGenerator.class, ExampleValue.class)
                                .returningVoid()
                                .throwing(IOException.class)
                        )
                )
        );
    }

    @Test
    public void writeStringProperty() throws Exception {
        ExampleValue value = ExampleValue.Builder.builder()
                .prop("a value")
                .build();

        Object writer = this.compiled.getClass("org.generated.json.ExampleValueWriter").newInstance();
        String json = this.compiled.on(writer).invoke("write", ExampleValue.class).with(value);
        assertThat(
                json,
                is("{" +
                        "\"prop\":\"a value\"," +
                        "\"listProp\":null," +
                        "\"complex\":null," +
                        "\"complexList\":null" +
                        "}")
        );
    }

    @Test
    public void writeStringArrayProperty() throws Exception {
        ExampleValue value = ExampleValue.Builder.builder()
                .listProp("a", "b", "c")
                .build();

        Object writer = this.compiled.getClass("org.generated.json.ExampleValueWriter").newInstance();
        String json = this.compiled.on(writer).invoke("write", ExampleValue.class).with(value);
        assertThat(
                json,
                is("{" +
                        "\"prop\":null," +
                        "\"listProp\":[\"a\",\"b\",\"c\"]," +
                        "\"complex\":null," +
                        "\"complexList\":null" +
                        "}")
        );
    }


    @Test
    public void writeComplexProperty() throws Exception {
        ExampleValue value = ExampleValue.Builder.builder()
                .complex(new Complex.Builder()
                        .sub("a value")
                        .build())
                .build();

        Object writer = this.compiled.getClass("org.generated.json.ExampleValueWriter").newInstance();
        String json = this.compiled.on(writer).invoke("write", ExampleValue.class).with(value);
        assertThat(
                json,
                is("{" +
                        "\"prop\":null," +
                        "\"listProp\":null," +
                        "\"complex\":{\"sub\":\"a value\"}," +
                        "\"complexList\":null" +
                        "}")
        );
    }

    @Test
    public void writeComplexListProperty() throws Exception {
        ExampleValue value = ExampleValue.Builder.builder()
                .complexList(new ComplexList.Builder()
                        .sub("a value")
                        .build())
                .build();

        Object writer = this.compiled.getClass("org.generated.json.ExampleValueWriter").newInstance();
        String json = this.compiled.on(writer).invoke("write", ExampleValue.class).with(value);
        assertThat(
                json,
                is("{" +
                        "\"prop\":null," +
                        "\"listProp\":null," +
                        "\"complex\":null," +
                        "\"complexList\":[{\"sub\":\"a value\"}]" +
                        "}")
        );
    }

    @Test
    public void writeSimpleTypes() throws Exception {
        SimpleProps value = new SimpleProps.Builder()
                .stringProp("str")
                .integerProp(12)
                .longProp(12L)
                .floatProp(12.12f)
                .doubleProp(12.12d)
                .booleanProp(true)
                .dateProp(LocalDate.parse("2011-12-03"))
                .timeProp(LocalTime.parse("10:15:30"))
                .dateTimeProp(LocalDateTime.parse("2011-12-03T10:15:30"))
                .tzDateTimeProp(ZonedDateTime.parse("2011-12-03T10:15:30+01:00"))
                .build();
        Object writer = this.compiled.getClass("org.generated.json.SimplePropsWriter").newInstance();
        String json = this.compiled.on(writer).invoke("write", SimpleProps.class).with(value);

        assertThat(
                json,
                is("{" +
                        "\"stringProp\":\"str\"," +
                        "\"integerProp\":12," +
                        "\"longProp\":12," +
                        "\"floatProp\":12.12," +
                        "\"doubleProp\":12.12," +
                        "\"booleanProp\":true," +
                        "\"dateProp\":\"2011-12-03\"," +
                        "\"timeProp\":\"10:15:30\"," +
                        "\"dateTimeProp\":\"2011-12-03T10:15:30\"," +
                        "\"tzDateTimeProp\":\"2011-12-03T10:15:30+01:00\"" +
                        "}")
        );
    }

    @Test
    public void writeSimpleTypeArrays() throws Exception {
        ArraySimpleProps value = new ArraySimpleProps.Builder()
                .stringProp("str")
                .integerProp(12)
                .longProp(12L)
                .floatProp(12.12f)
                .doubleProp(12.12d)
                .booleanProp(true)
                .dateProp(LocalDate.parse("2011-12-03"))
                .timeProp(LocalTime.parse("10:15:30"))
                .dateTimeProp(LocalDateTime.parse("2011-12-03T10:15:30"))
                .tzDateTimeProp(ZonedDateTime.parse("2011-12-03T10:15:30+01:00"))
                .build();
        Object writer = this.compiled.getClass("org.generated.json.ArraySimplePropsWriter").newInstance();
        String json = this.compiled.on(writer).invoke("write", ArraySimpleProps.class).with(value);

        assertThat(
                json,
                is("{" +
                        "\"stringProp\":[\"str\"]," +
                        "\"integerProp\":[12]," +
                        "\"longProp\":[12]," +
                        "\"floatProp\":[12.12]," +
                        "\"doubleProp\":[12.12]," +
                        "\"booleanProp\":[true]," +
                        "\"dateProp\":[\"2011-12-03\"]," +
                        "\"timeProp\":[\"10:15:30\"]," +
                        "\"dateTimeProp\":[\"2011-12-03T10:15:30\"]," +
                        "\"tzDateTimeProp\":[\"2011-12-03T10:15:30+01:00\"]" +
                        "}")
        );
    }

    @Test
    public void writeSimpleTypeArraysWithNullElements() throws Exception {
        ArraySimpleProps value = new ArraySimpleProps.Builder()
                .stringProp((String) null)
                .integerProp((Integer) null)
                .longProp((Long) null)
                .floatProp((Float) null)
                .doubleProp((Double) null)
                .booleanProp((Boolean) null)
                .dateProp((LocalDate) null)
                .timeProp((LocalTime) null)
                .dateTimeProp((LocalDateTime) null)
                .tzDateTimeProp((ZonedDateTime) null)
                .build();
        Object writer = this.compiled.getClass("org.generated.json.ArraySimplePropsWriter").newInstance();
        String json = this.compiled.on(writer).invoke("write", ArraySimpleProps.class).with(value);

        assertThat(
                json,
                is("{" +
                        "\"stringProp\":[null]," +
                        "\"integerProp\":[null]," +
                        "\"longProp\":[null]," +
                        "\"floatProp\":[null]," +
                        "\"doubleProp\":[null]," +
                        "\"booleanProp\":[null]," +
                        "\"dateProp\":[null]," +
                        "\"timeProp\":[null]," +
                        "\"dateTimeProp\":[null]," +
                        "\"tzDateTimeProp\":[null]" +
                        "}")
        );
    }

    @Test
    public void writeReferencedValue() throws Exception {
        RefValue value = new RefValue.Builder()
                .ref(new Referenced.Builder()
                        .prop("value")
                        .build())
                .refs(new Referenced.Builder()
                        .prop("value")
                        .build())
                .build();

        Object writer = this.compiled.getClass("org.generated.json.RefValueWriter").newInstance();
        String json = this.compiled.on(writer).invoke("write", RefValue.class).with(value);

        assertThat(
                json,
                is("{" +
                        "\"ref\":{\"prop\":\"value\"}," +
                        "\"refs\":[{\"prop\":\"value\"}]}"
                )
        );
    }

    @Test
    public void writeNullReferencedValue() throws Exception {
        RefValue value = new RefValue.Builder()
                .ref(null)
                .refs((Referenced) null)
                .build();

        Object writer = this.compiled.getClass("org.generated.json.RefValueWriter").newInstance();
        String json = this.compiled.on(writer).invoke("write", RefValue.class).with(value);

        assertThat(
                json,
                is("{" +
                        "\"ref\":null," +
                        "\"refs\":[null]}"
                )
        );
    }
}
