package org.codingmatters.value.objects.generation.collection;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.reflect.matchers.TypeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.lang.reflect.Method;
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

        assertThat(compiled.getClass("org.generated.ValueList"), is(anInterface().withParameter(aVariableType().named("E"))));

        assertThat(compiled.getClass("org.generated.ValueList"), is(anInterface()
                .with(aMethod().named("contains").withParameters(Object.class).returning(boolean.class))
        ));
        assertThat(compiled.getClass("org.generated.ValueList"), is(anInterface()
                .with(aMethod().named("containsAll").withParameters(Collection.class).returning(boolean.class))
        ));
        assertThat(compiled.getClass("org.generated.ValueList"), is(anInterface()
                .with(aMethod().named("get").withParameters(int.class).returning(aVariableType().named("E")))
        ));
        assertThat(compiled.getClass("org.generated.ValueList"), is(anInterface()
                .with(aMethod().named("size").returning(int.class))
        ));
        assertThat(compiled.getClass("org.generated.ValueList"), is(anInterface()
                .with(aMethod().named("indexOf").withParameters(Object.class).returning(int.class))
        ));
        assertThat(compiled.getClass("org.generated.ValueList"), is(anInterface()
                .with(aMethod().named("isEmpty").returning(boolean.class))
        ));
        assertThat(compiled.getClass("org.generated.ValueList"), is(anInterface()
                .with(aMethod().named("toArray").withParameters(TypeMatcher.typeArray(aVariableType().named("T"))).returning(TypeMatcher.typeArray(aVariableType().named("T"))))
        ));
        assertThat(compiled.getClass("org.generated.ValueList"), is(anInterface()
                .with(aMethod().named("iterator").returning(TypeMatcher.typeArray(aVariableType().named("E"))))
        ));
//        assertThat(compiled.getClass("org.generated.ValueList"), is(anInterface()
//                .with(aMethod().named("spliterator").returning(aGenericType().of(Spliterator.class).with(aVariableType().named("E"))))
//        ));

    }

    @Test
    public void name() throws Exception {
        String packageName = "org.generated";
        CompiledCode compiled = this.compiled(packageName, new ValueList(packageName).type());

        Method method = compiled.getClass("org.generated.ValueList").getMethod("spliterator");
        //Spliterator<E> spliterator()
        assertThat(method, is(aMethod().named("spliterator").returning(aGenericType()
//                        .of(Spliterator.class)
//                .with(aVariableType().named("E"))
        )));
    }
}