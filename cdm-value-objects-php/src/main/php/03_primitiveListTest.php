<?php

namespace Test;

use PHPUnit\Framework\TestCase;
use io\flexio\utils\FlexDate;
use org\generated\ArraySimpleProps;
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

}
