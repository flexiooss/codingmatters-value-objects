package org.codingmatters.value.objects.js;

import org.codingmatters.value.objects.js.parser.SpecReaderJs;
import org.codingmatters.value.objects.spec.Spec;
import org.junit.Test;

import java.io.FileInputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SpecReaderJsTest {

    @Test
    public void testEmptyObject() throws Exception {
        Spec spec = loadSpec( getYaml( "01_emptyObject.yaml" ) );
        assertThat( spec.valueSpecs().size(), is( 1 ) );
        assertThat( spec.valueSpecs().get( 0 ).name(), is( "emptyObject" ) );
        assertThat( spec.valueSpecs().get( 0 ).propertySpecs().size(), is( 0 ) );
    }

    @Test
    public void testSimpleObjectPrimitiveProps() throws Exception {
        Spec spec = loadSpec( getYaml( "02_simpleObjectWithPrimitiveProperties.yaml" ) );
        assertThat( spec.valueSpecs().size(), is( 1 ) );
        assertThat( spec.valueSpecs().get( 0 ).name(), is( "primitiveProps" ) );
    }

    @Test
    public void whenName_then() throws Exception {
        Spec spec = loadSpec( getYaml( "05_objectWithInSpecEnum.yaml" ) );
        assertThat( spec.valueSpecs().size(), is( 1 ) );
    }

    static private Spec loadSpec( String resource ) {
        try {
            return new SpecReaderJs().read( new FileInputStream( resource ) );
        } catch( Exception e ) {
            throw new RuntimeException( "error loading spec", e );
        }
    }

    private String getYaml( String fileName ) {
        return Thread.currentThread().getContextClassLoader().getResource( fileName ).getPath();
    }

}
