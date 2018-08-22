<?php

namespace Test;

use PHPUnit\Framework\TestCase;

use io\flexio\utils\FlexDate;
use org\generated\ValueObjectProps;
use org\generated\json\ValueObjectPropsReader;
use org\generated\valueobjectprops\ValueObjectPropsPropListList;
use org\generated\PrimitiveProps;

class ExternalValueObjectTest extends TestCase {

    public function testValueObjectTypedProperties() {
        $valueObjectProps = new ValueObjectProps();

        $object = new PrimitiveProps();
        $object -> withStringProp( "toto" );

        $object2 = new PrimitiveProps();
        $object2 -> withStringProp( "tutu" );

        $valueObjectProps->withProp( $object )
            -> withPropList( new ValueObjectPropsPropListList( array( $object )));

        $valueObjectProps->propList()[] = $object2;

        $this -> assertSame( $valueObjectProps->prop()->stringProp(), 'toto' );
        $this -> assertSame( $valueObjectProps->propList()[0]->stringProp(), 'toto' );
        $this -> assertSame( $valueObjectProps->propList()[1]->stringProp(), 'tutu' );
    }

    public function testReader(){
        $valueObjectProps = new ValueObjectProps();

        $object = new PrimitiveProps();
        $object -> withStringProp( "toto" );

        $object2 = new PrimitiveProps();
        $object2 -> withStringProp( "tutu" );

        $valueObjectProps->withProp( $object )
                -> withPropList( new ValueObjectPropsPropListList( array( $object, $object2 )));

        $content = json_encode( $valueObjectProps, true );

        $reader = new ValueObjectPropsReader();
        $parsed = $reader->read( $content );

        $this->assertSame( "toto", $parsed->prop()->stringProp() );
        $this->assertSame( "toto", $parsed->propList()[0]->stringProp() );
        $this->assertSame( "tutu", $parsed->propList()[1]->stringProp() );
    }

}