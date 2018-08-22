<?php

namespace Test;

use PHPUnit\Framework\TestCase;

use org\generated\InSpecEnumProperties;
use org\generated\json\InSpecEnumPropertiesReader;
use org\generated\inspecenumproperties\InSpecEnumPropertiesSingle;
use org\generated\inspecenumproperties\InSpecEnumPropertiesMultipleList;
use org\generated\inspecenumproperties\InSpecEnumPropertiesMultiple;

class InSpecEnumerationTest extends TestCase {

    public function testInSpecEnum(){
        $object = new InSpecEnumProperties();

        $object -> withSingle( InSpecEnumPropertiesSingle::SA() )
            -> withMultiple( new InSpecEnumPropertiesMultipleList(
                array( InSpecEnumPropertiesMultiple::MA(), InSpecEnumPropertiesMultiple::MC() )
         ));

        $this -> assertSame( $object->single()->value(), InSpecEnumPropertiesSingle::SA()->value() );
        $this -> assertSame( $object->single()->value(), 'SA' );
        $this -> assertSame( $object->multiple()[0]->value(), InSpecEnumPropertiesMultiple::MA()->value() );
        $this -> assertSame( $object->multiple()[1]->value(), InSpecEnumPropertiesMultiple::MC()->value() );
    }

    public function testReader() {
        $inSpec = new InSpecEnumProperties();
        $inSpec -> withSingle( InSpecEnumPropertiesSingle::SA() )
                -> withMultiple( new InSpecEnumPropertiesMultipleList( array( InSpecEnumPropertiesMultiple::MA(), InSpecEnumPropertiesMultiple::MC() )
        ));

        $content = json_encode( $inSpec, true );

        $reader = new InSpecEnumPropertiesReader();
        $object = $reader->read( $content );
        $this -> assertSame( 'SA', $object->single()->value() );
        $this -> assertSame( 'MA', $object->multiple()[0]->value() );
        $this -> assertSame( 'MC', $object->multiple()[1]->value() );
    }

}