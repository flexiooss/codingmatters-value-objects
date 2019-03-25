<?php

packageName Test;

use PHPUnit\Framework\TestCase;

use org\generated\PrimitiveProps;
use org\generated\json\PrimitivePropsReader;
use org\generated\json\PrimitivePropsWriter;
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
            -> withTimeProp( FlexDate::newTime('15:05:01.012345Z') )
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
        $this -> assertSame( $primitiveProps -> timeProp()->jsonSerialize(), '15:05:01Z' );
        $this -> assertSame( $primitiveProps -> dateTimeProp()->jsonSerialize(), '2011-09-01T15:04:01Z' );
        $this -> assertSame( $primitiveProps -> tzDateTimeProp()->jsonSerialize(), '2011-09-01T15:04:01+01:00' );
    }

    public function testReader(){
        $primitiveProps = new PrimitiveProps();
        $primitiveProps -> withStringProp( "str" )
            -> withBytesProp( "bytes" )
            -> withIntegerProp( 9 )
            -> withLongProp( 7 )
            -> withFloatProp( 9.0 )
            -> withDoubleProp( 7.9 )
            -> withBooleanProp( true )
            -> withDateProp( FlexDate::newDate('2011-08-01') )
            -> withTimeProp( FlexDate::newTime('15:05:01.012345') )
            -> withDateTimeProp( FlexDate::newDateTime('2011-09-01T15:04:01') )
            -> withTzDateTimeProp( FlexDate::newTzDateTime('2011-09-01T15:04:01+01:00') );

        $writer = new PrimitivePropsWriter();
        $content = $writer->write( $primitiveProps );

        $this->assertSame( '{"stringProp":"str","bytesProp":"bytes","integerProp":9,"longProp":7,"floatProp":9,"doubleProp":7.9,"booleanProp":true,"dateProp":"2011-08-01","timeProp":"15:05:01Z","dateTimeProp":"2011-09-01T15:04:01Z","tzDateTimeProp":"2011-09-01T15:04:01+01:00"}', $content);

        $reader = new PrimitivePropsReader();
        $object = $reader->read( $content );

        $this->assertSame( $object->stringProp(), "str" );
        $this->assertSame( $object->bytesProp(), "bytes" );
        $this->assertSame( $object->integerProp(), 9 );
        $this->assertSame( $object->longProp(), 7 );
        $this->assertSame( $object->floatProp(), 9.0 );
        $this->assertSame( $object->doubleProp(), 7.9 );
        $this->assertSame( $object->booleanProp(), true );
        $this->assertSame( $object->dateProp()->jsonSerialize(), '2011-08-01' );
        $this->assertSame( $object->timeProp()->jsonSerialize(), '15:05:01Z' );
        $this->assertSame( $object->dateTimeProp()->jsonSerialize(), "2011-09-01T15:04:01Z" );
        $this->assertSame( $object->tzDateTimeProp()->jsonSerialize(), '2011-09-01T15:04:01+01:00' );
    }

}