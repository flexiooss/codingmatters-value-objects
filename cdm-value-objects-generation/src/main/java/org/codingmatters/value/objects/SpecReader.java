package org.codingmatters.value.objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.codingmatters.value.objects.exception.SpecSyntaxException;
import org.codingmatters.value.objects.reader.ContextSpecParser;
import org.codingmatters.value.objects.spec.Spec;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by nelt on 9/3/16.
 */
public class SpecReader {

    static private final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

    public Spec read(InputStream in) throws IOException, SpecSyntaxException {
        ObjectMapper mapper = getMapper();


        Map<String,?> root = mapper.readValue(in, Map.class);

        return new ContextSpecParser(root).parse();
    }


    private ObjectMapper getMapper() {
        return MAPPER;
    }

}
