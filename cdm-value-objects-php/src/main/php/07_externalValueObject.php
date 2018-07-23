<?php

namespace Test;

use PHPUnit\Framework\TestCase;

use io\flexio\utils\FlexDate;
use org\generated\ValueObjectProps;
use org\generated\valueobjectprops\ValueObjectPropsPropListList;

class ExternalValueObjectTest extends TestCase {

    public function testValueObjectTypedProperties() {
        $object = new ValueObjectProps();

        $flexDate = FlexDate::newDate( '2018-05-20' );
        $flexDate2 = FlexDate::newDate( '2018-07-23' );

        $object->withProp( $flexDate )
            -> withPropList( new ValueObjectPropsPropListList( array( $flexDate )));

        $object->propList()[] = $flexDate2;

        $this -> assertSame( $object->prop()->jsonSerialize(), '2018-05-20' );
        $this -> assertSame( $object->propList()[0]->jsonSerialize(), '2018-05-20' );
        $this -> assertSame( $object->propList()[1]->jsonSerialize(), '2018-07-23' );
    }

}