package org.codingmatters.value.objects.demo;

import org.junit.Test;

import java.time.DayOfWeek;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FromToMapTest {
    @Test
    public void enums() throws Exception {
        ValueWithEnums value = ValueWithEnums.builder()
                .inlineEnum(ValueWithEnums.InlineEnum.A)
                .elswhereEnum(DayOfWeek.MONDAY)
                .build();

        System.out.println(value.toMap());

        assertThat(ValueWithEnums.fromMap(value.toMap()).build(), is(value));
    }
}
