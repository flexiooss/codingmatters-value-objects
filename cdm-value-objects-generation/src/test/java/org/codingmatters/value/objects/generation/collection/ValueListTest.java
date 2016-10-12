package org.codingmatters.value.objects.generation.collection;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.tests.compile.CompiledCode;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Collection;

import static org.codingmatters.tests.reflect.ReflectMatchers.aMethod;
import static org.codingmatters.tests.reflect.ReflectMatchers.anInterface;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 10/11/16.
 */
public class ValueListTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    private CompiledCode compiled(String packageName, TypeSpec type) throws Exception {
        File dest = this.dir.newFolder();
        JavaFile file = JavaFile.builder(packageName, type).build();
        file.writeTo(dest);

        return CompiledCode.compile(dest);
    }

    @Test
    public void name() throws Exception {
        String packageName = "org.generated";
        CompiledCode compiled = this.compiled(packageName, new ValueList(packageName).type());

        assertThat(compiled.getClass("org.generated.ValueList"), is(
                anInterface()
                        .with(aMethod().named("contains").withParameters(Object.class).returning(boolean.class))
                        .with(aMethod().named("containsAll").withParameters(Collection.class).returning(boolean.class))
        ));
    }
}