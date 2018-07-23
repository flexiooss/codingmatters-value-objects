<?php

namespace Test;

use PHPUnit\Framework\TestCase;

use org\generated\InSpecEnumProperties;
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

}