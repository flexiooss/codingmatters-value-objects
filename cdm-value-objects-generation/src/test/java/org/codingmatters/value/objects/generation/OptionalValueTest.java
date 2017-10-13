package org.codingmatters.value.objects.generation;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.FileHelper;
import org.codingmatters.tests.compile.helpers.ClassLoaderHelper;
import org.codingmatters.tests.compile.helpers.helpers.ObjectHelper;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.codingmatters.tests.reflect.ReflectMatchers.*;
import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@Ignore
public class OptionalValueTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();
    @Rule
    public FileHelper fileHelper = new FileHelper();

    private final Spec spec  = spec()
            .addValue(
                    valueSpec().name("val")
                            .addProperty(property().name("stringProp").type(type().typeKind(TypeKind.JAVA_TYPE).typeRef(String.class.getName())))
                            .addProperty(property().name("valProp").type(type().typeKind(TypeKind.IN_SPEC_VALUE_OBJECT).typeRef("Val")))
            )
            .build();
    private ClassLoaderHelper classes;

    @Before
    public void setUp() throws Exception {
        new SpecCodeGenerator(this.spec, "org.generated", dir.getRoot()).generate();

        this.fileHelper.printJavaContent("", this.dir.getRoot());
        this.fileHelper.printFile(this.dir.getRoot(), "OptionalVal.java");

        this.classes = CompiledCode.builder()
                .source(this.dir.getRoot())
                .compile()
                .classLoader();
    }

    @Test
    public void optionalClass() throws Exception {
        assertThat(
                this.classes.get("org.generated.optional.OptionalVal").get(),
                is(aPublic().class_())
        );
    }

    @Test
    public void constructor() throws Exception {
        assertThat(
                this.classes.get("org.generated.optional.OptionalVal").get(),
                is(aPublic().class_()
                        .with(aPrivate().constructor().withParameters(this.classes.get("org.generated.Val").get()))
                )
        );
    }

    @Test
    public void staticConstructor() throws Exception {
        assertThat(
                this.classes.get("org.generated.optional.OptionalVal").get(),
                is(aPublic().class_()
                        .with(aStatic().public_().method()
                                .named("of")
                                .withParameters(this.classes.get("org.generated.Val").get())
                                .returning(this.classes.get("org.generated.optional.OptionalVal").get())
                        )
                )
        );
    }

    @Test
    public void optionalMethods() throws Exception {
        assertThat(
                this.classes.get("org.generated.optional.OptionalVal").get(),
                is(aPublic().class_()
                        .with(aPublic().method()
                                .named("get")
                                .returning(this.classes.get("org.generated.Val").get())
                        )
                        .with(aPublic().method()
                                .named("isPresent")
                                .returning(boolean.class)
                        )
                        .with(aPublic().method()
                                .named("ifPresent")
                                .returningVoid()
                                .withParameters(genericType()
                                        .baseClass(Consumer.class)
                                        .withParameters(typeParameter().aClass(this.classes.get("org.generated.Val").get()))
                                )
                        )
                        .with(aPublic().method()
                                .named("filter")
                                .returning(genericType()
                                        .baseClass(Optional.class)
                                        .withParameters(typeParameter().aClass(this.classes.get("org.generated.Val").get()))
                                )
                                .withParameters(genericType()
                                        .baseClass(Predicate.class)
                                        .withParameters(typeParameter().aClass(this.classes.get("org.generated.Val").get()))
                                )
                        )
                        .with(aPublic().method()
                                .named("map")
                                .withVariable(variableType().named("U"))
                                .returning(genericType().baseClass(Optional.class).withParameters(typeParameter().named("U")))
                                .withParameters(
                                        genericType().baseClass(Function.class).withParameters(
                                                typeParameter().aClass(this.classes.get("org.generated.Val").get()),
                                                typeParameter().upperBound(variableType().named("U"))
                                        )
                                )
                        )
                        .with(aPublic().method()
                                .named("flatMap")
                                .withVariable(variableType().named("U"))
                                .returning(genericType().baseClass(Optional.class).withParameters(typeParameter().named("U")))
                                .withParameters(
                                        genericType().baseClass(Function.class).withParameters(
                                                typeParameter().aClass(this.classes.get("org.generated.Val").get()),

                                                typeParameter().aType(genericType().baseClass(Optional.class).withParameters(typeParameter().named("U")))
//                                                typeParameter().aType(genericType().baseClass(Optional.class)
//                                                        .withParameters(typeParameter().named("U"))
//                                                )
                                        )
                                )
                        )
                )
        );
        /*
        public <U> Optional<U> flatMap(Function<Book, Optional<U>> function) {
            return optional.flatMap(function);
        }

        public Book orElse(Book book) {
            return optional.orElse(book);
        }

        public Book orElseGet(Supplier<Book> supplier) {
            return optional.orElseGet(supplier);
        }

        public <X extends Throwable> Book orElseThrow(Supplier<? extends X> supplier) throws X {
            return optional.orElseThrow(supplier);
        }
        */
    }

    @Test
    public void fields() throws Exception {
        assertThat(
                this.classes.get("org.generated.optional.OptionalVal").get(),
                is(aPublic().class_()
                        .with(aPrivate().field().final_().named("optional")
                                .withType(genericType().baseClass(Optional.class)
                                        .withParameters(typeParameter().aClass(this.classes.get("org.generated.Val").get()))
                                )
                        )
                        .with(aPrivate().field().final_().named("stringProp")
                                .withType(genericType().baseClass(Optional.class)
                                        .withParameters(typeParameter().aClass(String.class))
                                )
                        )
                        .with(aPrivate().field().final_().named("valProp")
                                .withType(this.classes.get("org.generated.optional.OptionalVal").get())
                        )
                )
        );
    }

    @Test
    public void optionalGetter() throws Exception {
        assertThat(
                this.classes.get("org.generated.optional.OptionalVal").get(),
                is(aPublic().class_()
                        .with(aPublic().method().named("stringProp")
                                .returning(genericType().baseClass(Optional.class)
                                        .withParameters(typeParameter().aClass(String.class)))
                        )
                        .with(aPublic().method().named("valProp")
                                .returning(this.classes.get("org.generated.optional.OptionalVal").get())
                        )
                )
        );
    }

    @Test
    public void simpleProperty() throws Exception {
        ObjectHelper val = this.classes.get("org.generated.Val").call("builder")
                .call("stringProp", String.class).with("test")
                .call("build");
        ObjectHelper opt = this.classes.get("org.generated.optional.OptionalVal")
                .call("of", this.classes.get("org.generated.Val").get()).with(val.get());

        ObjectHelper optProp = opt.call("stringProp");

        assertThat(optProp.call("isPresent").get(), is(true));
        assertThat(optProp.call("get").get(), is("test"));
    }

    @Test
    public void valueObjectProperty() throws Exception {
        ObjectHelper val = this.classes.get("org.generated.Val").call("builder")
                .call("valProp", this.classes.get("org.generated.Val").get())
                    .with(this.classes.get("org.generated.Val").call("builder")
                            .call("stringProp", String.class).with("test")
                            .call("build").get())
                .call("stringProp", String.class).with("test")
                .call("build");
        ObjectHelper opt = this.classes.get("org.generated.optional.OptionalVal")
                .call("of", this.classes.get("org.generated.Val").get()).with(val.get());

        ObjectHelper optProp = opt.call("valProp");

        assertThat(optProp.call("isPresent").get(), is(true));
        assertThat(optProp.call("stringProp").call("isPresent").get(), is(true));
        assertThat(optProp.call("stringProp").call("get").get(), is("test"));
    }
}