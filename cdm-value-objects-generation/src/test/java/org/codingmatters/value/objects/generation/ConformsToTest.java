package org.codingmatters.value.objects.generation;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.FileHelper;
import org.codingmatters.tests.compile.helpers.ClassLoaderHelper;
import org.codingmatters.value.objects.spec.Spec;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.Serializable;

import static org.codingmatters.tests.reflect.ReflectMatchers.aPublic;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 6/7/17.
 */
public class ConformsToTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();



    private final Spec spec  = spec()
            .addValue(valueSpec().name("singleProtocol")
                    .addConformsTo(Serializable.class.getName())
            )
            .addValue(valueSpec().name("manyProtocols")
                    .addConformsTo()
            )
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
                this.classes.get("org.generated.SingleProtocol").get(),
                is(aPublic().interface_().implementing(Serializable.class))
        );
    }

    @Test
    public void manyImplementedInterface() throws Exception {
        assertThat(
                this.classes.get("org.generated.ManyProtocols").get(),
                is(aPublic().interface_())
        );
    }
}
