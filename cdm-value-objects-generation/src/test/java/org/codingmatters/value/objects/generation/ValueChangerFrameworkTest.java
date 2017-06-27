package org.codingmatters.value.objects.generation;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileWriter;

import static org.codingmatters.tests.reflect.ReflectMatchers.*;
import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 10/6/16.
 */
public class ValueChangerFrameworkTest {
    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    @Rule
    public TemporaryFolder testCodeDir = new TemporaryFolder();

    private final Spec spec = spec()
            .addValue(valueSpec().name("val")
                    .addProperty(property().name("prop").type(type().typeRef(String.class.getName()).typeKind(TypeKind.JAVA_TYPE)))
            )
            .build();
    private CompiledCode compiled;

    @Before
    public void setUp() throws Exception {
        new SpecCodeGenerator(this.spec, "org.generated", dir.getRoot()).generate();
        this.compiled = CompiledCode.builder().source(this.dir.getRoot()).compile();
    }

    @Test
    public void valueChangerFunctionalInterface() throws Exception {
        assertThat(compiled.getClass("org.generated.Val$Changer"), is(
                aStatic().interface_()
                        .with(aMethod().named("configure")
                                .withParameters(compiled.getClass("org.generated.Val$Builder"))
                                .returning(compiled.getClass("org.generated.Val$Builder")))
        ));
    }

    @Test
    public void changedMethod() throws Exception {
        assertThat(compiled.getClass("org.generated.Val"), is(
                anInterface()
                        .with(aMethod().named("changed")
                                .withParameters(compiled.getClass("org.generated.Val$Changer"))
                                .returning(compiled.getClass("org.generated.Val"))
                        )
        ));
    }

    @Test
    public void changedMethodImplementation() throws Exception {
        this.createTestFile("org.test", "Test",
                "package org.test;\n" +
                "\n" +
                "import org.generated.Val;\n" +
                "\n" +
                "public class Test {\n" +
                "  public static Val change(Val value) {\n" +
                "    return value.changed(builder -> builder.prop(\"changed\"));" +
                "  }\n" +
                "}"
        );
        compiled = compiled.withCompiled(testCodeDir.getRoot());

        Object builder = compiled.onClass("org.generated.Val").invoke("builder");
        compiled.on(builder).invoke("prop", String.class).with("unchanged");
        Object value = compiled.on(builder).invoke("build");

        Object newValue = compiled.onClass("org.test.Test").invoke("change", compiled.getClass("org.generated.Val")).with(value);

        assertThat(compiled.on(newValue).castedTo("org.generated.Val").invoke("prop"), is("changed"));
    }

    private void createTestFile(String path, String name, String content) throws Exception {
        File newClass = new File(this.testCodeDir.newFolder(path.split("\\.")), name + ".java");
        try(FileWriter writer = new FileWriter(newClass)) {
            writer.write(content);
            writer.flush();
        }

    }
}
