package org.codingmatters.value.objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.PropertyType;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.ValueSpec;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;

/**
 * Created by nelt on 9/3/16.
 */
public class SpecReader {

    static private final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

    public Spec read(InputStream in) throws IOException {
        ObjectMapper mapper = getMapper();

        Spec.Builder spec = spec();

        Map<String,?> root = mapper.readValue(in, Map.class);

        for (String valueName : root.keySet()) {
            ValueSpec.Builder value = valueSpec().name(valueName);
            Map<String, ?> properties = (Map<String, ?>) root.get(valueName);
            for (String propertyName : properties.keySet()) {
                value.addProperty(property().name(propertyName).type(PropertyType.valueOf(((String)properties.get(propertyName)).toUpperCase())));
            }

            spec.addValue(value);
        }


        return spec.build();
    }

    private ObjectMapper getMapper() {
        return MAPPER;
    }
}
