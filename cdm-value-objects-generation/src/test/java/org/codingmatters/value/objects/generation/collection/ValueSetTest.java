package org.codingmatters.value.objects.generation.collection;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.tests.compile.CompiledCode;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Collection;

import static org.codingmatters.tests.reflect.ReflectMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 11/19/16.
 */
public class ValueSetTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();
    private CompiledCode compiled;

    @Before
    public void setUp() throws Exception {
        String packageName = "org.generated";
        File dest = this.dir.newFolder();
        TypeSpec valueSetType = new ValueSet(packageName).type();
        JavaFile file = JavaFile.builder(packageName, valueSetType).build();
        file.writeTo(dest);
        file = JavaFile.builder(packageName, new ValueSetImplementation(packageName, valueSetType).type()).build();
        file.writeTo(dest);

        this.compiled = CompiledCode.builder().source(dest).compile();
    }

    @Test
    public void extendsIterable() throws Exception {
        assertThat(
                compiled.getClass("org.generated.ValueSet").getGenericInterfaces()[0],
                is(genericType().baseClass(Iterable.class).withParameters(typeParameter().named("E")))
        );
    }

    @Test
    public void signatures() throws Exception {
        assertThat(compiled.getClass("org.generated.ValueSet"), is(anInterface()
                //boolean contains(Object o);
                .with(aMethod().named("contains").withParameters(Object.class).returning(boolean.class))
        ));
        assertThat(compiled.getClass("org.generated.ValueSet"), is(anInterface()
                //boolean containsAll(Collection<?> c);
                .with(aMethod().named("containsAll").withParameters(genericType().baseClass(Collection.class).withParameters(typeParameter().wildcard())).returning(boolean.class))
        ));
        assertThat(compiled.getClass("org.generated.ValueSet"), is(anInterface()
                //int size();
                .with(aMethod().named("size").returning(int.class))
        ));
        assertThat(compiled.getClass("org.generated.ValueSet"), is(anInterface()
                //boolean isEmpty();
                .with(aMethod().named("isEmpty").returning(boolean.class))
        ));
        assertThat(compiled.getClass("org.generated.ValueSet"), is(anInterface()
                //<T> T[] toArray(T[] a);
                .with(aMethod().named("toArray").withParameters(typeArray(variableType().named("T"))).returning(typeArray(variableType().named("T"))))
        ));
        assertThat(compiled.getClass("org.generated.ValueSet"), is(anInterface()
                //Object[] toArray()
                .with(aMethod().named("toArray").withoutParameters().returning(Object[].class))
        ));
    }

}