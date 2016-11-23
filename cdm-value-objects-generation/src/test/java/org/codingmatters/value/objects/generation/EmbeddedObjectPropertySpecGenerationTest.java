package org.codingmatters.value.objects.generation;

import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.codingmatters.tests.reflect.ReflectMatchers.*;
import static org.codingmatters.value.objects.spec.AnonymousValueSpec.anonymousValueSpec;
import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 11/22/16.
 */
@Ignore
public class EmbeddedObjectPropertySpecGenerationTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    private final Spec spec  = spec()
            .addValue(valueSpec().name("val")
                    .addProperty(property().name("p").type(type().typeKind(TypeKind.EMBEDDED)
                            .embeddedValueSpec(anonymousValueSpec()
                                    .addProperty(property().name("p1").type(type().typeKind(TypeKind.JAVA_TYPE).typeRef(String.class.getName())))
                                    .addProperty(property().name("p2").type(type().typeKind(TypeKind.EMBEDDED)
                                            .embeddedValueSpec(anonymousValueSpec()
                                                    .addProperty(property().name("p3").type(type().typeKind(TypeKind.JAVA_TYPE).typeRef(String.class.getName())).build())
                                                    .build()
                                            )
                                    ))
                                    .build()
                            )
                    ))
            )
            .build();
    private CompiledCode compiled;

    @Before
    public void setUp() throws Exception {
        new SpecCodeGenerator(this.spec, "org.generated", dir.getRoot()).generate();
        this.compiled = CompiledCode.compile(this.dir.getRoot());
    }

    @Test
    public void oneValueInterfacePerEmbeddedObjectProperty() throws Exception {
        assertThat(compiled.getClass("org.generated.val.P"), is(aPublic().interface_()));
        assertThat(compiled.getClass("org.generated.val.p.P2"), is(aPublic().interface_()));
    }

    @Test
    public void embeddedObjectIsAValueObject() throws Exception {
        assertThat(compiled.getClass("org.generated.val.P"), is(aPublic().interface_()));
        assertThat(compiled.getClass("org.generated.val.PImpl"), is(aPackagePrivate().class_().implementing(compiled.getClass("org.generated.val.P"))));
        assertThat(compiled.getClass("org.generated.val.P$Builder"), is(aStatic().public_().class_().with(anInstance().method().named("build").returning(compiled.getClass("org.generated.val.P")))));
    }

    @Test
    public void embeddedObjectPropertyMethods() throws Exception {
        assertThat(
                compiled.getClass("org.generated.Val"),
                is(aPublic().interface_()
                        .with(aPublic().method().named("p").returning(compiled.getClass("org.generated.val.P")))
                )
        );
        assertThat(
                compiled.getClass("org.generated.Val$Builder"),
                is(aPublic().interface_()
                        .with(aPublic().method().named("p")
                                .withParameters(compiled.getClass("org.generated.val.P$Builder"))
                                .returning(compiled.getClass("org.generated.Val$Builder")))
                )
        );
    }
}
