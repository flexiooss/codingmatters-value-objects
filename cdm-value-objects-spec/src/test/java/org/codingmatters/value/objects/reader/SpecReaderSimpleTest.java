package org.codingmatters.value.objects.reader;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.codingmatters.value.objects.exception.LowLevelSyntaxException;
import org.codingmatters.value.objects.exception.SpecSyntaxException;
import org.codingmatters.value.objects.spec.PropertyCardinality;
import org.codingmatters.value.objects.spec.TypeKind;
import org.codingmatters.value.objects.spec.ValueSpec;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.codingmatters.value.objects.utils.Utils.streamFor;
import static org.codingmatters.value.objects.utils.Utils.string;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 9/3/16.
 */
public class SpecReaderSimpleTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private SpecReader reader = new SpecReader();

    @Test
    public void underlyingParserError_throwsLowLevelSyntaxException() throws Exception {
        this.exception.expect(LowLevelSyntaxException.class);
        this.exception.expectMessage("spec must be valid YAML expression");
        this.exception.expectCause(Matchers.isA(JsonMappingException.class));

        try(InputStream in = streamFor(string()
                .line("val")
                .line("  prop string")
                .build())) {
            reader.read(in);
        }
    }

    @Test
    public void malformedPropertyName() throws Exception {
        this.exception.expect(SpecSyntaxException.class);
        this.exception.expectMessage("malformed property name \"val/prop erty\" : should be a valid java identifier");

        try(InputStream in = streamFor(string()
                .line("val:")
                .line("  prop erty: string")
                .build())) {
            reader.read(in);
        }
    }

    @Test
    public void invalidType_throwsSpecSyntaxException() throws Exception {
        this.exception.expect(SpecSyntaxException.class);
        this.exception.expectMessage("invalid type for property \"val/prop\" : strrrrring, should be one of ");

        try(InputStream in = streamFor(string()
                .line("val:")
                .line("  prop: strrrrring")
                .build())) {
            reader.read(in);
        }
    }

    @Test
    public void manySimpleValueSpec() throws Exception {
        try(InputStream in = streamFor(string()
                .line("val1:")
                .line("val2:")
                .build())) {
            assertThat(
                    reader.read(in),
                    is(
                            spec()
                                    .addValue(valueSpec().name("val1"))
                                    .addValue(valueSpec().name("val2"))
                            .build()
                    )
            );
        }
    }

    @Test
    public void valueWithStringProperties() throws Exception {
        try(InputStream in = streamFor(string()
                .line("val:")
                .line("  p1: string")
                .line("  p2: string")
                .build())) {
            assertThat(
                    reader.read(in),
                    is(
                            spec()
                                    .addValue(valueSpec().name("val")
                                            .addProperty(property().name("p1").type(type().typeRef(String.class.getName()).typeKind(TypeKind.JAVA_TYPE)))
                                            .addProperty(property().name("p2").type(type().typeRef(String.class.getName()).typeKind(TypeKind.JAVA_TYPE)))
                                    )
                                    .build()
                    )
            );
        }
    }

    @Test
    public void propertyWithImplementationType() throws Exception {
        try(InputStream in = streamFor(string()
                .line("val1:")
                .line("  p: java.lang.String")
                .build())) {
            assertThat(
                    reader.read(in),
                    is(
                            spec()
                                    .addValue(valueSpec().name("val1")
                                            .addProperty(property().name("p").type(type().typeRef("java.lang.String").typeKind(TypeKind.JAVA_TYPE)))
                                    )
                                    .build()
                    )
            );
        }
    }

    @Test
    public void propertyWithInSpecValueObjectType() throws Exception {
        try(InputStream in = streamFor(string()
                .line("val1:")
                .line("  p: $val2")
                .line("val2:")
                .build())) {
            assertThat(
                    reader.read(in),
                    is(
                            spec()
                                    .addValue(valueSpec().name("val1")
                                            .addProperty(property().name("p").type(type().typeRef("val2").typeKind(TypeKind.IN_SPEC_VALUE_OBJECT)))
                                    )
                                    .addValue(valueSpec().name("val2"))
                                    .build()
                    )
            );
        }
    }

    @Test
    public void propertyWithInSpecValueObjectType_notDeclared_throwsSpecSyntaxException() throws Exception {
        this.exception.expect(SpecSyntaxException.class);
        this.exception.expectMessage("undeclared referenced type for \"val1/p\" : a referenced type should be declared in the same spec");

        try(InputStream in = streamFor(string()
                .line("val1:")
                .line("  p: $val2")
                .build())) {
            reader.read(in);
        }
    }

    @Test
    public void singleProperty() throws Exception {
        try(InputStream in = streamFor(string()
                .line("val:")
                .line("  prop: string")
                .build())) {
            assertThat(
                    reader.read(in),
                    is(
                            spec()
                                    .addValue(valueSpec().name("val")
                                            .addProperty(property().name("prop").type(type()
                                                    .typeRef(String.class.getName())
                                                    .typeKind(TypeKind.JAVA_TYPE)
                                                    .cardinality(PropertyCardinality.SINGLE)))
                                    )
                                    .build()
                    )
            );
        }
    }

    @Test
    public void listProperty() throws Exception {
        try(InputStream in = streamFor(string()
                .line("val:")
                .line("  listProp: ")
                .line("    $list: string")
                .line("  setProp: ")
                .line("    $set: string")
                .build())) {
            assertThat(
                    reader.read(in),
                    is(
                            spec()
                                    .addValue(valueSpec().name("val")
                                            .addProperty(property().name("listProp").type(type()
                                                    .typeRef(String.class.getName())
                                                    .typeKind(TypeKind.JAVA_TYPE)
                                                    .cardinality(PropertyCardinality.LIST)))
                                            .addProperty(property().name("setProp").type(type()
                                                    .typeRef(String.class.getName())
                                                    .typeKind(TypeKind.JAVA_TYPE)
                                                    .cardinality(PropertyCardinality.SET)))
                                    )
                                    .build()
                    )
            );
        }
    }

    @Test
    public void basicTypesProperty() throws Exception {
        try(InputStream in = streamFor(string()
                .line("val:")
                .line("  stringProp: string")
                .line("  intProp: int")
                .line("  longProp: long")
                .line("  floatProp: float")
                .line("  doubleProp: double")
                .line("  boolProp: bool")
                .line("  dateProp: bool")
                .line("  timeProp: bool")
                .line("  boolProp: bool")
                .line("  dateProp: date")
                .line("  timeProp: time")
                .line("  dateTimeProp: date-time")
                .line("  tzDateTimeProp: tz-date-time")
                .build())) {
            ValueSpec actual = reader.read(in).valueSpec("val");

            assertThat(actual.propertySpec("stringProp").typeSpec().typeRef(), is(String.class.getName()));
            assertThat(actual.propertySpec("intProp").typeSpec().typeRef(), is(Integer.class.getName()));
            assertThat(actual.propertySpec("longProp").typeSpec().typeRef(), is(Long.class.getName()));
            assertThat(actual.propertySpec("floatProp").typeSpec().typeRef(), is(Float.class.getName()));
            assertThat(actual.propertySpec("doubleProp").typeSpec().typeRef(), is(Double.class.getName()));
            assertThat(actual.propertySpec("boolProp").typeSpec().typeRef(), is(Boolean.class.getName()));
            assertThat(actual.propertySpec("dateProp").typeSpec().typeRef(), is(LocalDate.class.getName()));
            assertThat(actual.propertySpec("timeProp").typeSpec().typeRef(), is(LocalTime.class.getName()));
            assertThat(actual.propertySpec("dateTimeProp").typeSpec().typeRef(), is(LocalDateTime.class.getName()));
            assertThat(actual.propertySpec("tzDateTimeProp").typeSpec().typeRef(), is(ZonedDateTime.class.getName()));

        }

    }
}
