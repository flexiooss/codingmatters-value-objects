package org.codingmatters.value.objects.values.matchers;

import org.codingmatters.value.objects.generated.ValueList;
import org.codingmatters.value.objects.generated.ValueObject;
import org.codingmatters.value.objects.generated.ValueObjectWrapper;
import org.codingmatters.value.objects.generated.valueobject.ReferencesList;
import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.List;

import static org.codingmatters.value.objects.values.matchers.ContainsValueObjectMatcher.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ContainsValueObjectMatcherTest {
    @Test
    public void objectValueContainsItself() {
        final ObjectValue actual = ObjectValue.builder()
                .property("anotherOne", v -> v.stringValue("bite the dust"))
                .property("smoothness", v -> v.longValue(100L))
                .build();

        final Matcher<ObjectValue> containsAnotherObjectValue = containsAnotherObjectValue(
                ObjectValue.builder()
                        .property("smoothness", v -> v.longValue(100L))
                        .property("anotherOne", v -> v.stringValue("bite the dust"))
                        .build()
        );

        assertThat(actual, containsAnotherObjectValue);
    }

    @Test
    public void objectValueContainsASubpart() {
        final ObjectValue actual = ObjectValue.builder()
                .property("anotherOne", v -> v.stringValue("bite the dust"))
                .property("smoothness", v -> v.longValue(100L))
                .build();

        final Matcher<ObjectValue> containsAnotherObjectValue = containsAnotherObjectValue(
                ObjectValue.builder()
                        .property("smoothness", v -> v.longValue(100L))
                        .build()
        );

        assertThat(actual, containsAnotherObjectValue);
    }

    @Test
    public void objectValueContainsNull__DoNotMatch() {
        final ObjectValue actual = null;

        final Matcher<ObjectValue> expected = containsAnotherObjectValue(
                ObjectValue.builder()
                        .property("smoothness", v -> v.longValue(100L))
                        .build()
        );

        assertThat(expected.matches(actual), is(false));
    }

    @Test
    public void objectValue__ContainsValueObject__ThatMatch() {
        final ValueObject valueObject = ValueObject.builder()
                .complex(c -> c.real(2d).imaginary(5d))
                .mathematicians("Taylor", "Cauchy", "Conway")
                .referencesList(List.of(
                        ReferencesList.builder().isbn("979-8566401997").build()
                ))
                .smoothness(100L)
                .build();

        final ObjectValue objectValue = ObjectValue.builder()
                .property("smoothness", s -> s.longValue(100L))
                .property("complex", c -> c.objectValue(o -> o
                        .property("real", r -> r.doubleValue(2d))
                        .property("imaginary", i -> i.doubleValue(5d))
                ))
                .property("mathematicians", PropertyValue.multiple(PropertyValue.Type.STRING,
                        m -> m.stringValue("Taylor"),
                        m -> m.stringValue("Cauchy"),
                        m -> m.stringValue("Conway")
                ))
                .property("referencesList", PropertyValue.multiple(PropertyValue.Type.OBJECT,
                        rl -> rl.objectValue(r -> r
                                .property("isbn", i -> i.stringValue("979-8566401997"))
                        )
                ))
                .build();

        assertThat(objectValue, containsValueObject(valueObject));
    }

    @Test(expected = AssertionError.class)
    public void objectValue__ContainsInvalidValueObject__ThrowsAssertionError() {
        final Double valueObject = 8d;

        final ObjectValue objectValue = ObjectValue.builder()
                .property("smoothness", s -> s.longValue(100L))
                .property("complex", c -> c.objectValue(o -> o
                        .property("real", r -> r.doubleValue(2d))
                        .property("imaginary", i -> i.doubleValue(5d))
                ))
                .property("mathematicians", PropertyValue.multiple(PropertyValue.Type.STRING,
                        m -> m.stringValue("Taylor"),
                        m -> m.stringValue("Cauchy"),
                        m -> m.stringValue("Conway")
                ))
                .property("referencesList", PropertyValue.multiple(PropertyValue.Type.OBJECT,
                        rl -> rl.objectValue(r -> r
                                .property("isbn", i -> i.stringValue("979-8566401997"))
                        )
                ))
                .build();

        assertThat(objectValue, containsValueObject(valueObject));
    }


    @Test
    public void nullObjectValue__ContainsValueObject__ThatMatch() {
        final ValueObject valueObject = ValueObject.builder()
                .complex(c -> c.real(2d).imaginary(5d))
                .mathematicians("Taylor", "Cauchy", "Conway")
                .referencesList(List.of(
                        ReferencesList.builder().isbn("979-8566401997").build()
                ))
                .smoothness(100L)
                .build();

        final ObjectValue objectValue = null;

        assertThat(containsValueObject(valueObject).matches(null), is(false));
    }

    @Test
    public void valueObject__ContainsAnotherValueObject__ThatMatch() {
        final ValueObject valueObject = ValueObject.builder()
                .complex(c -> c.real(2d).imaginary(5d))
                .mathematicians("Taylor", "Cauchy", "Conway")
                .referencesList(List.of(
                        ReferencesList.builder().isbn("979-8566401997").build()
                ))
                .smoothness(100L)
                .build();

        final ValueObjectWrapper valueObjectWrapper = ValueObjectWrapper.builder()
                .wrapperAnnotation("This object works like an inherited ValueObject")
                .complex(c -> c.real(2d).imaginary(5d))
                .mathematicians("Taylor", "Cauchy", "Conway")
                .referencesList(ValueList.<org.codingmatters.value.objects.generated.valueobjectwrapper.ReferencesList>builder().with(
                        org.codingmatters.value.objects.generated.valueobjectwrapper.ReferencesList.builder().isbn("979-8566401997").build()
                ).build())
                .smoothness(100L)
                .build();

        assertThat(valueObjectWrapper, containsAnotherValueObject(valueObject));
    }

    @Test
    public void nullValueObject__ContainsAnotherValueObject__DoNotMatch() {
        final ValueObject valueObject = ValueObject.builder()
                .complex(c -> c.real(2d).imaginary(5d))
                .mathematicians("Taylor", "Cauchy", "Conway")
                .referencesList(List.of(
                        ReferencesList.builder().isbn("979-8566401997").build()
                ))
                .smoothness(100L)
                .build();

        final ValueObjectWrapper valueObjectWrapper = null;

        assertThat(containsAnotherValueObject(valueObject).matches(valueObjectWrapper), is(false));
    }

    @Test
    public void valueObject__ContainsAnotherValueObject__ButDoNotMatch() {
        final ValueObject valueObject = ValueObject.builder()
                .complex(c -> c.real(2d).imaginary(5d))
                .mathematicians("Taylor", "Cauchy", "Conway")
                .referencesList(List.of(
                        ReferencesList.builder().isbn("979-8566401997").build()
                ))
                .smoothness(100L)
                .build();

        final ValueObjectWrapper valueObjectWrapper = ValueObjectWrapper.builder()
                .wrapperAnnotation("This object works like an inherited ValueObject")
                .complex(c -> c.real(2d).imaginary(5d))
                .mathematicians("Taylor", "Cauchy", "Conway")
                .referencesList(ValueList.<org.codingmatters.value.objects.generated.valueobjectwrapper.ReferencesList>builder().with(
                        org.codingmatters.value.objects.generated.valueobjectwrapper.ReferencesList.builder().isbn("979-8566401997").build()
                ).build())
                .smoothness(100L)
                .build();

        assertThat(containsAnotherValueObject(valueObjectWrapper).matches(valueObject), is(false));
    }

    @Test
    public void valueObject__ContainsObjectValue__ThatMatch() {
        final ValueObject valueObject = ValueObject.builder()
                .complex(c -> c.real(2d).imaginary(5d))
                .mathematicians("Taylor", "Cauchy", "Conway")
                .referencesList(List.of(
                        ReferencesList.builder().isbn("979-8566401997").build()
                ))
                .smoothness(100L)
                .build();

        final ObjectValue objectValue = ObjectValue.builder()
                .property("smoothness", s -> s.longValue(100L))
                .property("complex", c -> c.objectValue(o -> o
                        .property("real", r -> r.doubleValue(2d))
                        .property("imaginary", i -> i.doubleValue(5d))
                ))
                .property("mathematicians", PropertyValue.multiple(PropertyValue.Type.STRING,
                        m -> m.stringValue("Taylor"),
                        m -> m.stringValue("Cauchy"),
                        m -> m.stringValue("Conway")
                ))
                .property("referencesList", PropertyValue.multiple(PropertyValue.Type.OBJECT,
                        rl -> rl.objectValue(r -> r
                                .property("isbn", i -> i.stringValue("979-8566401997"))
                        )
                ))
                .build();

        assertThat(valueObject, containsObjectValue(objectValue));
    }

    @Test
    public void valueObject__ContainsString__DoNotMatch() {
        final String valueObject = "Y'en a qui ont essayés ...";

        final ObjectValue objectValue = ObjectValue.builder()
                .property("smoothness", s -> s.longValue(100L))
                .property("complex", c -> c.objectValue(o -> o
                        .property("real", r -> r.doubleValue(2d))
                        .property("imaginary", i -> i.doubleValue(5d))
                )).build();

        assertThat(containsObjectValue(objectValue).matches(valueObject), is(false));
    }

    @Test
    public void nullValueObject__ContainsObjectValue__ThatMatch() {
        final ValueObject valueObject = null;

        final ObjectValue objectValue = ObjectValue.builder()
                .property("smoothness", s -> s.longValue(100L))
                .property("complex", c -> c.objectValue(o -> o
                        .property("real", r -> r.doubleValue(2d))
                        .property("imaginary", i -> i.doubleValue(5d))
                ))
                .property("mathematicians", PropertyValue.multiple(PropertyValue.Type.STRING,
                        m -> m.stringValue("Taylor"),
                        m -> m.stringValue("Cauchy"),
                        m -> m.stringValue("Conway")
                ))
                .property("referencesList", PropertyValue.multiple(PropertyValue.Type.OBJECT,
                        rl -> rl.objectValue(r -> r
                                .property("isbn", i -> i.stringValue("979-8566401997"))
                        )
                ))
                .build();

        assertThat(containsObjectValue(objectValue).matches(valueObject), is(false));
    }
}