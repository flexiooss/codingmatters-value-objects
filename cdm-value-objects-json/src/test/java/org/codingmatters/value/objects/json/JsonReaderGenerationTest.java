package org.codingmatters.value.objects.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.FileHelper;
import org.codingmatters.tests.compile.helpers.ClassLoaderHelper;
import org.codingmatters.tests.compile.helpers.helpers.ObjectHelper;
import org.codingmatters.tests.reflect.ReflectMatchers;
import org.codingmatters.value.objects.exception.LowLevelSyntaxException;
import org.codingmatters.value.objects.exception.SpecSyntaxException;
import org.codingmatters.value.objects.generation.SpecCodeGenerator;
import org.codingmatters.value.objects.reader.SpecReader;
import org.codingmatters.value.objects.spec.Spec;
import org.generated.*;
import org.generated.examplevalue.Complex;
import org.generated.examplevalue.ComplexList;
import org.generated.ref.ExtReferenced;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.time.*;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import static org.codingmatters.tests.reflect.ReflectMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 4/6/17.
 */
public class JsonReaderGenerationTest {
    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    @Rule
    public FileHelper fileHelper = new FileHelper();

    private Spec spec;

    private final JsonFactory factory = new JsonFactory();

    static private Spec loadSpec(String resource) {
        try {
            return new SpecReader().read(Thread.currentThread().getContextClassLoader().getResourceAsStream(resource));
        } catch (IOException | SpecSyntaxException | LowLevelSyntaxException e) {
            throw new RuntimeException("error loading spec", e);
        }
    }

    private CompiledCode compiled;
    private ClassLoaderHelper classes;

    @Before
    public void setUp() throws Exception {Spec refSpec = loadSpec("ref.yaml");
        new SpecCodeGenerator(refSpec, "org.generated.ref", dir.getRoot()).generate();
        new JsonFrameworkGenerator(refSpec, "org.generated.ref", dir.getRoot()).generate();

        this.spec = loadSpec("spec.yaml");

        new SpecCodeGenerator(this.spec, "org.generated", dir.getRoot()).generate();
        new JsonFrameworkGenerator(this.spec, "org.generated", dir.getRoot()).generate();
        this.compiled = new CompiledCode.Builder()
                .classpath(CompiledCode.findLibraryInClasspath("jackson-core"))
                .source(this.dir.getRoot())
                .compile();
        this.classes = this.compiled.classLoader();
    }

    @Test
    public void readerSignature() throws Exception {
        this.fileHelper.printJavaContent("", this.dir.getRoot());
        this.fileHelper.printFile(this.dir.getRoot(), "ExampleValueReader.java");
        assertThat(
                this.classes.get("org.generated.json.ExampleValueReader$Reader").get(),
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
                this.classes.get("org.generated.json.ExampleValueReader").get(),
                is(aClass()
                        .with(aPublic().method().named("read")
                                .withParameters(JsonParser.class)
                                .returning(ExampleValue.class)
                                .throwing(IOException.class)
                        )
                        .with(aPublic().method().named("readArray")
                                .withParameters(JsonParser.class)
                                .returning(ReflectMatchers.typeArray(classType(ExampleValue.class)))
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
        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ObjectHelper reader = this.classes.get("org.generated.json.ExampleValueReader").newInstance();
            ObjectHelper value = reader.call("read", JsonParser.class).with(parser);

            assertThat(
                    value.get(),
                    is(ExampleValue.builder()
                            .prop("a value")
                            .build())
            );
        }
    }


    @Test
    public void readStringArrayProperty() throws Exception {
        String json = "{" +
                "\"prop\":null," +
                "\"listProp\":[\"a\",\"b\",\"c\"]," +
                "\"complex\":null," +
                "\"complexList\":null" +
                "}";
        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ObjectHelper reader = this.classes.get("org.generated.json.ExampleValueReader").newInstance();
            ObjectHelper value = reader.call("read", JsonParser.class).with(parser);

            assertThat(
                    value.get(),
                    is(ExampleValue.builder()
                            .listProp("a", "b", "c")
                            .build())
            );
        }
    }


    @Test
    public void readComplexProperty() throws Exception {
        String json = "{" +
                "\"prop\":null," +
                "\"listProp\":null," +
                "\"complex\":{\"sub\":\"a value\"}," +
                "\"complexList\":null" +
                "}";
        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ObjectHelper reader = this.classes.get("org.generated.json.ExampleValueReader").newInstance();
            ObjectHelper value = reader.call("read", JsonParser.class).with(parser);

            assertThat(
                    value.get(),
                    is(ExampleValue.builder()
                            .complex(new Complex.Builder()
                                    .sub("a value")
                                    .build())
                            .build())
            );
        }
    }


    @Test
    public void readComplexListProperty() throws Exception {
        String json = "{" +
                "\"prop\":null," +
                "\"listProp\":null," +
                "\"complex\":null," +
                "\"complexList\":[{\"sub\":\"a value\"}]" +
                "}";
        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ObjectHelper reader = this.classes.get("org.generated.json.ExampleValueReader").newInstance();
            ObjectHelper value = reader.call("read", JsonParser.class).with(parser);

            assertThat(
                    value.get(),
                    is(ExampleValue.builder().complexList(new ComplexList.Builder()
                            .sub("a value")
                            .build()
                            ).build()
                    )
            );
        }
    }


    @Test
    public void readComplexEmptyListProperty() throws Exception {
        String json = "{" +
                "\"prop\":null," +
                "\"listProp\":null," +
                "\"complex\":null," +
                "\"complexList\":[]" +
                "}";
        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ObjectHelper reader = this.classes.get("org.generated.json.ExampleValueReader").newInstance();
            ObjectHelper value = reader.call("read", JsonParser.class).with(parser);

            assertThat(
                    value.get(),
                    is(ExampleValue.builder()
                            .complexList(new ComplexList[0])
                            .build())
            );
        }
    }

    @Test
    public void readArrayComplexList() throws Exception {
        String json = "[{\"sub\":\"a value\"}]";
        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ComplexList[] value = (ComplexList[]) this.classes.get("org.generated.examplevalue.json.ComplexListReader").newInstance()
                    .call("readArray", JsonParser.class).with(parser)
                    .get();

            assertThat(
                    value,
                    is(arrayContaining(new ComplexList.Builder()
                            .sub("a value")
                            .build()
                    ))
            );
        }
    }

    @Test
    public void readArrayEmptyComplexList() throws Exception {
        String json = "[]";
        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ComplexList[] value = (ComplexList[]) this.classes.get("org.generated.examplevalue.json.ComplexListReader").newInstance()
                    .call("readArray", JsonParser.class).with(parser)
                    .get();

            assertThat(
                    value,
                    is(new ComplexList[0])
            );
        }
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

        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ObjectHelper reader = this.classes.get("org.generated.json.SimplePropsReader").newInstance();
            ObjectHelper value = reader.call("read", JsonParser.class).with(parser);

            assertThat(
                    value.get(),
                    is(new SimpleProps.Builder()
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
                            .build()
                    )
            );
        }
    }

    @Test
    public void readBooleanTrue() throws Exception {
        String json = "{" +
                "\"booleanProp\":true" +
                "}";
        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ObjectHelper reader = this.classes.get("org.generated.json.SimplePropsReader").newInstance();
            ObjectHelper value = reader.call("read", JsonParser.class).with(parser);

            assertThat(
                    value.get(),
                    is(new SimpleProps.Builder()
                            .booleanProp(true)
                            .build()
                    )
            );
        }
    }

    @Test
    public void readBooleanFalse() throws Exception {
        String json = "{" +
                "\"booleanProp\":false" +
                "}";
        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ObjectHelper reader = this.classes.get("org.generated.json.SimplePropsReader").newInstance();
            ObjectHelper value = reader.call("read", JsonParser.class).with(parser);

            assertThat(
                    value.get(),
                    is(new SimpleProps.Builder()
                            .booleanProp(false)
                            .build()
                    )
            );
        }
    }

    @Test
    public void readOutsideSpecEnumValue() throws Exception {
        String json = "{" +
                "\"single\":\"MONDAY\"," +
                "\"multiple\":[\"MONDAY\",\"TUESDAY\"]" +
                "}";
        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ObjectHelper reader = this.classes.get("org.generated.json.EnumPropertiesReader").newInstance();
            ObjectHelper value = reader.call("read", JsonParser.class).with(parser);

            assertThat(
                    value.get(),
                    is(new EnumProperties.Builder()
                            .single(DayOfWeek.MONDAY)
                            .multiple(DayOfWeek.MONDAY, DayOfWeek.TUESDAY)
                            .build()
                    )
            );
        }
    }


    @Test
    public void readInSpecEnumValue() throws Exception {
        String json = "{" +
                "\"single\":\"A\"," +
                "\"multiple\":[\"A\",\"B\"]" +
                "}";
        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ObjectHelper reader = this.classes.get("org.generated.json.InSpecEnumPropertiesReader").newInstance();
            ObjectHelper value = reader.call("read", JsonParser.class).with(parser);

            assertThat(
                    value.get(),
                    is(new InSpecEnumProperties.Builder()
                            .single(InSpecEnumProperties.Single.A)
                            .multiple(InSpecEnumProperties.Multiple.A, InSpecEnumProperties.Multiple.B)
                            .build()
                    )
            );
        }
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
                "\"dateProp\":[\"2011-12-03\"]," +
                "\"timeProp\":[\"10:15:30\"]," +
                "\"dateTimeProp\":[\"2011-12-03T10:15:30\"]," +
                "\"tzDateTimeProp\":[\"2011-12-03T10:15:30+01:00\"]" +
                "}";
        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ObjectHelper reader = this.classes.get("org.generated.json.ArraySimplePropsReader").newInstance();
            ObjectHelper value = reader.call("read", JsonParser.class).with(parser);

            assertThat(
                    value.get(),
                    is(
                            new ArraySimpleProps.Builder()
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
                                    .build()
                    )
            );
        }
    }

    @Test
    public void readSimpleTypeArraysWithNullElements() throws Exception {
        String json = "{" +
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
                "}";
        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ObjectHelper reader = this.classes.get("org.generated.json.ArraySimplePropsReader").newInstance();
            ObjectHelper value = reader.call("read", JsonParser.class).with(parser);

            assertThat(
                    value.get(),
                    is(
                            new ArraySimpleProps.Builder()
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
                                    .build()
                    )
            );
        }
    }

    @Test
    public void readReferencedValue() throws Exception {
        String json = "{" +
                "\"ref\":{\"prop\":\"value\"}," +
                "\"refs\":[{\"prop\":\"value\"}]" +
                "}";
        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ObjectHelper reader = this.classes.get("org.generated.json.RefValueReader").newInstance();
            ObjectHelper value = reader.call("read", JsonParser.class).with(parser);
            assertThat(
                    value.get(),
                    is(
                            new RefValue.Builder()
                                    .ref(new Referenced.Builder()
                                            .prop("value")
                                            .build())
                                    .refs(new Referenced.Builder()
                                            .prop("value")
                                            .build())
                                    .build()
                    )
            );
        }
    }

    @Test
    public void readNullReferencedValue() throws Exception {
        String json = "{" +
                "\"ref\":null," +
                "\"refs\":[null]" +
                "}";
        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ObjectHelper reader = this.classes.get("org.generated.json.RefValueReader").newInstance();
            ObjectHelper value = reader.call("read", JsonParser.class).with(parser);
            assertThat(
                    value.get(),
                    is(
                            new RefValue.Builder()
                                    .ref((Referenced) null)
                                    .refs((Referenced) null)
                                    .build()
                    )
            );
        }
    }

    @Test
    public void readExternalReferenceValue() throws Exception {
        String json = "{\"prop\":{\"prop\":\"val\"}}";
        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ObjectHelper reader = this.classes.get("org.generated.json.ValueObjectPropsReader").newInstance();
            ObjectHelper value = reader.call("read", JsonParser.class).with(parser);

            assertThat(
                    value.get(),
                    is(
                            ValueObjectProps.builder()
                                    .prop(ExtReferenced.builder()
                                            .prop("val")
                                            .build())
                                    .build()
                    )
            );
        }
    }

    @Test
    public void readBinary() throws Exception {
        String json = String.format("{\"prop\":\"%s\"}", new String(Base64.getEncoder().encode("binary".getBytes())));

        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ObjectHelper reader = this.classes.get("org.generated.json.BinaryReader").newInstance();
            ObjectHelper value = reader.call("read", JsonParser.class).with(parser);

            assertThat(value.as("org.generated.Binary").call("prop").get(), is("binary".getBytes()));

            assertThat(
                    value.get(),
                    is(
                            Binary.builder()
                                    .prop("binary".getBytes())
                                    .build()
                    )
            );
        }
    }

    @Test
    public void readArray() throws Exception {
        String json = "[{" +
                "\"prop\":\"a value\"" +
                "}, {" +
                "\"prop\":\"another value\"" +
                "}]";
        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ObjectHelper reader = this.classes.get("org.generated.json.ExampleValueReader").newInstance();
            ObjectHelper value = reader.call("readArray", JsonParser.class).with(parser);

            assertThat(
                    value.asArray().get(),
                    arrayContaining(
                            ExampleValue.builder().prop("a value").build(),
                            ExampleValue.builder().prop("another value").build()
                    )
            );
        }
    }

    @Test
    public void readEmptyArray() throws Exception {
        String json = "[]";
        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ObjectHelper reader = this.classes.get("org.generated.json.ExampleValueReader").newInstance();
            ObjectHelper value = reader.call("readArray", JsonParser.class).with(parser);

            assertThat(
                    value.asArray().get(),
                    emptyArray()
            );
        }
    }

    @Test
    public void readNullArray() throws Exception {
        String json = "null";
        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ObjectHelper reader = this.classes.get("org.generated.json.ExampleValueReader").newInstance();
            ObjectHelper value = reader.call("readArray", JsonParser.class).with(parser);

            assertThat(
                    value.get(),
                    is(nullValue())
            );
        }
    }


    @Test
    public void readArrayWithUnexpectedSimpleProperty() throws Exception {
        this.fileHelper.printFile(this.dir.getRoot(), "ExampleValueReader.java");
        String json = "[{" +
                "\"prop\":\"a value\", \"unexpected\":\"property\"" +
                "}, {" +
                "\"prop\":\"another value\"" +
                "}]";
        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ObjectHelper reader = this.classes.get("org.generated.json.ExampleValueReader").newInstance();
            ObjectHelper value = reader.call("readArray", JsonParser.class).with(parser);

            assertThat(
                    value.asArray().get(),
                    arrayContaining(
                            ExampleValue.builder().prop("a value").build(),
                            ExampleValue.builder().prop("another value").build()
                    )
            );
        }
    }

    @Test
    public void readArrayWithUnexpectedEmptyArrayProperty() throws Exception {
        String json = "[{" +
                "\"prop\":\"a value\", \"unexpected\":[]" +
                "}, {" +
                "\"prop\":\"another value\"" +
                "}]";
        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ObjectHelper reader = this.classes.get("org.generated.json.ExampleValueReader").newInstance();
            ObjectHelper value = reader.call("readArray", JsonParser.class).with(parser);

            assertThat(
                    value.asArray().get(),
                    arrayContaining(
                            ExampleValue.builder().prop("a value").build(),
                            ExampleValue.builder().prop("another value").build()
                    )
            );
        }
    }

    @Test
    public void readArrayWithUnexpectedArrayProperty() throws Exception {
        String json = "[{" +
                "\"prop\":\"a value\", \"unexpected\":[\"array\", \"property\"]" +
                "}, {" +
                "\"prop\":\"another value\"" +
                "}]";
        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ObjectHelper reader = this.classes.get("org.generated.json.ExampleValueReader").newInstance();
            ObjectHelper value = reader.call("readArray", JsonParser.class).with(parser);

            assertThat(
                    value.asArray().get(),
                    arrayContaining(
                            ExampleValue.builder().prop("a value").build(),
                            ExampleValue.builder().prop("another value").build()
                    )
            );
        }
    }

    @Test
    public void readArrayWithUnexpectedEmptyObjectProperty() throws Exception {
        String json = "[{" +
                "\"prop\":\"a value\", \"unexpected\":{}" +
                "}, {" +
                "\"prop\":\"another value\"" +
                "}]";
        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ObjectHelper reader = this.classes.get("org.generated.json.ExampleValueReader").newInstance();
            ObjectHelper value = reader.call("readArray", JsonParser.class).with(parser);

            assertThat(
                    value.asArray().get(),
                    arrayContaining(
                            ExampleValue.builder().prop("a value").build(),
                            ExampleValue.builder().prop("another value").build()
                    )
            );
        }
    }

    @Test
    public void readArrayWithUnexpectedObjectProperty() throws Exception {
        String json = "[{" +
                "\"prop\":\"a value\", \"unexpected\":{\"object\": \"property\"}" +
                "}, {" +
                "\"prop\":\"another value\"" +
                "}]";
        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ObjectHelper reader = this.classes.get("org.generated.json.ExampleValueReader").newInstance();
            ObjectHelper value = reader.call("readArray", JsonParser.class).with(parser);

            assertThat(
                    value.asArray().get(),
                    arrayContaining(
                            ExampleValue.builder().prop("a value").build(),
                            ExampleValue.builder().prop("another value").build()
                    )
            );
        }
    }

    @Test
    public void readArrayWithUnexpectedDeepObjectProperty() throws Exception {
        String json = "[{" +
                "\"prop\":\"a value\", \"unexpected\":{\"object\": \"property\", \"nested\": {\"deep\": {\"object\": \"value\"}}}" +
                "}, {" +
                "\"prop\":\"another value\"" +
                "}]";
        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ObjectHelper reader = this.classes.get("org.generated.json.ExampleValueReader").newInstance();
            ObjectHelper value = reader.call("readArray", JsonParser.class).with(parser);

            assertThat(
                    value.asArray().get(),
                    arrayContaining(
                            ExampleValue.builder().prop("a value").build(),
                            ExampleValue.builder().prop("another value").build()
                    )
            );
        }
    }


    @Test
    public void readUnormalizedProperties() throws Exception {
        String[] jsons = new String[] {
                "{\"StringProp\":\"str\"}",
                "{\"string prop\":\"str\"}",
                "{\"string-prop\":\"str\"}"
        };

        for (String json : jsons) {
            try(JsonParser parser = this.factory.createParser(json.getBytes())) {
                ObjectHelper reader = this.classes.get("org.generated.json.SimplePropsReader").newInstance();
                ObjectHelper value = reader.call("read", JsonParser.class).with(parser);
                assertThat(
                        json,
                        value.get(),
                        is(new SimpleProps.Builder()
                                .stringProp("str")

                                .build()
                        )
                );
            }
        }
    }

    @Test
    public void rawPropertyNameHint() throws Exception {
        String json = "{\"Raw Property Name\":\"value\"}";

        try(JsonParser parser = this.factory.createParser(json.getBytes())) {
            ObjectHelper reader = this.classes.get("org.generated.json.HintsReader").newInstance();
            ObjectHelper value = reader.call("read", JsonParser.class).with(parser);
            assertThat(
                    json,
                    value.get(),
                    is(Hints.builder()
                            .propName("value")
                            .build()
                    )
            );
        }
    }

}
