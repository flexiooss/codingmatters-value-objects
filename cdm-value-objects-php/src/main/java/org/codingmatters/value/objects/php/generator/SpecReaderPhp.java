package org.codingmatters.value.objects.php.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.codingmatters.value.objects.exception.LowLevelSyntaxException;
import org.codingmatters.value.objects.spec.Spec;

import java.io.InputStream;
import java.util.Map;

public class SpecReaderPhp {

    public static final ObjectMapper MAPPER = new ObjectMapper( new YAMLFactory() );

    public Spec read( InputStream inputStream ) throws LowLevelSyntaxException {
        Map<String, ?> root = null;
        try {
            root = MAPPER.readValue( inputStream, Map.class );
            return new ContextSpecParserPhp( root ).parse();
        } catch( Exception e ) {
            throw new LowLevelSyntaxException( "Spec must be valid YAML expression", e );
        }
    }


}
