package org.codingmatters.value.objects.js;

import org.codingmatters.value.objects.js.generator.NamingUtility;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class NamingUtilityTest {

    @Test
    public void testFindPackageNameForInSpecObjectValue() throws Exception {
        assertThat( NamingUtility.findPackage( "org.generated", "org.generated.complextype.ComplexProp" ), is( "./complextype/ComplexProp" ) );
        assertThat( NamingUtility.findPackage( "org.generated", "org.toto.ComplexProp" ), is( "../toto/ComplexProp" ) );
        assertThat( NamingUtility.findPackage( "org.generated", "org.toto.titi.ComplexProp" ), is( "../toto/titi/ComplexProp" ) );
        assertThat( NamingUtility.findPackage( "org.generated.tata", "org.toto.titi.ComplexProp" ), is( "../../toto/titi/ComplexProp" ) );
        assertThat( NamingUtility.findPackage( "org.toto", "titi.ComplexProp" ), is( "../../titi/ComplexProp" ) );
        assertThat( NamingUtility.findPackage( "org.toto", "org.toto.ComplexProp" ), is( "./ComplexProp" ) );
    }

}
