package org.codingmatters.value.objects.values.helper;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ObjectValueToMapTest {
    @Test
    public void givenOV_WithNestedMultipleProperty_WithNullElement__WhenToMap__ThenNullInMap() throws PropertyValue.Type.UnsupportedTypeException {
        final PropertyValue faultyPropertyValue = PropertyValue.fromObject(new Object[]{null});
        final ObjectValue value = ObjectValue.builder()
                .property("dashboard_variables", vars -> vars.objectValue(ObjectValue.builder()
                        .property("start-date", date -> date.dateValue(LocalDate.of(2021, 2, 22)))
                        .property("start-hour", hour -> hour.timeValue(LocalTime.of(7, 30)))
                        .property("faulty-property", faultyPropertyValue)
                        .build()
                ))
                .build();

        final Map<String, Object> valueMap = value.toMap();

        final Map<String, Object> variablesMap = new HashMap<>();
        variablesMap.put("start-date", LocalDate.of(2021, 2, 22));
        variablesMap.put("start-hour", LocalTime.of(7, 30));
        variablesMap.put("faulty-property", Collections.singletonList(null));

        final Map<String, Object> expectedValueMap = new HashMap<>();
        expectedValueMap.put("dashboard_variables", variablesMap);


        assertThat(valueMap, is(expectedValueMap));
    }

}