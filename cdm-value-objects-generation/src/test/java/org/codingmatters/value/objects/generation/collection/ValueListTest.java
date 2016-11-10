package org.codingmatters.value.objects.generation.collection;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.tests.compile.CompiledCode;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Collection;

import static org.codingmatters.tests.reflect.ReflectMatchers.*;
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
        file.writeTo(System.out);

        return CompiledCode.compile(dest);
    }

    @Test
    public void signatures() throws Exception {
        String packageName = "org.generated";
        CompiledCode compiled = this.compiled(packageName, new ValueList(packageName).type());

        assertThat(
                compiled.getClass("org.generated.ValueList").getGenericInterfaces()[0],
                is(genericType().baseClass(Iterable.class).withParameters(typeParameter().named("E")))
        );

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
//        assertThat(compiled.getClass("org.generated.ValueList"), is(anInterface()
//                //E[] iterator();
//                .with(aMethod().named("iterator").returning(typeArray(variableType().named("E"))))
//        ));
//        assertThat(compiled.getClass("org.generated.ValueList"), is(anInterface()
//                //Spliterator<E> spliterator();
//                .with(aMethod().named("spliterator").returning(genericType().baseClass(Spliterator.class).withParameters(typeParameter().named("E"))))
//        ));
//        assertThat(compiled.getClass("org.generated.ValueList"), is(anInterface()
//                //void forEach(Consumer<? super T> action)
//                .with(aMethod().named("forEach")
//                        .returningVoid()
//                        .withParameters(genericType().baseClass(Consumer.class).withParameters(typeParameter().wildcard().lowerBound(variableType().named("E"))))
//                )
//        ));
    }
}