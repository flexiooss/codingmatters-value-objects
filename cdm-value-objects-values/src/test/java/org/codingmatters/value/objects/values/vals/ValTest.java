package org.codingmatters.value.objects.values.vals;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class ValTest {

    @Test
    public void array() throws Exception {
        assertThat(
                Val.array().values(Val.stringValue("hello"), Val.longValue(12L)).build().toString(),
                is("[hello (STRING), 12 (LONG)]")
        );
    }

    @Test
    public void array_equals() throws Exception {
        assertThat(
                Val.array().values(Val.stringValue("hello"), Val.longValue(12L)).build(),
                is(Val.array().values(Val.stringValue("hello"), Val.longValue(12L)).build())
        );
        assertThat(
                Val.array().values(Val.stringValue("hello"), Val.longValue(12L)).build(),
                is(not(Val.array().values(Val.stringValue("hello you"), Val.longValue(12L)).build()))
        );
    }

    @Test
    public void nestedArray() throws Exception {
        assertThat(
                Val.array().values(
                        Val.array().values(Val.stringValue("a"), Val.stringValue("b")).build(),
                        Val.array().values(Val.stringValue("c"), Val.stringValue("d")).build()
                ).build().toString(),
                is("[[a (STRING), b (STRING)], [c (STRING), d (STRING)]]")
        );
    }

    @Test
    public void nestedArray_equals() throws Exception {
        assertThat(
                Val.array().values(
                        Val.array().values(Val.stringValue("a"), Val.stringValue("b")).build(),
                        Val.array().values(Val.stringValue("c"), Val.stringValue("d")).build()
                ).build(),
                is(
                    Val.array().values(
                            Val.array().values(Val.stringValue("a"), Val.stringValue("b")).build(),
                            Val.array().values(Val.stringValue("c"), Val.stringValue("d")).build()
                    ).build()
                )
        );
        assertThat(
                Val.array().values(
                        Val.array().values(Val.stringValue("a"), Val.stringValue("b")).build(),
                        Val.array().values(Val.stringValue("c"), Val.stringValue("d")).build()
                ).build(),
                is(not(
                    Val.array().values(
                            Val.array().values(Val.stringValue("c"), Val.stringValue("d")).build(),
                            Val.array().values(Val.stringValue("a"), Val.stringValue("b")).build()
                    ).build()
                ))
        );
    }

    @Test
    public void mixedArray() throws Exception {
        assertThat(
                Val.array()
                        .with(Val.longValue(12L))
                        .with(Val.object().property("p", Val.booleanValue(true)).build())
                        .with(Val.array().values(Val.longValue(42L), Val.stringValue("the answer")).build())
                        .build().toString(),
                is("[12 (LONG), {p=true (BOOLEAN)}, [42 (LONG), the answer (STRING)]]")
        );
    }

    @Test
    public void mixedArray_equals() throws Exception {
        assertThat(
                Val.array()
                        .with(Val.longValue(12L))
                        .with(Val.object().property("p", Val.booleanValue(true)).build())
                        .with(Val.array().values(Val.longValue(42L), Val.stringValue("the answer")).build())
                        .build(),
                is(
                    Val.array()
                            .with(Val.longValue(12L))
                            .with(Val.object().property("p", Val.booleanValue(true)).build())
                            .with(Val.array().values(Val.longValue(42L), Val.stringValue("the answer")).build())
                            .build()
                )
        );
        assertThat(
                Val.array()
                        .with(Val.longValue(12L))
                        .with(Val.object().property("p", Val.booleanValue(true)).build())
                        .with(Val.array().values(Val.longValue(42L), Val.stringValue("the answer")).build())
                        .build(),
                is(not(
                    Val.array()
                            .with(Val.longValue(12L))
                            .with(Val.object().property("p", Val.booleanValue(true)).build())
                            .with(Val.array().values(Val.longValue(12L), Val.stringValue("the answer")).build())
                            .build()
                ))
        );
    }

    @Test
    public void object() throws Exception {
        assertThat(
                Val.object()
                        .property("p1", Val.stringValue("v1"))
                        .property("p2", Val.stringValue("v2")).build().toString(),
                is("{p1=v1 (STRING), p2=v2 (STRING)}")
        );
    }

    @Test
    public void object_equals() throws Exception {
        assertThat(
                Val.object().property("p1", Val.stringValue("v1")).build(),
                is(Val.object().property("p1", Val.stringValue("v1")).build())
        );
        assertThat(
                Val.object().property("p1", Val.stringValue("v1")).build(),
                is(not(Val.object().property("p1", Val.stringValue("v2")).build()))
        );
        assertThat(
                Val.object().property("p1", Val.stringValue("v1")).build(),
                is(not(Val.object().property("p2", Val.stringValue("v1")).build()))
        );
        assertThat(
                Val.object().property("p1", Val.stringValue("v1")).property("p2", Val.stringValue("v2")).build(),
                is(not(Val.object().property("p1", Val.stringValue("v1")).build()))
        );
    }
}