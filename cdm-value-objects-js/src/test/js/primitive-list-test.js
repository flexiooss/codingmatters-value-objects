import {TestCase} from '@flexio-oss/code-altimeter-js'
import '../org/package'
import {globalFlexioImport} from '@flexio-oss/js-commons-bundle/global-import-registry'
import {FlexDate, FlexDateTime, FlexTime, FlexZonedDateTime} from '@flexio-oss/js-commons-bundle/flex-types'

const assert = require('assert')

class PrimitiveListTest extends TestCase {

  testBuilder() {
    let builder = new globalFlexioImport.org.generated.ArraySimplePropsBuilder()
    builder.booleanList(new globalFlexioImport.io.flexio.flex_types.arrays.BooleanArray(true, true, false))
    builder.dateList(new globalFlexioImport.io.flexio.flex_types.arrays.DateArray(new FlexDate('2019-01-17')))
    builder.dateTimeList(new globalFlexioImport.io.flexio.flex_types.arrays.DateTimeArray(new FlexDateTime('2019-01-17T14:15:37')))
    builder.timeList(new globalFlexioImport.io.flexio.flex_types.arrays.TimeArray(new FlexTime('14:15:37')))
    builder.tzDateTimeList(new globalFlexioImport.io.flexio.flex_types.arrays.TzDateTimeArray(new FlexZonedDateTime('2019-01-17T14:15:37+04:30')))
    builder.doubleList(new globalFlexioImport.io.flexio.flex_types.arrays.DoubleArray(1.4, 1.7, 1.9))
    builder.floatList(new globalFlexioImport.io.flexio.flex_types.arrays.FloatArray(1.5, 1.6, 1.9))
    builder.integerList(new globalFlexioImport.io.flexio.flex_types.arrays.IntegerArray(1, 2, 3))
    builder.longList(new globalFlexioImport.io.flexio.flex_types.arrays.LongArray(4, 5, 6))
    builder.stringList(new globalFlexioImport.io.flexio.flex_types.arrays.StringArray('1', '2', '3'))

    let arrayProps = builder.build()

    assert.deepEqual(arrayProps.booleanList(), [true, true, false])
    assert.strictEqual(arrayProps.dateList()[0].toJSON(), '2019-01-17')
    assert.strictEqual(arrayProps.dateTimeList()[0].toJSON(), '2019-01-17T14:15:37')
    assert.strictEqual(arrayProps.timeList()[0].toJSON(), '14:15:37')
    assert.strictEqual(arrayProps.tzDateTimeList()[0].toJSON(), '2019-01-17T14:15:37+04:30')
    assert.deepEqual(arrayProps.doubleList(), [1.4, 1.7, 1.9])
    assert.deepEqual(arrayProps.floatList(), [1.5, 1.6, 1.9])
    assert.deepEqual(arrayProps.integerList(), [1, 2, 3])
    assert.deepEqual(arrayProps.longList(), [4, 5, 6])
    assert.deepEqual(arrayProps.stringList(), ['1', '2', '3'])

    // TODO test check types
  }

  testListWithNullItem(){
    let builder = new globalFlexioImport.org.generated.ArraySimplePropsBuilder()
    builder.booleanList(new globalFlexioImport.io.flexio.flex_types.arrays.BooleanArray(true, true, false, null))
    builder.dateList(new globalFlexioImport.io.flexio.flex_types.arrays.DateArray(new FlexDate('2019-01-17', null)))
    builder.dateTimeList(new globalFlexioImport.io.flexio.flex_types.arrays.DateTimeArray(new FlexDateTime('2019-01-17T14:15:37', null)))
    builder.timeList(new globalFlexioImport.io.flexio.flex_types.arrays.TimeArray(new FlexTime('14:15:37', null)))
    builder.tzDateTimeList(new globalFlexioImport.io.flexio.flex_types.arrays.TzDateTimeArray(new FlexZonedDateTime('2019-01-17T14:15:37+04:30', null)))
    builder.doubleList(new globalFlexioImport.io.flexio.flex_types.arrays.DoubleArray(1.4, 1.7, 1.9, null))
    builder.floatList(new globalFlexioImport.io.flexio.flex_types.arrays.FloatArray(1.5, 1.6, 1.9, null))
    builder.integerList(new globalFlexioImport.io.flexio.flex_types.arrays.IntegerArray(1, 2, 3, null))
    builder.longList(new globalFlexioImport.io.flexio.flex_types.arrays.LongArray(4, 5, 6, null))
    builder.stringList(new globalFlexioImport.io.flexio.flex_types.arrays.StringArray('1', '2', '3', null))

    let arrayProps = builder.build()

    assert.deepEqual(arrayProps.booleanList(), [true, true, false, null])
    assert.strictEqual(arrayProps.dateList()[0].toJSON(), '2019-01-17', null)
    assert.strictEqual(arrayProps.dateTimeList()[0].toJSON(), '2019-01-17T14:15:37', null)
    assert.strictEqual(arrayProps.timeList()[0].toJSON(), '14:15:37', null)
    assert.strictEqual(arrayProps.tzDateTimeList()[0].toJSON(), '2019-01-17T14:15:37+04:30', null)
    assert.deepEqual(arrayProps.doubleList(), [1.4, 1.7, 1.9, null])
    assert.deepEqual(arrayProps.floatList(), [1.5, 1.6, 1.9, null])
    assert.deepEqual(arrayProps.integerList(), [1, 2, 3, null])
    assert.deepEqual(arrayProps.longList(), [4, 5, 6, null])
    assert.deepEqual(arrayProps.stringList(), ['1', '2', '3', null])
  }

  testObjectImmutable() {
    let builder = new globalFlexioImport.org.generated.ArraySimplePropsBuilder()
    builder.booleanList(new globalFlexioImport.io.flexio.flex_types.arrays.BooleanArray(true, true, false))
    let primitiveProp = builder.build()

    assert.throws(() => {
      primitiveProp.booleanList = 12.5
    }, TypeError)
  }

  testObjectFrozen() {
    let builder = new globalFlexioImport.org.generated.ArraySimplePropsBuilder()
    builder.booleanList(new globalFlexioImport.io.flexio.flex_types.arrays.BooleanArray(true, true, false))

    let primitiveProp = builder.build()

    assert.throws(() => {
      primitiveProp.newProp = 12.5
    }, TypeError)
  }

  testSerialization() {
    let builder = new globalFlexioImport.org.generated.ArraySimplePropsBuilder()
    builder.booleanList(new globalFlexioImport.io.flexio.flex_types.arrays.BooleanArray(true, true, false))
    builder.dateList(new globalFlexioImport.io.flexio.flex_types.arrays.DateArray(new FlexDate('2019-01-17')))
    builder.dateTimeList(new globalFlexioImport.io.flexio.flex_types.arrays.DateTimeArray(new FlexDateTime('2019-01-17T14:15:37')))
    builder.timeList(new globalFlexioImport.io.flexio.flex_types.arrays.TimeArray(new FlexTime('14:15:37')))
    builder.tzDateTimeList(new globalFlexioImport.io.flexio.flex_types.arrays.TzDateTimeArray(new FlexZonedDateTime('2019-01-17T14:15:37+04:30')))
    builder.doubleList(new globalFlexioImport.io.flexio.flex_types.arrays.DoubleArray(1.4, 1.7, 1.9))
    builder.floatList(new globalFlexioImport.io.flexio.flex_types.arrays.FloatArray(1.5, 1.6, 1.9))
    builder.integerList(new globalFlexioImport.io.flexio.flex_types.arrays.IntegerArray(1, 2, 3))
    builder.longList(new globalFlexioImport.io.flexio.flex_types.arrays.LongArray(4, 5, 6))
    builder.stringList(new globalFlexioImport.io.flexio.flex_types.arrays.StringArray('1', '2', '3'))
    let arrayProps = builder.build()
    assert.strictEqual(JSON.stringify(arrayProps), '{"stringList":["1","2","3"],"integerList":[1,2,3],"longList":[4,5,6],"floatList":[1.5,1.6,1.9],"doubleList":[1.4,1.7,1.9],"booleanList":[true,true,false],"date-list":["2019-01-17"],"timeList":["14:15:37"],"dateTimeList":["2019-01-17T14:15:37"],"tzDateTimeList":["2019-01-17T14:15:37+04:30"]}')
  }

  testDeserialization() {
    let json = '{"stringList":["1","2","3"],"integerList":[1,2,3],"longList":[4,5,6],"floatList":[1.5,1.6,1.9],"doubleList":[1.4,1.7,1.9],"booleanList":[true,true,false],"date-list":["2019-01-17"],"timeList":["14:15:37"],"dateTimeList":["2019-01-17T14:15:37"],"tzDateTimeList":["2019-01-17T14:15:37+04:30"]}'

    let arrayProps = globalFlexioImport.org.generated.ArraySimplePropsBuilder.fromJson(json).build()
    assert.deepEqual(arrayProps.booleanList(), [true, true, false])
    assert.strictEqual(arrayProps.dateList()[0].toJSON(), '2019-01-17')
    assert.strictEqual(arrayProps.dateTimeList()[0].toJSON(), '2019-01-17T14:15:37')
    assert.strictEqual(arrayProps.timeList()[0].toJSON(), '14:15:37')
    assert.strictEqual(arrayProps.tzDateTimeList()[0].toJSON(), '2019-01-17T14:15:37+04:30')
    assert.deepEqual(arrayProps.doubleList(), [1.4, 1.7, 1.9])
    assert.deepEqual(arrayProps.floatList(), [1.5, 1.6, 1.9])
    assert.deepEqual(arrayProps.integerList(), [1, 2, 3])
    assert.deepEqual(arrayProps.longList(), [4, 5, 6])
    assert.deepEqual(arrayProps.stringList(), ['1', '2', '3'])
  }

  testDeserializationNullProp() {
    let json = '{"stringList":["1","2","3",null],"integerList":[1,2,3,null],"longList":[4,5,6,null],"floatList":[1.5,1.6,1.9,null],"doubleList":[1.4,1.7,1.9,null],"booleanList":[true,true,false,null],"date-list":["2019-01-17",null],"timeList":["14:15:37",null],"dateTimeList":["2019-01-17T14:15:37",null],"tzDateTimeList":["2019-01-17T14:15:37+04:30",null]}'

    let arrayProps = globalFlexioImport.org.generated.ArraySimplePropsBuilder.fromJson(json).build()
    assert.deepEqual(arrayProps.booleanList(), [true, true, false,null])
    assert.strictEqual(arrayProps.dateList()[0].toJSON(), '2019-01-17')
    assert.strictEqual(arrayProps.dateList()[1], null)
    assert.strictEqual(arrayProps.dateTimeList()[0].toJSON(), '2019-01-17T14:15:37')
    assert.strictEqual(arrayProps.dateTimeList()[1], null)
    assert.strictEqual(arrayProps.timeList()[0].toJSON(), '14:15:37')
    assert.strictEqual(arrayProps.timeList()[1], null)
    assert.strictEqual(arrayProps.tzDateTimeList()[0].toJSON(), '2019-01-17T14:15:37+04:30')
    assert.strictEqual(arrayProps.tzDateTimeList()[1], null)
    assert.deepEqual(arrayProps.doubleList(), [1.4, 1.7, 1.9, null])
    assert.deepEqual(arrayProps.floatList(), [1.5, 1.6, 1.9, null])
    assert.deepEqual(arrayProps.integerList(), [1, 2, 3, null])
    assert.deepEqual(arrayProps.longList(), [4, 5, 6, null])
    assert.deepEqual(arrayProps.stringList(), ['1', '2', '3', null])
    }


  testDeserializationWithNormalizedName() {
    let json = '{"dateList":["2019-01-17"]}'
    let arrayProps = globalFlexioImport.org.generated.ArraySimplePropsBuilder.fromJson(json).build()
    assert.strictEqual(arrayProps.dateList()[0].toJSON(), '2019-01-17')
  }

}

runTest(PrimitiveListTest)
