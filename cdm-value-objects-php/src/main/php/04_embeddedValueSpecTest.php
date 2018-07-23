<?php

namespace Test;

use PHPUnit\Framework\TestCase;

use org\generated\ComplexType;
use org\generated\complextype\ComplexProps;
use org\generated\complextype\complexprops\ComplexPropsIntListList;

class EmbeddedObjectTest extends TestCase {

    public function testComplexType(){
        $complex = new ComplexType();

        $complexProp = new ComplexProps();

        $complexProp -> withStringProp( "toto" )
            -> withIntList( new ComplexPropsIntListList( array( 7, 9, 20 ) ) );

        $complex -> withComplexProps( $complexProp )
            -> withTestIsOk( true )
            ->withFoo( 74.9 );

        $this -> assertSame( $complex->foo(), 74.9 );
        $this -> assertSame( $complex->testIsOk(), true );
        $this -> assertSame( $complex->complexProps()->stringProp(), "toto" );
        $this -> assertSame( $complex->complexProps()->intList()[0], 7 );
        $this -> assertSame( $complex->complexProps()->intList()[1], 9 );
        $this -> assertSame( $complex->complexProps()->intList()[2], 20 );
    }

}
