<?php

namespace Test;

use PHPUnit\Framework\TestCase;

use org\generated\ComplexType;
use org\generated\json\ComplexTypeReader;
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

    public function testReader(){
        $complex = new ComplexType();

        $complexProp = new ComplexProps();
        $complexProp -> withStringProp( "toto" )
            -> withIntList( new ComplexPropsIntListList( array( 7, 9, 20 ) ) );

        $complex -> withComplexProps( $complexProp )
            -> withTestIsOk( true )
            ->withFoo( 74.9 );

        $content = json_encode( $complex );

        $reader = new ComplexTypeReader();
        $object = $reader->read( $content );

        $this -> assertSame( $object -> complexProps() -> stringProp(), 'toto' );
        $this -> assertSame( $object -> complexProps() -> intList()[0], 7 );
        $this -> assertSame( $object -> complexProps() -> intList()[1], 9 );
        $this -> assertSame( $object -> complexProps() -> intList()[2], 20 );
        $this -> assertSame( $object -> testIsOk(), true );
        $this -> assertSame( $object -> foo(), 74.9 );
    }
}
