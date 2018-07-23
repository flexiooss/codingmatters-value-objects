<?php

namespace Test;

use PHPUnit\Framework\TestCase;

use org\generated\PrimitiveProps;
use io\flexio\utils\FlexDate;


class EmptyObjectTest extends TestCase {

    public function testEmptyObjectInit(){

        $primitiveProps = new PrimitiveProps();
        $primitiveProps -> withStringProp( "str" )
            -> withBytesProp( "bytes" )
            -> withIntegerProp( 9 )
            -> withLongProp( 7 )
            -> withFloatProp( 9.7 )
            -> withDoubleProp( 7.9 )
            -> withBooleanProp( true )
            -> withDateProp( FlexDate::newDate('2011-08-01') )
            -> withTimeProp( FlexDate::newTime('2011-10-01T15:05:01.012345Z') )
            -> withDateTimeProp( FlexDate::newDateTime('2011-09-01T15:04:01') )
            -> withTzDateTimeProp( FlexDate::newTzDateTime('2011-09-01T15:04:01+01:00') );

        $this -> assertSame( $primitiveProps -> stringProp(), "str" );
        $this -> assertSame( $primitiveProps -> bytesProp(), "bytes" );
        $this -> assertSame( $primitiveProps -> integerProp(), 9 );
        $this -> assertSame( $primitiveProps -> longProp(), 7 );
        $this -> assertSame( $primitiveProps -> floatProp(), 9.7 );
        $this -> assertSame( $primitiveProps -> doubleProp(), 7.9 );
        $this -> assertSame( $primitiveProps -> booleanProp(), true );
        $this -> assertSame( $primitiveProps -> dateProp()->jsonSerialize(), '2011-08-01' );
        $this -> assertSame( $primitiveProps -> timeProp()->jsonSerialize(), '15:05:01' );
        $this -> assertSame( $primitiveProps -> dateTimeProp()->jsonSerialize(), '2011-09-01T15:04:01' );
        $this -> assertSame( $primitiveProps -> tzDateTimeProp()->jsonSerialize(), '2011-09-01T15:04:01+01:00' );

    }

}