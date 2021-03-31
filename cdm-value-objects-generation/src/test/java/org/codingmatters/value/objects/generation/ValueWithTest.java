package org.codingmatters.value.objects.generation;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.helpers.ClassLoaderHelper;
import org.codingmatters.tests.compile.helpers.helpers.ObjectHelper;
import org.codingmatters.value.objects.spec.PropertyCardinality;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileWriter;

import static org.codingmatters.tests.reflect.ReflectMatchers.aPublic;
import static org.codingmatters.tests.reflect.ReflectMatchers.anInterface;
import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by nelt on 9/27/16.
 */
public class ValueWithTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();
    @Rule
    public TemporaryFolder testCodeDir = new TemporaryFolder();

    private final Spec spec = spec()
            .addValue(valueSpec().name("val")
                    .addProperty(property().name("prop1").type(type().typeRef(String.class.getName()).typeKind(TypeKind.JAVA_TYPE)))
                    .addProperty(property().name("prop2").type(type().typeRef(String.class.getName()).typeKind(TypeKind.JAVA_TYPE)))
            )
            .addValue(valueSpec().name("noPropertyVal"))
            .addValue(valueSpec().name("complexVal")
                    .addProperty(property().name("prop").type(type().typeRef("val").typeKind(TypeKind.IN_SPEC_VALUE_OBJECT)))
                    .addProperty(property().name("multipleProp").type(type().typeRef("val").typeKind(TypeKind.IN_SPEC_VALUE_OBJECT).cardinality(PropertyCardinality.LIST)))
            )
            .build();
    private ClassLoaderHelper classes;
    private CompiledCode compiled;


    @Before
    public void setUp() throws Exception {
        new SpecCodeGenerator(this.spec, "org.generated", dir.getRoot()).generate();
        this.compiled = CompiledCode.builder().source(this.dir.getRoot()).compile();
        this.classes = compiled.classLoader();
    }

    @Test
    public void signature() throws Exception {
        assertThat(classes.get("org.generated.Val").get(),
                is(anInterface()
                        .with(aPublic().method()
                                .named("withProp1")
                                .withParameters(String.class)
                                .returning(classes.get("org.generated.Val").get())
                        )
                        .with(aPublic().method()
                                .named("withProp2")
                                .withParameters(String.class)
                                .returning(classes.get("org.generated.Val").get())
                        )
                )
        );
        assertThat(classes.get("org.generated.ComplexVal").get(),
                is(anInterface()
                        .with(aPublic().method()
                                .named("withProp")
                                .withParameters(classes.get("org.generated.Val").get())
                                .returning(classes.get("org.generated.ComplexVal").get())
                        )
                        .with(aPublic().method()
                                .named("withChangedProp")
                                .withParameters(classes.get("org.generated.Val$Changer").get())
                                .returning(classes.get("org.generated.ComplexVal").get())
                        )
                )
        );
    }

    @Test
    public void simpleValue() throws Exception {
        ObjectHelper aBuilder = classes.get("org.generated.Val").call("builder");
        aBuilder.call("prop1", String.class).with("v1");
        aBuilder.call("prop2", String.class).with("v2");
        ObjectHelper aValue = aBuilder.call("build");

        ObjectHelper anotherValue = aValue.as("org.generated.Val").call("withProp1", String.class).with("v3");

        assertThat(anotherValue.as("org.generated.Val").call("prop1").get(), is("v3"));
        assertThat(anotherValue.as("org.generated.Val").call("prop2").get(), is("v2"));
    }

    @Test
    public void complex() throws Exception {
        ObjectHelper builder = classes.get("org.generated.Val").call("builder");
        builder.call("prop1", String.class).with("v1");
        builder.call("prop2", String.class).with("v2");
        ObjectHelper builded = builder.call("build");

        ObjectHelper complexBuilder = classes.get("org.generated.ComplexVal").call("builder");
        complexBuilder.call("prop", classes.get("org.generated.Val").get()).with(builded.get());
        ObjectHelper complexValue = complexBuilder.call("build");

        builder.call("prop1", String.class).with("v3");
        builded = builder.call("build");
        ObjectHelper anotherValue = complexValue.as("org.generated.ComplexVal")
                .call("withProp", classes.get("org.generated.Val").get())
                .with(builded.get());

        assertThat(anotherValue.as("org.generated.ComplexVal").call("prop").get(), is(builder.call("build").get()));
    }

    @Test
    public void complexPropertyChanger() throws Exception {
        this.createTestFile("org.test", "Test",
                "package org.test;\n" +
                        "\n" +
                        "import org.generated.Val;\n" +
                        "import org.generated.ComplexVal;\n" +
                        "\n" +
                        "public class Test {\n" +
                        "  public static ComplexVal test(ComplexVal value) {\n" +
                        "    return value.withChangedProp(builder -> builder.prop1(\"changed\"));" +
                        "  }\n" +
                        "}"
        );
        ClassLoaderHelper testClasses = this.compiled.withCompiled(this.testCodeDir.getRoot()).classLoader();

        ObjectHelper builder = testClasses.get("org.generated.Val").call("builder");
        builder.call("prop1", String.class).with("unchanged");
        builder.call("prop2", String.class).with("unchanged");
        ObjectHelper builded = builder.call("build");

        ObjectHelper complexBuilder = testClasses.get("org.generated.ComplexVal").call("builder");
        complexBuilder.call("prop", testClasses.get("org.generated.Val").get()).with(builded.get());
        ObjectHelper complexValue = complexBuilder.call("build");

        ObjectHelper changed = testClasses.get("org.test.Test").call("test", testClasses.get("org.generated.ComplexVal").get()).with(complexValue.get());

        assertThat(
                changed.as("org.generated.ComplexVal").call("prop").as("org.generated.Val").call("prop1").get(),
                is("changed")
        );
        assertThat(
                changed.as("org.generated.ComplexVal").call("prop").as("org.generated.Val").call("prop2").get(),
                is("unchanged")
        );
    }


    private void createTestFile(String path, String name, String content) throws Exception {
        File newClass = new File(this.testCodeDir.newFolder(path.split("\\.")), name + ".java");
        try(FileWriter writer = new FileWriter(newClass)) {
            writer.write(content);
            writer.flush();
        }

    }
}
