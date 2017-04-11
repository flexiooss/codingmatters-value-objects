package org.codingmatters.value.objects.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.value.objects.exception.LowLevelSyntaxException;
import org.codingmatters.value.objects.exception.SpecSyntaxException;
import org.codingmatters.value.objects.generation.SpecCodeGenerator;
import org.codingmatters.value.objects.reader.SpecReader;
import org.codingmatters.value.objects.spec.Spec;
import org.generated.ArraySimpleProps;
import org.generated.ExampleValue;
import org.generated.SimpleProps;
import org.generated.examplevalue.Complex;
import org.generated.examplevalue.ComplexList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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
                                        genericType().named("org.generated.json.ExampleValueReader$Reader").withParameters(typeParameter().named("T")),
                                        classType(String.class),
                                        genericType().baseClass(Set.class).withParameters(typeParameter().aClass(JsonToken.class))
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



    @Test
    public void readSimpleTypes() throws Exception {
        String json = "{" +
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
                "}";

        Object reader = this.compiled.getClass("org.generated.json.SimplePropsReader").newInstance();
        SimpleProps value = this.compiled.on(reader).invoke("read", String.class).with(json);

        assertThat(
                value,
                is(new SimpleProps.Builder()
                    .stringProp("str")
                    .integerProp(12)
                    .longProp(12L)
                    .floatProp(12.12f)
                    .doubleProp(12.12d)
                    .booleanProp(true)
                    .dateProp(LocalDate.parse("2011-12-03"))
//                    .timeProp(LocalTime.parse("10:15:30"))
//                    .dateTimeProp(LocalDateTime.parse("2011-12-03T10:15:30"))
//                    .tzDateTimeProp(ZonedDateTime.parse("2011-12-03T10:15:30+01:00"))
                    .build()
                )
        );
    }

    @Test
    public void readBooleanTrue() throws Exception {
        String json = "{" +
                "\"booleanProp\":true" +
                "}";

        Object reader = this.compiled.getClass("org.generated.json.SimplePropsReader").newInstance();
        SimpleProps value = this.compiled.on(reader).invoke("read", String.class).with(json);

        assertThat(
                value,
                is(new SimpleProps.Builder()
                                .booleanProp(true)
                                .build()
                )
        );
    }

    @Test
    public void readBooleanFalse() throws Exception {
        String json = "{" +
                "\"booleanProp\":false" +
                "}";

        Object reader = this.compiled.getClass("org.generated.json.SimplePropsReader").newInstance();
        SimpleProps value = this.compiled.on(reader).invoke("read", String.class).with(json);

        assertThat(
                value,
                is(new SimpleProps.Builder()
                                .booleanProp(false)
                                .build()
                )
        );
    }

    @Test
    public void readSimpleTypeArrays() throws Exception {
        String json = "{" +
                "\"stringProp\":[\"str\"]," +
                "\"integerProp\":[12]," +
                "\"longProp\":[12]," +
                "\"floatProp\":[12.12]," +
                "\"doubleProp\":[12.12]," +
                "\"booleanProp\":[true]," +
                "\"dateProp\":[\"2011-12-03\"]" +
//                "\"timeProp\":[\"10:15:30\"]," +
//                "\"dateTimeProp\":[\"2011-12-03T10:15:30\"]," +
//                "\"tzDateTimeProp\":[\"2011-12-03T10:15:30+01:00\"]" +
                "}";

        Object reader = this.compiled.getClass("org.generated.json.ArraySimplePropsReader").newInstance();
        ArraySimpleProps value = this.compiled.on(reader).invoke("read", String.class).with(json);

        assertThat(
                value,
                is(
                        new ArraySimpleProps.Builder()
                                .stringProp("str")
                                .integerProp(12)
                                .longProp(12L)
                                .floatProp(12.12f)
                                .doubleProp(12.12d)
                                .booleanProp(true)
                                .dateProp(LocalDate.parse("2011-12-03"))
//                                .timeProp(LocalTime.parse("10:15:30"))
//                                .dateTimeProp(LocalDateTime.parse("2011-12-03T10:15:30"))
//                                .tzDateTimeProp(ZonedDateTime.parse("2011-12-03T10:15:30+01:00"))
                                .build()
                )
        );
    }




//    @Test
//    public void writeSimpleTypeArraysWithNullElements() throws Exception {
//        ArraySimpleProps value = new ArraySimpleProps.Builder()
//                .stringProp((String) null)
//                .integerProp((Integer) null)
//                .longProp((Long) null)
//                .floatProp((Float) null)
//                .doubleProp((Double) null)
//                .booleanProp((Boolean) null)
//                .dateProp((LocalDate) null)
//                .timeProp((LocalTime) null)
//                .dateTimeProp((LocalDateTime) null)
//                .tzDateTimeProp((ZonedDateTime) null)
//                .build();
//        Object writer = this.compiled.getClass("org.generated.json.ArraySimplePropsWriter").newInstance();
//        String json = this.compiled.on(writer).invoke("write", ArraySimpleProps.class).with(value);
//
//        assertThat(
//                json,
//                is("{" +
//                        "\"stringProp\":[null]," +
//                        "\"integerProp\":[null]," +
//                        "\"longProp\":[null]," +
//                        "\"floatProp\":[null]," +
//                        "\"doubleProp\":[null]," +
//                        "\"booleanProp\":[null]," +
//                        "\"dateProp\":[null]," +
//                        "\"timeProp\":[null]," +
//                        "\"dateTimeProp\":[null]," +
//                        "\"tzDateTimeProp\":[null]" +
//                        "}")
//        );
//    }
//
//    @Test
//    public void writeReferencedValue() throws Exception {
//        RefValue value = new RefValue.Builder()
//                .ref(new Referenced.Builder()
//                        .prop("value")
//                        .build())
//                .refs(new Referenced.Builder()
//                        .prop("value")
//                        .build())
//                .build();
//
//        Object writer = this.compiled.getClass("org.generated.json.RefValueWriter").newInstance();
//        String json = this.compiled.on(writer).invoke("write", RefValue.class).with(value);
//
//        assertThat(
//                json,
//                is("{" +
//                        "\"ref\":{\"prop\":\"value\"}," +
//                        "\"refs\":[{\"prop\":\"value\"}]}"
//                )
//        );
//    }
//
//    @Test
//    public void writeNullReferencedValue() throws Exception {
//        RefValue value = new RefValue.Builder()
//                .ref(null)
//                .refs((Referenced) null)
//                .build();
//
//        Object writer = this.compiled.getClass("org.generated.json.RefValueWriter").newInstance();
//        String json = this.compiled.on(writer).invoke("write", RefValue.class).with(value);
//
//        assertThat(
//                json,
//                is("{" +
//                        "\"ref\":null," +
//                        "\"refs\":[null]}"
//                )
//        );
//    }
}
