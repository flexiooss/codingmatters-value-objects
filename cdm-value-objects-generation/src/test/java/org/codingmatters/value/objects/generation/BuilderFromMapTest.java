package org.codingmatters.value.objects.generation;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.FileHelper;
import org.codingmatters.tests.compile.helpers.helpers.ObjectHelper;
import org.codingmatters.value.objects.spec.PropertyCardinality;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.codingmatters.value.objects.spec.TypeToken;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.codingmatters.tests.reflect.ReflectMatchers.aStatic;
import static org.codingmatters.tests.reflect.ReflectMatchers.anInterface;
import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class BuilderFromMapTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    private final Spec spec = spec()
            .addValue(valueSpec().name("val")
                    .addProperty(property().name("prop1").type(type().typeRef(String.class.getName()).typeKind(TypeKind.JAVA_TYPE)))
                    .addProperty(property().name("prop2").type(type().typeRef(String.class.getName()).typeKind(TypeKind.JAVA_TYPE)))
            )
            .addValue(valueSpec().name("noPropertyVal"))
            .addValue(valueSpec().name("complexVal")
                    .addProperty(property().name("prop").type(type().typeRef("val").typeKind(TypeKind.IN_SPEC_VALUE_OBJECT)))
            )
            .addValue(valueSpec().name("valueWithDateTime")
                    .addProperty(property().name("dt").type(type()
                            .typeRef(TypeToken.DATE_TIME.getImplementationType())
                            .typeKind(TypeKind.JAVA_TYPE)))
                    .addProperty(property().name("d").type(type()
                            .typeRef(TypeToken.DATE.getImplementationType())
                            .typeKind(TypeKind.JAVA_TYPE)))
                    .addProperty(property().name("t").type(type()
                            .typeRef(TypeToken.TIME.getImplementationType())
                            .typeKind(TypeKind.JAVA_TYPE)))
                    .addProperty(property().name("dts").type(type()
                            .cardinality(PropertyCardinality.LIST)
                            .typeRef(TypeToken.DATE_TIME.getImplementationType())
                            .typeKind(TypeKind.JAVA_TYPE)))
            )
            .addValue(valueSpec().name("dashed")
                    .addProperty(property().name("dashedPropSimpleSingle")
                            .type(type()
                                .typeRef(String.class.getName())
                                .typeKind(TypeKind.JAVA_TYPE))
                            .hints(stringSet("property:raw(dashed-prop-simple-single)")))
                    .addProperty(property().name("dashedPropSimpleMultiple").type(type()
                            .cardinality(PropertyCardinality.LIST)
                            .typeRef(String.class.getName())
                            .typeKind(TypeKind.JAVA_TYPE))
                            .hints(stringSet("property:raw(dashed-prop-simple-multiple)")))
                    .addProperty(property().name("dashedPropParsedSingle").type(type()
                            .typeRef(TypeToken.DATE_TIME.getImplementationType())
                            .typeKind(TypeKind.JAVA_TYPE))
                            .hints(stringSet("property:raw(dashed-prop-parsed-single)")))
                    .addProperty(property().name("dashedPropParsedMultiple").type(type()
                            .cardinality(PropertyCardinality.LIST)
                            .typeRef(TypeToken.DATE_TIME.getImplementationType())
                            .typeKind(TypeKind.JAVA_TYPE))
                            .hints(stringSet("property:raw(dashed-prop-parsed-multiple)")))
                    .build())

            .addValue(valueSpec().name("strings")
                    .addProperty(property().name("str").type(type().typeRef(String.class.getName()).typeKind(TypeKind.JAVA_TYPE)))
                    .addProperty(property().name("strs").type(type().cardinality(PropertyCardinality.LIST).typeRef(String.class.getName()).typeKind(TypeKind.JAVA_TYPE)))
            )
            .build();




    private Set<String> stringSet(String... strings) {
        return new HashSet<>(Arrays.asList(strings));
    }

    private CompiledCode compiled;

    @Rule
    public FileHelper fileHelper = new FileHelper();

    @Before
    public void setUp() throws Exception {
        new SpecCodeGenerator(this.spec, "org.generated", dir.getRoot()).generate();
        this.compiled = CompiledCode.builder().source(this.dir.getRoot()).compile();
//        this.fileHelper.printJavaContent("", this.dir.getRoot());
        this.fileHelper.printFile(this.dir.getRoot(), "Strings.java");
    }


    @Test
    public void signature() throws Exception {
        assertThat(
                compiled.getClass("org.generated.Val").getDeclaredMethod("fromMap", Map.class),
                is(aStatic().public_().method()
                        .named("fromMap")
                        .withParameters(Map.class)
                        .returning(compiled.getClass("org.generated.Val$Builder"))
                )
        );

        assertThat(compiled.getClass("org.generated.Val"),
                is(anInterface()
                        .with(
                                aStatic().public_().method()
                                        .named("fromMap")
                                        .withParameters(Map.class)
                                        .returning(compiled.getClass("org.generated.Val$Builder"))
                        )
                )
        );
    }


    @Test
    public void givenLocalDateTime_orLocalDate_orLocalTimeProperty__whenGoingToMapThenBack_andWhenGoingFromStringPropertyMap__thenValueObjectsAreEqual() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        List<LocalDateTime> nows = new LinkedList<>();
        nows.add(now); nows.add(now);

        ObjectHelper value = compiled.classLoader().get("org.generated.ValueWithDateTime")
                .call("builder")
                .call("dt", LocalDateTime.class).with(now)
                .call("d", LocalDate.class).with(now.toLocalDate())
                .call("t", LocalTime.class).with(now.toLocalTime())
                .call("dts", Collection.class).with(nows)
                .call("build");

        ObjectHelper valueFromMap = compiled.classLoader().get("org.generated.ValueWithDateTime")
                .call("fromMap", Map.class).with(value.as("org.generated.ValueWithDateTime").call("toMap").get())
                .call("build");
        assertThat(valueFromMap.get(), is(value.get()));

        Map rawMap = new HashMap();
        rawMap.put("dt", now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        rawMap.put("d", now.format(DateTimeFormatter.ISO_LOCAL_DATE));
        rawMap.put("t", now.format(DateTimeFormatter.ISO_LOCAL_TIME));

        List mixedNows = new LinkedList();
        mixedNows.add(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        mixedNows.add(now);
        rawMap.put("dts", mixedNows);

        ObjectHelper valueFromRawMap = compiled.classLoader().get("org.generated.ValueWithDateTime")
                .call("fromMap", Map.class).with(rawMap)
                .call("build");
        assertThat(valueFromRawMap.get(), is(value.get()));
    }


    @Test
    public void givenTypeWithRawProperties__whenToMapAndBack__thenToMapUsesRawPropertyyNames_andFromMapAcceptsBoth() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        ObjectHelper value = compiled.classLoader().get("org.generated.Dashed")
                .call("builder")
                .call("dashedPropSimpleSingle", String.class).with("hello")
                .call("dashedPropSimpleMultiple", Collection.class).with(Arrays.asList("hello", "world"))
                .call("dashedPropParsedSingle", LocalDateTime.class).with(now)
                .call("dashedPropParsedMultiple", Collection.class).with(Arrays.asList(now, now))
                .call("build")
                .as("org.generated.Dashed");

        Map<String, Object> map = (Map) value.call("toMap").get();
        assertThat(map.keySet(), containsInAnyOrder(
                "dashed-prop-simple-single",
                "dashed-prop-simple-multiple",
                "dashed-prop-parsed-single",
                "dashed-prop-parsed-multiple"
        ));
        assertThat(
                compiled.classLoader().get("org.generated.Dashed").call("fromMap", Map.class).with(map).call("build").get(),
                is(value.get())
        );

        Map<String, Object> raw = new HashMap<>();
        raw.put("dashedPropSimpleSingle", "hello");
        raw.put("dashedPropSimpleMultiple", Arrays.asList("hello", "world"));
        raw.put("dashedPropParsedSingle", now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        raw.put("dashedPropParsedMultiple", Arrays.asList(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), now));

        assertThat(
                compiled.classLoader().get("org.generated.Dashed").call("fromMap", Map.class).with(raw).call("build").get(),
                is(value.get())
        );
    }

    @Test
    public void givenFromMap__whenWrongTypeOnSingle__thenPropertyIgnored() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("str", LocalDateTime.now());

        ObjectHelper actual = compiled.classLoader().get("org.generated.Strings").call("fromMap", Map.class).with(map).call("build").as("org.generated.Strings");

        assertThat(
                actual.call("str").get(),
                is(nullValue())
        );
    }

    @Test
    public void givenFromMap__whenWrongTypeOnMultiple__thenValueIgnored() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("strs", Arrays.asList(LocalDateTime.now(), "kept"));

        ObjectHelper actual = compiled.classLoader().get("org.generated.Strings").call("fromMap", Map.class).with(map).call("build").as("org.generated.Strings");

        System.out.println(actual.call("strs").get());
        assertThat(
                (Object []) actual.call("strs").call("toArray").get(),
                is(arrayContaining("kept"))
        );
    }

    @Test
    public void givenFromMap__whenWrongCardinalityOnSingleProperty__thenPropertyIgnored() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("str", Arrays.asList("hello", "world"));

        ObjectHelper actual = compiled.classLoader().get("org.generated.Strings").call("fromMap", Map.class).with(map).call("build").as("org.generated.Strings");

        assertThat(
                actual.call("str").get(),
                is(nullValue())
        );
    }

    @Test
    public void givenFromMap__whenWrongCardinalityOnMultipleProperty__thenPropertyIgnored() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("strs", "hello");

        ObjectHelper actual = compiled.classLoader().get("org.generated.Strings").call("fromMap", Map.class).with(map).call("build").as("org.generated.Strings");

        assertThat(
                actual.call("strs").get(),
                is(nullValue())
        );
    }
}
