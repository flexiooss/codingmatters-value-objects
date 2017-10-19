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
                                                    .addProperty(property().name("setProp").type(type()
                                                            .typeRef(String.class.getName())
                                                            .typeKind(TypeKind.JAVA_TYPE)
                                                            .cardinality(PropertyCardinality.SET))
                                                    )
                                            ))
                            )
            )
            .build();
    private ClassLoaderHelper classes;

    @Before
    public void setUp() throws Exception {
        new SpecCodeGenerator(this.spec, "org.generated", dir.getRoot()).generate();

//        this.fileHelper.printJavaContent("", this.dir.getRoot());
//        this.fileHelper.printFile(this.dir.getRoot(), "OptionalVal.java");
//        this.fileHelper.printFile(this.dir.getRoot(), "Val.java");
//        this.fileHelper.printFile(this.dir.getRoot(), "ValImpl.java");
//        this.fileHelper.printFile(this.dir.getRoot(), "OptionalValueList.java");
//        this.fileHelper.printFile(this.dir.getRoot(), "OptionalValueSet.java");
//        this.fileHelper.printFile(this.dir.getRoot(), "OptionalContainer.java");
//        this.fileHelper.printFile(this.dir.getRoot(), "Container.java");
//        this.fileHelper.printFile(this.dir.getRoot(), "OptionalValueList.java");

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
    public void optValueMethod() throws Exception {
        assertThat(
                this.classes.get("org.generated.Val").get(),
                is(aPublic().interface_()
                        .with(aPublic().method()
                                .named("opt")
                                .returning(this.classes.get("org.generated.optional.OptionalVal").get())
                        )
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
    public void optionalValueCollectionsClass() throws Exception {
        assertThat(
                this.classes.get("org.generated.optional.OptionalValueList").get(),
                is(aPublic().class_()
                        .withParameter(variableType().named("E"))
                        .withParameter(variableType().named("O"))
                        .with(aPublic().constructor()
                                .withParameters(
                                        classType(this.classes.get("org.generated.ValueList").get()),
                                        genericType().baseClass(Function.class)
                                                .withParameters(typeParameter().named("E"), typeParameter().named("O"))
                                )
                        )
                )
        );
        assertThat(
                this.classes.get("org.generated.optional.OptionalValueSet").get(),
                is(aPublic().class_().withParameter(variableType().named("E"))
                        .with(aPublic().constructor().withParameters(this.classes.get("org.generated.ValueSet").get()))
                )
        );
    }

    @Test
    public void optionalListOptionalMethods() throws Exception {
        assertThat(
                this.classes.get("org.generated.optional.OptionalValueList").get(),
                is(aPublic().class_()
                                .with(aPublic().method()
                                        .named("get")
                                        .returning(this.classes.get("org.generated.ValueList").get())
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
                                                .withParameters(typeParameter().aClass(this.classes.get("org.generated.ValueList").get()))
                                        )
                                )
                                .with(aPublic().method()
                                        .named("filter")
                                        .returning(genericType()
                                                .baseClass(Optional.class)
                                                .withParameters(typeParameter().aClass(this.classes.get("org.generated.ValueList").get()))
                                        )
                                        .withParameters(genericType()
                                                .baseClass(Predicate.class)
                                                .withParameters(typeParameter().aClass(this.classes.get("org.generated.ValueList").get()))
                                        )
                                )
                                .with(aPublic().method()
                                        .named("map")
                                        .withVariable(variableType().named("U"))
                                        .returning(genericType().baseClass(Optional.class).withParameters(typeParameter().named("U")))
                                        .withParameters(
                                                genericType().baseClass(Function.class).withParameters(
                                                        typeParameter().aClass(this.classes.get("org.generated.ValueList").get()),
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
                                                        typeParameter().aClass(this.classes.get("org.generated.ValueList").get()),
                                                        typeParameter().aType(
                                                                genericType().baseClass(Optional.class)
                                                                        .withParameters(typeParameter().named("U"))
                                                        )
                                                )
                                        )
                                )
                                .with(aPublic().method()
                                        .named("orElse")
                                        .returning(this.classes.get("org.generated.ValueList").get())
                                        .withParameters(this.classes.get("org.generated.ValueList").get())
                                )
                                .with(aPublic().method()
                                        .named("orElseGet")
                                        .returning(this.classes.get("org.generated.ValueList").get())
                                        .withParameters(genericType().baseClass(Supplier.class)
                                                .withParameters(typeParameter().aClass(this.classes.get("org.generated.ValueList").get()))
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
                                                .returning(this.classes.get("org.generated.ValueList").get())
                                                .withParameters(genericType().baseClass(Supplier.class)
//                                        .withParameters(typeParameter().wildcard().upperBound(variableType().named("X")))
                                                )
//                                .throwing(variableType().named("X"))
                                )
                )
        );
    }

    @Test
    public void optionalValueListElementAccessor() throws Exception {
        assertThat(
                this.classes.get("org.generated.optional.OptionalValueList").get(),
                is(aPublic().class_()
                        .with(aPublic().method()
                                .named("get")
                                .withParameters(int.class)
                                .returning(variableType().named("O"))
                        )
                )
        );
    }

    @Test
    public void optionalSetOptionalMethods() throws Exception {
        assertThat(
                this.classes.get("org.generated.optional.OptionalValueSet").get(),
                is(aPublic().class_()
                                .with(aPublic().method()
                                        .named("get")
                                        .returning(this.classes.get("org.generated.ValueSet").get())
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
                                                .withParameters(typeParameter().aClass(this.classes.get("org.generated.ValueSet").get()))
                                        )
                                )
                                .with(aPublic().method()
                                        .named("filter")
                                        .returning(genericType()
                                                .baseClass(Optional.class)
                                                .withParameters(typeParameter().aClass(this.classes.get("org.generated.ValueSet").get()))
                                        )
                                        .withParameters(genericType()
                                                .baseClass(Predicate.class)
                                                .withParameters(typeParameter().aClass(this.classes.get("org.generated.ValueSet").get()))
                                        )
                                )
                                .with(aPublic().method()
                                        .named("map")
                                        .withVariable(variableType().named("U"))
                                        .returning(genericType().baseClass(Optional.class).withParameters(typeParameter().named("U")))
                                        .withParameters(
                                                genericType().baseClass(Function.class).withParameters(
                                                        typeParameter().aClass(this.classes.get("org.generated.ValueSet").get()),
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
                                                        typeParameter().aClass(this.classes.get("org.generated.ValueSet").get()),
                                                        typeParameter().aType(
                                                                genericType().baseClass(Optional.class)
                                                                        .withParameters(typeParameter().named("U"))
                                                        )
                                                )
                                        )
                                )
                                .with(aPublic().method()
                                        .named("orElse")
                                        .returning(this.classes.get("org.generated.ValueSet").get())
                                        .withParameters(this.classes.get("org.generated.ValueSet").get())
                                )
                                .with(aPublic().method()
                                        .named("orElseGet")
                                        .returning(this.classes.get("org.generated.ValueSet").get())
                                        .withParameters(genericType().baseClass(Supplier.class)
                                                .withParameters(typeParameter().aClass(this.classes.get("org.generated.ValueSet").get()))
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
                                                .returning(this.classes.get("org.generated.ValueSet").get())
                                                .withParameters(genericType().baseClass(Supplier.class)
//                                        .withParameters(typeParameter().wildcard().upperBound(variableType().named("X")))
                                                )
//                                .throwing(variableType().named("X"))
                                )
                )
        );
    }

    @Test
    public void collectionMethodsAndFields() throws Exception {
        assertThat(
                this.classes.get("org.generated.val.optional.OptionalContainer").get(),
                is(aPublic().class_()
                        .with(aPublic().method()
                                .named("listProp")
                                .returning(genericType()
                                        .baseClass(this.classes.get("org.generated.optional.OptionalValueList").get())
                                        .withParameters(typeParameter().aClass(String.class)))
                        )
                        .with(aPrivate().field().final_()
                                .named("listProp")
                                .withType(genericType()
                                        .baseClass(this.classes.get("org.generated.optional.OptionalValueList").get())
                                        .withParameters(typeParameter().aClass(String.class))
                                )
                        )
                )
        );
        assertThat(
                this.classes.get("org.generated.val.optional.OptionalContainer").get(),
                is(aPublic().class_()
                        .with(aPublic().method()
                                .named("setProp")
                                .returning(genericType()
                                        .baseClass(this.classes.get("org.generated.optional.OptionalValueSet").get())
                                        .withParameters(typeParameter().aClass(String.class)))
                        )
                        .with(aPrivate().field().final_()
                                .named("setProp")
                                .withType(genericType()
                                        .baseClass(this.classes.get("org.generated.optional.OptionalValueSet").get())
                                        .withParameters(typeParameter().aClass(String.class))
                                )
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

        ObjectHelper opt = val.as("org.generated.Val").call("opt");
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

    @Test
    public void collectionProperty() throws Exception {
        ObjectHelper container = this.classes.get("org.generated.val.Container").call("builder")
                .call("listProp", String[].class)
                    .with(new Object [] {new String [] {"A", "B"}})
                .call("build");
        ObjectHelper opt = container.as("org.generated.val.Container").call("opt");

        assertThat(opt.call("listProp").call("isPresent").get(), is(true));
        assertThat(opt.call("listProp").call("get").call("toString").get(), is("[A, B]"));

        assertThat(opt.call("listProp").call("get", int.class).with(0).call("isPresent").get(), is(true));
        assertThat(opt.call("listProp").call("get", int.class).with(0).call("get").get(), is("A"));
        assertThat(opt.call("listProp").call("get", int.class).with(1).call("isPresent").get(), is(true));
        assertThat(opt.call("listProp").call("get", int.class).with(2).call("isPresent").get(), is(false));

        assertThat(opt.call("setProp").call("isPresent").get(), is(false));
    }
}