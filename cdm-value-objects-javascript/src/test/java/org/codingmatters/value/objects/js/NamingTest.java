package org.codingmatters.value.objects.js;

import org.codingmatters.value.objects.js.generator.Naming;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class NamingTest {

    @Test
    public void testFindPackageNameForInSpecObjectValue() throws Exception {
        assertThat( Naming.findPackage( "org.generated", "org.generated.complextype.ComplexProp" ), is( "./complextype/ComplexProp" ) );
        assertThat( Naming.findPackage( "org.generated", "org.toto.ComplexProp" ), is( "../toto/ComplexProp" ) );
        assertThat( Naming.findPackage( "org.generated", "org.toto.titi.ComplexProp" ), is( "../toto/titi/ComplexProp" ) );
        assertThat( Naming.findPackage( "org.generated.tata", "org.toto.titi.ComplexProp" ), is( "../../toto/titi/ComplexProp" ) );
        assertThat( Naming.findPackage( "org.toto", "titi.ComplexProp" ), is( "../../titi/ComplexProp" ) );
        assertThat( Naming.findPackage( "org.toto", "org.toto.ComplexProp" ), is( "./ComplexProp" ) );
    }

}
