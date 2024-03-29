package org.codingmatters.value.objects.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.helpers.ClassLoaderHelper;
import org.codingmatters.tests.compile.helpers.helpers.ObjectHelper;
import org.codingmatters.value.objects.exception.LowLevelSyntaxException;
import org.codingmatters.value.objects.exception.SpecSyntaxException;
import org.codingmatters.value.objects.generation.SpecCodeGenerator;
import org.codingmatters.value.objects.reader.SpecReader;
import org.codingmatters.value.objects.spec.Spec;
import org.generated.*;
import org.generated.embedded.Multiple;
import org.generated.embedded.Single;
import org.generated.examplevalue.Complex;
import org.generated.examplevalue.ComplexList;
import org.generated.raw.RootType;
import org.generated.raw.roottype.NestedOne;
import org.generated.raw.roottype.nestedone.NestedTwo;
import org.generated.raw.roottype.nestedone.nestedtwo.NestedThree;
import org.generated.ref.ExtReferenced;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.*;
import java.util.Base64;

import static org.codingmatters.tests.reflect.ReflectMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by nelt on 3/30/17.
 */
public class JsonWriterOmitNullValuesGenerationTest {
    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

//    private Spec spec;

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
    public void setUp() throws Exception {
        Spec spec;

        spec = loadSpec("ref.yaml");
        new SpecCodeGenerator(spec, "org.generated.ref", dir.getRoot()).generate();
        new JsonFrameworkGenerator(spec, "org.generated.ref", dir.getRoot(), ValueWriter.NullStrategy.OMIT).generate();

        spec = loadSpec("spec.yaml");
        new SpecCodeGenerator(spec, "org.generated", dir.getRoot()).generate();
        new JsonFrameworkGenerator(spec, "org.generated", dir.getRoot(), ValueWriter.NullStrategy.OMIT).generate();

        spec = loadSpec("raw-naming.yaml");
        new SpecCodeGenerator(spec, "org.generated.raw", dir.getRoot()).generate();
        new JsonFrameworkGenerator(spec, "org.generated.raw", dir.getRoot(), ValueWriter.NullStrategy.OMIT).generate();

        this.compiled = new CompiledCode.Builder()
                .classpath(CompiledCode.findLibraryInClasspath("jackson-core"))
                .source(this.dir.getRoot())
                .compile();
        this.classes = this.compiled.classLoader();
    }

    @Test
    public void writerSignature() throws Exception {
        assertThat(
                this.compiled.getClass("org.generated.json.ExampleValueWriter"),
                is(aClass()
                        .with(aPublic().method().named("write")
                                .withParameters(JsonGenerator.class, ExampleValue.class)
                                .returningVoid()
                                .throwing(IOException.class)
                        )
                        .with(aPublic().method().named("writeArray")
                                .withParameters(classType(JsonGenerator.class), typeArray(classType(ExampleValue.class)))
                                .returningVoid()
                                .throwing(IOException.class)
                        )
                )
        );
    }

    @Test
    public void writeStringProperty() throws Exception {
        ExampleValue value = ExampleValue.builder()
                .prop("a value")
                .build();

        Object writer = this.compiled.getClass("org.generated.json.ExampleValueWriter").newInstance();
        try (OutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            this.compiled.on(writer).invoke("write", JsonGenerator.class, ExampleValue.class).with(generator, value);
            generator.close();

            assertThat(
                    out.toString(),
                    is("{" +
                            "\"prop\":\"a value\"" +
                            "}")
            );
        }
    }

    @Test
    public void writeStringArrayProperty() throws Exception {
        ExampleValue value = ExampleValue.builder()
                .listProp("a", "b", "c")
                .build();

        Object writer = this.compiled.getClass("org.generated.json.ExampleValueWriter").newInstance();
        try (OutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            this.compiled.on(writer).invoke("write", JsonGenerator.class, ExampleValue.class).with(generator, value);
            generator.close();

            assertThat(
                    out.toString(),
                    is("{" +
                            "\"listProp\":[\"a\",\"b\",\"c\"]" +
                            "}")
            );
        }
    }


    @Test
    public void writeComplexProperty() throws Exception {
        ExampleValue value = ExampleValue.builder()
                .complex(new Complex.Builder()
                        .sub("a value")
                        .build())
                .build();

        Object writer = this.compiled.getClass("org.generated.json.ExampleValueWriter").newInstance();
        try (OutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            this.compiled.on(writer).invoke("write", JsonGenerator.class, ExampleValue.class).with(generator, value);
            generator.close();

            assertThat(
                    out.toString(),
                    is("{" +
                            "\"complex\":{\"sub\":\"a value\"}" +
                            "}")
            );
        }
    }

    @Test
    public void writeComplexListProperty() throws Exception {
        ExampleValue value = ExampleValue.builder()
                .complexList(new ComplexList.Builder()
                        .sub("a value")
                        .build())
                .build();

        Object writer = this.compiled.getClass("org.generated.json.ExampleValueWriter").newInstance();
        try (OutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            this.compiled.on(writer).invoke("write", JsonGenerator.class, ExampleValue.class).with(generator, value);
            generator.close();

            assertThat(
                    out.toString(),
                    is("{" +
                            "\"complexList\":[{\"sub\":\"a value\"}]" +
                            "}")
            );
        }
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
                .timeProp(LocalTime.parse("10:15:30.123456789"))
                .dateTimeProp(LocalDateTime.parse("2011-12-03T10:15:30.123456789"))
                .tzDateTimeProp(ZonedDateTime.parse("2011-12-03T10:15:30.123456789+01:00"))
                .build();
        Object writer = this.compiled.getClass("org.generated.json.SimplePropsWriter").newInstance();
        try (OutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            this.compiled.on(writer).invoke("write", JsonGenerator.class, SimpleProps.class).with(generator, value);
            generator.close();

            assertThat(
                    out.toString(),
                    is("{" +
                            "\"stringProp\":\"str\"," +
                            "\"integerProp\":12," +
                            "\"longProp\":12," +
                            "\"floatProp\":12.12," +
                            "\"doubleProp\":12.12," +
                            "\"booleanProp\":true," +
                            "\"dateProp\":\"2011-12-03\"," +
                            "\"timeProp\":\"10:15:30.123456789\"," +
                            "\"dateTimeProp\":\"2011-12-03T10:15:30.123456789\"," +
                            "\"tzDateTimeProp\":\"2011-12-03T10:15:30.123456789+01:00\"" +
                            "}")
            );
        }
    }

    @Test
    public void writeFloatsWith0Decimal() throws Exception {
        SimpleProps value = new SimpleProps.Builder()
                .floatProp(12f)
                .doubleProp(12d)
                .build();
        Object writer = this.compiled.getClass("org.generated.json.SimplePropsWriter").newInstance();
        try (OutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            this.compiled.on(writer).invoke("write", JsonGenerator.class, SimpleProps.class).with(generator, value);
            generator.close();

            assertThat(
                    out.toString(),
                    is("{" +
                            "\"floatProp\":12.0," +
                            "\"doubleProp\":12.0" +
                            "}")
            );
        }
    }

    @Test
    public void writeArray() throws Exception {
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
        try (OutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            this.compiled.on(writer).invoke("writeArray", JsonGenerator.class, SimpleProps[].class).with(generator, new SimpleProps[]{value, value});
            generator.close();

            assertThat(
                    out.toString(),
                    is("[" +
                            "{" +
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
                            "}," +
                            "{" +
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
                            "}" +
                            "]")
            );
        }
    }

    @Test
    public void writeEmptyArray() throws Exception {
        Object writer = this.compiled.getClass("org.generated.json.SimplePropsWriter").newInstance();
        try (OutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            this.compiled.on(writer).invoke("writeArray", JsonGenerator.class, SimpleProps[].class).with(generator, new SimpleProps[]{});
            generator.close();

            assertThat(
                    out.toString(),
                    is("[]")
            );
        }
    }

    @Test
    public void writeNullArray() throws Exception {
        Object writer = this.compiled.getClass("org.generated.json.SimplePropsWriter").newInstance();
        try (OutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            this.compiled.on(writer).invoke("writeArray", JsonGenerator.class, SimpleProps[].class).with(generator, null);
            generator.close();

            assertThat(
                    out.toString(),
                    is("null")
            );
        }
    }

    @Test
    public void writeBinary() throws Exception {
        Binary value = Binary.builder()
                .prop("binary".getBytes())
                .build();

        Object writer = this.compiled.getClass("org.generated.json.BinaryWriter").newInstance();
        try (OutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            this.compiled.on(writer).invoke("write", JsonGenerator.class, Binary.class).with(generator, value);
            generator.close();

            assertThat(
                    out.toString(),
                    is(String.format("{\"prop\":\"%s\"}", new String(Base64.getEncoder().encode("binary".getBytes()))))
            );
        }
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
                .timeProp(
                        LocalTime.parse("10:15:30.123456789"), LocalTime.parse("10:15:30.12345678"), LocalTime.parse("10:15:30.1234567"),
                        LocalTime.parse("10:15:30.123456"), LocalTime.parse("10:15:30.12345"), LocalTime.parse("10:15:30.1234"),
                        LocalTime.parse("10:15:30.123"), LocalTime.parse("10:15:30.12"), LocalTime.parse("10:15:30.1"), LocalTime.parse("10:15:30")
                )
                .dateTimeProp(LocalDateTime.parse("2011-12-03T10:15:30"))
                .tzDateTimeProp(ZonedDateTime.parse("2011-12-03T10:15:30+01:00"))
                .build();
        Object writer = this.compiled.getClass("org.generated.json.ArraySimplePropsWriter").newInstance();
        try (OutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            this.compiled.on(writer).invoke("write", JsonGenerator.class, ArraySimpleProps.class).with(generator, value);
            generator.close();

            assertThat(
                    out.toString(),
                    is("{" +
                            "\"stringProp\":[\"str\"]," +
                            "\"integerProp\":[12]," +
                            "\"longProp\":[12]," +
                            "\"floatProp\":[12.12]," +
                            "\"doubleProp\":[12.12]," +
                            "\"booleanProp\":[true]," +
                            "\"dateProp\":[\"2011-12-03\"]," +
                            "\"timeProp\":[\"10:15:30.123456789\",\"10:15:30.12345678\",\"10:15:30.1234567\",\"10:15:30.123456\",\"10:15:30.12345\",\"10:15:30.1234\",\"10:15:30.123\",\"10:15:30.12\",\"10:15:30.1\",\"10:15:30\"]," +
                            "\"dateTimeProp\":[\"2011-12-03T10:15:30\"]," +
                            "\"tzDateTimeProp\":[\"2011-12-03T10:15:30+01:00\"]" +
                            "}")
            );
        }
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
        try (OutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            this.compiled.on(writer).invoke("write", JsonGenerator.class, ArraySimpleProps.class).with(generator, value);
            generator.close();

            assertThat(
                    out.toString(),
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
        try (OutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            this.compiled.on(writer).invoke("write", JsonGenerator.class, RefValue.class).with(generator, value);
            generator.close();

            assertThat(
                    out.toString(),
                    is("{" +
                            "\"ref\":{\"prop\":\"value\"}," +
                            "\"refs\":[{\"prop\":\"value\"}]}"
                    )
            );
        }
    }

    @Test
    public void writeNullReferencedValue() throws Exception {
        RefValue value = new RefValue.Builder()
                .ref((Referenced) null)
                .refs((Referenced) null)
                .build();

        Object writer = this.compiled.getClass("org.generated.json.RefValueWriter").newInstance();
        try (OutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            this.compiled.on(writer).invoke("write", JsonGenerator.class, RefValue.class).with(generator, value);
            generator.close();

            assertThat(
                    out.toString(),
                    is("{" +
                            "\"refs\":[null]}"
                    )
            );
        }
    }

    @Test
    public void writeExternalReferenceValue() throws Exception {
        ValueObjectProps value = ValueObjectProps.builder()
                .prop(ExtReferenced.builder()
                        .prop("val")
                        .build())
                .build();

        Object writer = this.compiled.getClass("org.generated.json.ValueObjectPropsWriter").newInstance();
        try (OutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            this.compiled.on(writer).invoke("write", JsonGenerator.class, ValueObjectProps.class).with(generator, value);
            generator.close();

            assertThat(
                    out.toString(),
                    is("{\"prop\":{\"prop\":\"val\"}}"
                    )
            );
        }
    }

    @Test
    public void writeEmbeddedValue() throws Exception {
        Embedded value = Embedded.builder()
                .single(Single.builder().prop("val").build())
                .multiple(
                        Multiple.builder().prop("v1").build(),
                        Multiple.builder().prop("v2").build()
                )
                .build();

        Object writer = this.compiled.getClass("org.generated.json.EmbeddedWriter").newInstance();
        try (OutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            this.compiled.on(writer).invoke("write", JsonGenerator.class, Embedded.class).with(generator, value);
            generator.close();

            assertThat(
                    out.toString(),
                    is("{\"single\":{\"prop\":\"val\"},\"multiple\":[{\"prop\":\"v1\"},{\"prop\":\"v2\"}]}")
            );
        }
    }

    @Test
    public void writeOutsideEnumValue() throws Exception {
        EnumProperties value = new EnumProperties.Builder()
                .single(DayOfWeek.MONDAY)
                .multiple(DayOfWeek.MONDAY, DayOfWeek.TUESDAY)
                .build();

        Object writer = this.compiled.getClass("org.generated.json.EnumPropertiesWriter").newInstance();
        try (OutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            this.compiled.on(writer).invoke("write", JsonGenerator.class, EnumProperties.class).with(generator, value);
            generator.close();

            assertThat(
                    out.toString(),
                    is("{" +
                            "\"single\":\"MONDAY\"," +
                            "\"multiple\":[\"MONDAY\",\"TUESDAY\"]" +
                            "}"
                    )
            );
        }
    }

    @Test
    public void writeInSpecEnumValue() throws Exception {
        InSpecEnumProperties value = new InSpecEnumProperties.Builder()
                .single(InSpecEnumProperties.Single.A)
                .multiple(InSpecEnumProperties.Multiple.A, InSpecEnumProperties.Multiple.B)
                .build();

        Object writer = this.compiled.getClass("org.generated.json.InSpecEnumPropertiesWriter").newInstance();
        try (OutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            this.compiled.on(writer).invoke("write", JsonGenerator.class, InSpecEnumProperties.class).with(generator, value);
            generator.close();

            assertThat(
                    out.toString(),
                    is("{" +
                            "\"single\":\"A\"," +
                            "\"multiple\":[\"A\",\"B\"]" +
                            "}"
                    )
            );
        }
    }


    @Test
    public void rawPropertyNameHint() throws Exception {
        Hints value = Hints.builder().propName("value").build();
        ObjectHelper writer = this.classes.get("org.generated.json.HintsWriter").newInstance();
        try (OutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            writer.call("write", JsonGenerator.class, Hints.class).with(generator, value);
            generator.close();

            assertThat(
                    out.toString(),
                    is("{" +
                            "\"Raw Property Name\":\"value\"" +
                            "}"
                    )
            );
        }
    }

    @Test
    public void nestedRawHints() throws Exception {
        RootType value = RootType.builder()
                .rootProp("root")
                .nestedOne(NestedOne.builder()
                        .nestedOneProp("nested one")
                        .nestedTwo(NestedTwo.builder()
                                .nestedTwoProp("nested two")
                                .nestedThree(NestedThree.builder()
                                        .nestedThreeProp("nested three")
                                        .build())
                                .build())
                        .build())
                .build();
        ObjectHelper writer = this.classes.get("org.generated.raw.json.RootTypeWriter").newInstance();
        try (OutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            writer.call("write", JsonGenerator.class, RootType.class).with(generator, value);
            generator.close();

            System.out.println(out.toString());

            assertThat(
                    out.toString(),
                    is("{" +
                            "\"root-prop\":\"root\"," +
                            "\"nested-one\":{" +
                            "\"nested-one-prop\":\"nested one\"," +
                            "\"nested-two\":{" +
                            "\"nested-two-prop\":\"nested two\"," +
                            "\"nested-three\":{" +
                            "\"nested-three-prop\":\"nested three\"" +
                            "}" +
                            "}" +
                            "}" +
                            "}"
                    )
            );
        }
    }
}
