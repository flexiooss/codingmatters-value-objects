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
import java.util.stream.Stream;

import static org.codingmatters.tests.reflect.ReflectMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 10/11/16.
 */
public class ValueListTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();
    private CompiledCode compiled;

    @Before
    public void setUp() throws Exception {
        String packageName = "org.generated";
        File dest = this.dir.newFolder();

        TypeSpec type = new ValueList(packageName).type();
        JavaFile file = JavaFile.builder(packageName, type).build();
        file.writeTo(dest);

        file = JavaFile.builder(packageName, new ValueListImplementation(packageName, type).type()).build();
        file.writeTo(dest);

        this.compiled = CompiledCode.builder().source(dest).compile();
    }

    @Test
    public void extendsIterable() throws Exception {
        assertThat(
                compiled.getClass("org.generated.ValueList").getGenericInterfaces()[0],
                is(genericType().baseClass(Iterable.class).withParameters(typeParameter().named("E")))
        );
    }

    @Test
    public void signatures() throws Exception {
        assertThat(compiled.getClass("org.generated.ValueList"), is(anInterface()
                //boolean contains(Object o);
                .with(aMethod().named("contains").withParameters(Object.class).returning(boolean.class))
        ));
        assertThat(compiled.getClass("org.generated.ValueList"), is(anInterface()
                //boolean containsAll(Collection<?> c);
                .with(aMethod().named("containsAll").withParameters(genericType().baseClass(Collection.class).withParameters(typeParameter().wildcard())).returning(boolean.class))
        ));
        assertThat(compiled.getClass("org.generated.ValueList"), is(anInterface()
                //E get(int index);
                .with(aMethod().named("get").withParameters(int.class).returning(variableType().named("E")))
        ));
        assertThat(compiled.getClass("org.generated.ValueList"), is(anInterface()
                //int size();
                .with(aMethod().named("size").returning(int.class))
        ));
        assertThat(compiled.getClass("org.generated.ValueList"), is(anInterface()
                //int indexOf(Object o);
                .with(aMethod().named("indexOf").withParameters(Object.class).returning(int.class))
        ));
        assertThat(compiled.getClass("org.generated.ValueList"), is(anInterface()
                //boolean isEmpty();
                .with(aMethod().named("isEmpty").returning(boolean.class))
        ));
        assertThat(compiled.getClass("org.generated.ValueList"), is(anInterface()
                //<T> T[] toArray(T[] a);
                .with(aMethod().named("toArray").withParameters(typeArray(variableType().named("T"))).returning(typeArray(variableType().named("T"))))
        ));
        assertThat(compiled.getClass("org.generated.ValueList"), is(anInterface()
                //Object[] toArray()
                .with(aMethod().named("toArray").withoutParameters().returning(Object[].class))
        ));

        assertThat(compiled.getClass("org.generated.ValueList"), is(anInterface()
                .with(aMethod().named("stream")
                        .withoutParameters()
                        .returning(genericType().baseClass(Stream.class).withParameters(typeParameter().named("E"))
                        ))
        ));

    }
}