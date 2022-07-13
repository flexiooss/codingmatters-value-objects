package org.codingmatters.value.objects.generation;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.FileHelper;
import org.codingmatters.tests.compile.helpers.ClassLoaderHelper;
import org.codingmatters.value.objects.spec.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.Serializable;


import static org.codingmatters.tests.reflect.ReflectMatchers.aStatic;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BuilderConformsToTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();



    private final Spec spec  = spec()
            .addValue(valueSpec().name("singleProtocol")
                    .addBuilderConformsTo(Serializable.class.getName())
            )
            .addValue(valueSpec().name("manyProtocols")
                    .addConformsTo()
            )
            .addValue(valueSpec()
                    .name("nested")
                    .addProperty(PropertySpec.property()
                            .name("embed").type(type().typeKind(TypeKind.EMBEDDED).cardinality(PropertyCardinality.SINGLE)
                                    .embeddedValueSpec(AnonymousValueSpec.anonymousValueSpec()
                                            .addBuilderConformsTo(Serializable.class.getName())
                                            .build())
                            )
                            .build())
                    .build())
            .build();

    @Rule
    public FileHelper fileHelper = new FileHelper();
    private ClassLoaderHelper classes;

    @Before
    public void setUp() throws Exception {
        new SpecCodeGenerator(this.spec, "org.generated", dir.getRoot()).generate();

        this.fileHelper.printJavaContent("", this.dir.getRoot());
        this.classes = CompiledCode.builder().source(this.dir.getRoot()).compile().classLoader();
    }

    @Test
    public void singleImplementedInterface() throws Exception {
        this.fileHelper.printFile(this.dir.getRoot(), "SingleProtocol.java");
        assertThat(
                this.classes.get("org.generated.SingleProtocol$Builder").get(),
                is(aStatic().public_().class_().implementing(Serializable.class))
        );
    }

    @Test
    public void manyImplementedInterface() throws Exception {
        assertThat(
                this.classes.get("org.generated.ManyProtocols$Builder").get(),
                is(aStatic().public_().class_())
        );
    }


    @Test
    public void nestedImplementedInterface() throws Exception {
        this.fileHelper.printFile(this.dir.getRoot(), "Embed.java");
        assertThat(
                this.classes.get("org.generated.nested.Embed$Builder").get(),
                is(aStatic().public_().class_().implementing(Serializable.class))
        );
    }
}
