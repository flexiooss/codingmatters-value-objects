package org.codingmatters.value.objects.generation;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.FileHelper;
import org.codingmatters.tests.compile.helpers.ClassLoaderHelper;
import org.codingmatters.tests.compile.helpers.helpers.ObjectHelper;
import org.codingmatters.value.objects.spec.PropertyCardinality;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.codingmatters.tests.reflect.ReflectMatchers.*;
import static org.codingmatters.value.objects.spec.AnonymousValueSpec.anonymousValueSpec;
import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

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
                            .addProperty(property().name("enumProp").type(type().typeKind(TypeKind.ENUM).enumValues("A", "B")))
                            .addProperty(property().name("container")
                                    .type(type().typeKind(TypeKind.EMBEDDED)
                                            .embeddedValueSpec(anonymousValueSpec()
                                                    .addProperty(property().name("listProp").type(type()
                                                            .typeRef(String.class.getName())
                                                            .typeKind(TypeKind.JAVA_TYPE)
                                                            .cardinality(PropertyCardinality.LIST))
                                                    )
                                            ))
                            )
            )
            .build();
    private ClassLoaderHelper classes;

    @Before
    public void setUp() throws Exception {
        new SpecCodeGenerator(this.spec, "org.generated", dir.getRoot()).generate();

        this.fileHelper.printJavaContent("", this.dir.getRoot());
//        this.fileHelper.printFile(this.dir.getRoot(), "Val.java");
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
                                                typeParameter().aType(
                                                        genericType().baseClass(Optional.class)
                                                                .withParameters(typeParameter().named("U"))
                                                )
                                        )
                                )
                        )
                        .with(aPublic().method()
                                .named("orElse")
                                .returning(this.classes.get("org.generated.Val").get())
                                .withParameters(this.classes.get("org.generated.Val").get())
                        )
                        .with(aPublic().method()
                                .named("orElseGet")
                                .returning(this.classes.get("org.generated.Val").get())
                                .withParameters(genericType().baseClass(Supplier.class)
                                        .withParameters(typeParameter().aClass(this.classes.get("org.generated.Val").get()))
                                )
                        )

                        /*
                        public <X extends Throwable> Book orElseThrow(Supplier<? extends X> supplier) throws X {
                            return optional.orElseThrow(supplier);
                        }
                        */
                        // TODO better test with wildcard : see https://github.com/nelt/codingmatters-reflect-unit/issues/8
                        .with(aPublic().method()
                                .named("orElseGet")
//                                .withVariable(variableType().named("X extends Throwable"))
                                .returning(this.classes.get("org.generated.Val").get())
                                .withParameters(genericType().baseClass(Supplier.class)
//                                        .withParameters(typeParameter().wildcard().upperBound(variableType().named("X")))
                                )
//                                .throwing(variableType().named("X"))
                        )
                )
        );

        Method method = null;
        for (Method m : this.classes.get("org.generated.optional.OptionalVal").get().getMethods()) {
            if(m.getName().equals("orElseThrow")) {
                method = m;
            }
        }
        System.out.println(method);

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
                        .with(aPrivate().field().final_().named("enumProp")
                                .withType(genericType().baseClass(Optional.class)
                                        .withParameters(typeParameter().aClass(this.classes.get("org.generated.Val$EnumProp").get()))
                                )
                        )
                        .with(aPrivate().field().named("valProp")
                                .withType(this.classes.get("org.generated.optional.OptionalVal").get())
                        )
                        .with(aPrivate().field().named("container")
                                .withType(this.classes.get("org.generated.val.optional.OptionalContainer").get())
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
                        .with(aPublic().method().named("enumProp")
                                .returning(genericType().baseClass(Optional.class)
                                        .withParameters(typeParameter().aClass(this.classes.get("org.generated.Val$EnumProp").get()))
                                )
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

    @Test
    public void valueObjectRecursive() throws Exception {
        ObjectHelper val = this.classes.get("org.generated.Val").call("builder")
                .call("valProp", this.classes.get("org.generated.Val").get())
                .with(this.classes.get("org.generated.Val").call("builder")
                        .call("stringProp", String.class).with("test")
                        .call("build").get())
                .call("stringProp", String.class).with("test")
                .call("build");
        ObjectHelper opt = this.classes.get("org.generated.optional.OptionalVal")
                .call("of", this.classes.get("org.generated.Val").get()).with(val.get());

        assertThat(opt.call("valProp").call("valProp").call("isPresent").get(), is(false));
        assertThat(opt.call("valProp").call("valProp").call("valProp").call("isPresent").get(), is(false));
    }
}