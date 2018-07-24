<?php

namespace Test;

use PHPUnit\Framework\TestCase;
use io\flexio\utils\FlexDate;
use org\generated\ArraySimpleProps;
use org\generated\json\ArraySimplePropsReader;

use org\generated\arraysimpleprops\ArraySimplePropsStringListList;
use org\generated\arraysimpleprops\ArraySimplePropsIntegerListList;
use org\generated\arraysimpleprops\ArraySimplePropsDoubleListList;
use org\generated\arraysimpleprops\ArraySimplePropsFloatListList;
use org\generated\arraysimpleprops\ArraySimplePropsBooleanListList;
use org\generated\arraysimpleprops\ArraySimplePropsLongListList;
use org\generated\arraysimpleprops\ArraySimplePropsDateListList;
use org\generated\arraysimpleprops\ArraySimplePropsDateTimeListList;
use org\generated\arraysimpleprops\ArraySimplePropsTzDateTimeListList;
use org\generated\arraysimpleprops\ArraySimplePropsTimeListList;

use org\generated\arraysimpleprops\ArraySimplePropsStringSetList;
use org\generated\arraysimpleprops\ArraySimplePropsIntegerSetList;
use org\generated\arraysimpleprops\ArraySimplePropsDoubleSetList;
use org\generated\arraysimpleprops\ArraySimplePropsFloatSetList;
use org\generated\arraysimpleprops\ArraySimplePropsBooleanSetList;
use org\generated\arraysimpleprops\ArraySimplePropsLongSetList;
use org\generated\arraysimpleprops\ArraySimplePropsDateSetList;
use org\generated\arraysimpleprops\ArraySimplePropsDateTimeSetList;
use org\generated\arraysimpleprops\ArraySimplePropsTzDateTimeSetList;
use org\generated\arraysimpleprops\ArraySimplePropsTimeSetList;

class EmptyObjectTest extends TestCase {

    public function testStringArray(){
        $arrayProp = new ArraySimpleProps();
        $arrayProp -> withStringList( new ArraySimplePropsStringListList( array( "foo", 7, 9.7, true ) ) );
        $this->assertSame( $arrayProp->stringList()[0], "foo" );
        $this->assertSame( $arrayProp->stringList()[1], "7" );
        $this->assertSame( $arrayProp->stringList()[2], "9.7" );
        $this->assertSame( $arrayProp->stringList()[3], "1" );

        $arrayProp->stringList()[] = "joe";
        $this->assertSame( $arrayProp->stringList()[4], "joe" );
    }

    public function testIntegerArray(){
        $arrayProp = new ArraySimpleProps();
        $arrayProp -> withIntegerList( new ArraySimplePropsIntegerListList( array( 7, 9 ) ) );
        $this->assertSame( $arrayProp->integerList()[0], 7 );
        $arrayProp->integerList()[] = 12;
        $this->assertSame( $arrayProp->integerList()[2], 12 );
    }

    public function testLongArray(){
        $arrayProp = new ArraySimpleProps();
        $arrayProp -> withLongList( new ArraySimplePropsLongListList( array( 7, 9 ) ) );
        $this->assertSame( $arrayProp->longList()[0], 7 );
        $arrayProp->longList()[] = 12;
        $this->assertSame( $arrayProp->longList()[2], 12 );
    }

    public function testFloatArray(){
        $arrayProp = new ArraySimpleProps();
        $arrayProp -> withFloatList( new ArraySimplePropsFloatListList( array( 7, 9.0 ) ) );
        $this->assertSame( $arrayProp->floatList()[0], 7.0 );
        $this->assertSame( $arrayProp->floatList()[1], 9.0 );
        $arrayProp->floatList()[] = 12.2;
        $this->assertSame( $arrayProp->floatList()[2], 12.2 );
    }

    public function testDoubleArray(){
        $arrayProp = new ArraySimpleProps();
        $arrayProp -> withDoubleList( new ArraySimplePropsDoubleListList( array( 7, 9.0 ) ) );
        $this->assertSame( $arrayProp->doubleList()[0], 7.0 );
        $this->assertSame( $arrayProp->doubleList()[1], 9.0 );
        $arrayProp->doubleList()[] = 12.2;
        $this->assertSame( $arrayProp->doubleList()[2], 12.2 );
    }

    public function testBoolArray(){
        $arrayProp = new ArraySimpleProps();
        $arrayProp -> withBooleanList( new ArraySimplePropsBooleanListList( array( true, false ) ) );
        $this->assertSame( $arrayProp->booleanList()[0], true );
        $this->assertSame( $arrayProp->booleanList()[1], false );
        $arrayProp->booleanList()[] = false;
        $this->assertSame( $arrayProp->booleanList()[2], false );
    }

    public function testTimeArray(){
        $arrayProp = new ArraySimpleProps();

        $date1 = FlexDate::newTime( '15:07:20' );
        $date2 = FlexDate::newTime( '20:00:00' );

        $arrayProp -> withTimeList( new ArraySimplePropsTimeListList( array( $date1 ) ) );
        $this->assertSame( $arrayProp->timeList()[0]->jsonSerialize(), '15:07:20' );
        $arrayProp->timeList()[] = $date2;
        $this->assertSame( $arrayProp->timeList()[1]->jsonSerialize(), '20:00:00' );
    }

    public function testDateArray(){
        $arrayProp = new ArraySimpleProps();

        $date1 = FlexDate::newDate( "2018-10-17" );
        $date2 = FlexDate::newDate( "2017-01-18" );

        $arrayProp -> withDateList( new ArraySimplePropsDateListList( array( $date1 ) ) );
        $this->assertSame( $arrayProp->dateList()[0]->jsonSerialize(), '2018-10-17' );
        $arrayProp->dateList()[] = $date2;
        $this->assertSame( $arrayProp->dateList()[1]->jsonSerialize(), '2017-01-18' );
    }

    public function testDateTimeArray(){
        $arrayProp = new ArraySimpleProps();

        $date1 = FlexDate::newDateTime( '2018-10-17T15:07:20' );
        $date2 = FlexDate::newDateTime( '2017-01-18T20:00:00' );

        $arrayProp -> withDateTimeList( new ArraySimplePropsDateTimeListList( array( $date1 ) ) );
        $this->assertSame( $arrayProp->dateTimeList()[0]->jsonSerialize(), '2018-10-17T15:07:20' );
        $arrayProp->dateTimeList()[] = $date2;
        $this->assertSame( $arrayProp->dateTimeList()[1]->jsonSerialize(), '2017-01-18T20:00:00' );
    }

    public function testTzDateTimeArray(){
        $arrayProp = new ArraySimpleProps();

        $date1 = FlexDate::newTzDateTime( '2018-10-17T15:07:20' );
        $date2 = FlexDate::newTzDateTime( '2018-10-17T15:07:20+01:00' );
        $date3 = FlexDate::newTzDateTime( '2017-01-18T20:00:00-02:00' );

        $arrayProp -> withTzDateTimeList( new ArraySimplePropsTzDateTimeListList( array( $date1, $date2 ) ) );
        $this->assertSame( $arrayProp->tzDateTimeList()[0]->jsonSerialize(), '2018-10-17T15:07:20+00:00' );
        $this->assertSame( $arrayProp->tzDateTimeList()[1]->jsonSerialize(), '2018-10-17T15:07:20+01:00' );

        $arrayProp->tzDateTimeList()[] = $date3;
        $this->assertSame( $arrayProp->tzDateTimeList()[2]->jsonSerialize(), '2017-01-18T20:00:00-02:00' );
    }

    public function testStringBadParameter(){
        $this->expectException(\TypeError::class);
        new ArraySimplePropsStringListList( array( "toto", "titi", new ArraySimpleProps() ) );
    }

    public function testIntegerBadParameter(){
        $this->expectException(\TypeError::class);
        new ArraySimplePropsIntegerListList( array( "toto", "titi" ) );
    }

    public function testLongBadParameter(){
        $this->expectException(\TypeError::class);
        new ArraySimplePropsLongListList( array( "toto", "titi" ) );
    }

    public function testFloatBadParameter(){
        $this->expectException(\TypeError::class);
        new ArraySimplePropsFloatListList( array( "toto", "titi" ) );
    }

    public function testDoubleBadParameter(){
        $this->expectException(\TypeError::class);
        new ArraySimplePropsDoubleListList( array( "toto", "titi" ) );
    }

    public function testBoolBadParameter(){
        $this->expectException(\TypeError::class);
        new ArraySimplePropsDoubleListList( array( "toto", "titi" ) );
    }

    public function testTimeBadParameter(){
        $this->expectException(\TypeError::class);
        new ArraySimplePropsTimeListList( array( new \DateTime() ) );
    }

    public function testDateBadParameter(){
        $this->expectException(\TypeError::class);
        new ArraySimplePropsDateListList( array( new \DateTime() ) );
    }

    public function testDateTimeBadParameter(){
        $this->expectException(\TypeError::class);
        new ArraySimplePropsDateTimeListList( array( new \DateTime() ) );
    }

    public function testTzDateTimeBadParameter(){
        $this->expectException(\TypeError::class);
        new ArraySimplePropsTzDateTimeListList( array( new \DateTime() ) );
    }

    public function testReader(){
        $arrayProp = new ArraySimpleProps();
        $arrayProp -> withStringList( new ArraySimplePropsStringListList( array( "foo", "bar" )));
        $arrayProp -> withIntegerList( new ArraySimplePropsIntegerListList( array( 7, 9 )));
        $arrayProp -> withLongList( new ArraySimplePropsLongListList( array( 7, 9 )));
        $arrayProp -> withFloatList( new ArraySimplePropsFloatListList( array( 7.5, 9.0 )));
        $arrayProp -> withDoubleList( new ArraySimplePropsDoubleListList( array( 7.5, 9.0 )));
        $arrayProp -> withBooleanList( new ArraySimplePropsBooleanListList( array( true, false )));
        $arrayProp -> withTimeList( new ArraySimplePropsTimeListList( array( FlexDate::newTime( '15:07:20' ))));
        $arrayProp -> withDateList( new ArraySimplePropsDateListList( array( FlexDate::newDate( "2018-10-17" ))));
        $arrayProp -> withDateTimeList( new ArraySimplePropsDateTimeListList( array( FlexDate::newDateTime( '2017-01-18T20:00:00' ))));
        $arrayProp -> withTzDateTimeList( new ArraySimplePropsTzDateTimeListList( array( FlexDate::newTzDateTime( '2018-10-17T15:07:20+01:00' ))));

        $arrayProp -> withStringSet( new ArraySimplePropsStringSetList( array( "foo", "bar" )));
        $arrayProp -> withIntegerSet( new ArraySimplePropsIntegerSetList( array( 7, 9 )));
        $arrayProp -> withLongSet( new ArraySimplePropsLongSetList( array( 7, 9 )));
        $arrayProp -> withFloatSet( new ArraySimplePropsFloatSetList( array( 7.5, 9.0 )));
        $arrayProp -> withDoubleSet( new ArraySimplePropsDoubleSetList( array( 7.5, 9.0 )));
        $arrayProp -> withBooleanSet( new ArraySimplePropsBooleanSetList( array( true, false )));
        $arrayProp -> withTimeSet( new ArraySimplePropsTimeSetList( array( FlexDate::newTime( '15:07:20' ))));
        $arrayProp -> withDateSet( new ArraySimplePropsDateSetList( array( FlexDate::newDate( "2018-10-17" ))));
        $arrayProp -> withDateTimeSet( new ArraySimplePropsDateTimeSetList( array( FlexDate::newDateTime( '2017-01-18T20:00:00' ))));
        $arrayProp -> withTzDateTimeSet( new ArraySimplePropsTzDateTimeSetList( array( FlexDate::newTzDateTime( '2018-10-17T15:07:20+01:00' ))));

        $content = json_encode( $arrayProp, JSON_PRESERVE_ZERO_FRACTION );

        $reader = new ArraySimplePropsReader();
        $object = $reader->read( $content );

        $this->assertNotNull( $object );
        $this->assertSame( $object->stringList()[0], 'foo' );
        $this->assertSame( $object->stringList()[1], 'bar' );

        $this->assertSame( $object->integerList()[0], 7 );
        $this->assertSame( $object->integerList()[1], 9 );

        $this->assertSame( $object->longList()[0], 7 );
        $this->assertSame( $object->longList()[1], 9 );

        $this->assertSame( $object->floatList()[0], 7.5 );
        $this->assertSame( $object->floatList()[1], 9.0 );

        $this->assertSame( $object->doubleList()[0], 7.5 );
        $this->assertSame( $object->doubleList()[1], 9.0 );

        $this->assertSame( $object->booleanList()[0], true );
        $this->assertSame( $object->booleanList()[1], false );

        $this->assertSame( $object->timeList()[0]->jsonSerialize(), '15:07:20' );

        $this->assertSame( $object->dateList()[0]->jsonSerialize(), '2018-10-17' );

        $this->assertSame( $object->dateTimeList()[0]->jsonSerialize(), '2017-01-18T20:00:00' );

        $this->assertSame( $object->tzDateTimeList()[0]->jsonSerialize(), '2018-10-17T15:07:20+01:00' );

        $this->assertSame( $object->stringSet()[0], 'foo' );
        $this->assertSame( $object->stringSet()[1], 'bar' );

        $this->assertSame( $object->integerSet()[0], 7 );
        $this->assertSame( $object->integerSet()[1], 9 );

        $this->assertSame( $object->longSet()[0], 7 );
        $this->assertSame( $object->longSet()[1], 9 );

        $this->assertSame( $object->floatSet()[0], 7.5 );
        $this->assertSame( $object->floatSet()[1], 9.0 );

        $this->assertSame( $object->doubleSet()[0], 7.5 );
        $this->assertSame( $object->doubleSet()[1], 9.0 );

        $this->assertSame( $object->booleanSet()[0], true );
        $this->assertSame( $object->booleanSet()[1], false );

        $this->assertSame( $object->timeSet()[0]->jsonSerialize(), '15:07:20' );

        $this->assertSame( $object->dateSet()[0]->jsonSerialize(), '2018-10-17' );

        $this->assertSame( $object->dateTimeSet()[0]->jsonSerialize(), '2017-01-18T20:00:00' );

        $this->assertSame( $object->tzDateTimeSet()[0]->jsonSerialize(), '2018-10-17T15:07:20+01:00' );
    }


}
