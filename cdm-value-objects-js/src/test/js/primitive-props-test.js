import {TestCase} from '@flexio-oss/code-altimeter-js'
import '../org/package'
import {globalFlexioImport} from '@flexio-oss/js-commons-bundle/global-import-registry'
import {FlexDate, FlexDateTime, FlexTime, FlexZonedDateTime} from '@flexio-oss/js-commons-bundle/flex-types'
import { isStrictObject } from '@flexio-oss/js-commons-bundle/assert'

const assert = require('assert')

class PrimitivePropsTest extends TestCase {

  testBuilder() {
    let builder = new globalFlexioImport.org.generated.PrimitivePropsBuilder()
    builder.stringProp('str')
//    builder.bytesProp('bytes')
    builder.integerProp(9)
    builder.longProp(7)
    builder.floatProp(9.7)
    builder.doubleProp(7.9)
    builder.booleanProp(true)
    let ov = globalFlexioImport.io.flexio.flex_types.ObjectValue
      .builder()
      .stringValue('jean', 'tenbien')
      .build()
    builder.objectProp(ov)

    let primitiveProp = builder.build()

    assert.strictEqual(primitiveProp.stringProp(), 'str')
//    assert.strictEqual(primitiveProp.bytesProp(), 'bytes')
    assert.strictEqual(primitiveProp.integerProp(), 9)
    assert.strictEqual(primitiveProp.longProp(), 7)
    assert.strictEqual(primitiveProp.floatProp(), 9.7)
    assert.strictEqual(primitiveProp.doubleProp(), 7.9)
    assert.strictEqual(primitiveProp.booleanProp(), true)
    assert.strictEqual(primitiveProp.objectProp().stringValue('jean'), 'tenbien')
  }

  testEmbededBuilder() {
    let ov = globalFlexioImport.io.flexio.flex_types.ObjectValue
      .builder()
      .stringValue('jean', 'tenbien')
      .build()
    const primitiveProp = globalFlexioImport.org.generated.PrimitiveProps.builder()
      .stringProp('str')
      //      .bytesProp('bytes')
      .integerProp(9)
      .longProp(7)
      .floatProp(9.7)
      .doubleProp(7.9)
      .booleanProp(true)
      .objectProp(ov)
      .build()

    assert.strictEqual(primitiveProp.stringProp(), 'str')
//    assert.strictEqual(primitiveProp.bytesProp(), 'bytes')
    assert.strictEqual(primitiveProp.integerProp(), 9)
    assert.strictEqual(primitiveProp.longProp(), 7)
    assert.strictEqual(primitiveProp.floatProp(), 9.7)
    assert.strictEqual(primitiveProp.doubleProp(), 7.9)
    assert.strictEqual(primitiveProp.booleanProp(), true)
    assert.strictEqual(primitiveProp.objectProp().stringValue('jean'), 'tenbien')
  }

  testBuilderFrom() {
    const primitiveProp = globalFlexioImport.org.generated.PrimitiveProps.builder()
      .stringProp('str')
      //      .bytesProp('bytes')
      .build()

    const primitiveProp2 = globalFlexioImport.org.generated.PrimitiveProps
      .from(primitiveProp)
      .stringProp('str2')
      .build()

    assert.strictEqual(primitiveProp.stringProp(), 'str')
//    assert.strictEqual(primitiveProp.bytesProp(), 'bytes')
    assert.strictEqual(primitiveProp2.stringProp(), 'str2')
//    assert.strictEqual(primitiveProp2.bytesProp(), 'bytes')
  }

  testWithMethod() {
    let builder = new globalFlexioImport.org.generated.PrimitivePropsBuilder()
    builder.stringProp('str')
//    builder.bytesProp('bytes')
    builder.integerProp(9)
    builder.longProp(7)
    builder.floatProp(9.7)
    builder.doubleProp(7.9)
    builder.booleanProp(true)
    let primitiveProp = builder.build()

    assert.strictEqual(primitiveProp.stringProp(), 'str')
    let primitiveProp2 = primitiveProp.withStringProp('str2')
    assert.strictEqual(primitiveProp.stringProp(), 'str')
    assert.strictEqual(primitiveProp2.stringProp(), 'str2')
  }

  testObjectImmutable() {
    let builder = new globalFlexioImport.org.generated.PrimitivePropsBuilder()
    builder.stringProp('str')

    let primitiveProp = builder.build()

    assert.throws(() => {
      primitiveProp.floatProp = 12.5
    }, TypeError)
  }

  testObjectFrozen() {
    let builder = new globalFlexioImport.org.generated.PrimitivePropsBuilder()
    builder.stringProp('str')

    let primitiveProp = builder.build()

    assert.throws(() => {
      primitiveProp.newProp = 12.5
    }, TypeError)
  }

  testSerialization() {
    let builder = new globalFlexioImport.org.generated.PrimitivePropsBuilder()
    builder.stringProp('str')
//    builder.bytesProp('bytes')
    builder.integerProp(9)
    builder.longProp(7)
    builder.floatProp(9.7)
    builder.doubleProp(7.9)
    builder.booleanProp(true)
    builder.dateProp(new FlexDate('2019-01-09'))
    builder.timeProp(new FlexTime('14:17:32'))
    builder.dateTimeProp(new FlexDateTime('2019-01-09T14:17:32'))
    builder.tzDateTimeProp(new FlexZonedDateTime('2019-01-09T14:17:32-03:00'))
    let ov = globalFlexioImport.io.flexio.flex_types.ObjectValue
      .builder()
      .stringValue('jean', 'tenbien')
      .build()
    builder.objectProp(ov)
    let primitiveProp = builder.build()
    assert.strictEqual(JSON.stringify(primitiveProp), '{"stringProp":"str","integerProp":9,"longProp":7,"floatProp":9.7,"doubleProp":7.9,"booleanProp":true,"date-prop":"2019-01-09","timeProp":"14:17:32","dateTimeProp":"2019-01-09T14:17:32","tzDateTimeProp":"2019-01-09T14:17:32-03:00","objectProp":{"jean":"tenbien"}}')
  }

  testDeserialization() {
    let json = '{"stringProp":"str","integerProp":9,"longProp":7,"floatProp":9.7,"doubleProp":7.9,"booleanProp":true,"date-prop":"2019-01-09","timeProp":"14:17:32","dateTimeProp":"2019-01-09T14:17:32","tzDateTimeProp":"2019-01-09T14:17:32-03:00","objectProp":{"jean":"tenbien"}}'
    let primitiveProp = globalFlexioImport.org.generated.PrimitivePropsBuilder.fromJson(json).build()
    assert.strictEqual(primitiveProp.stringProp(), 'str')
//    assert.strictEqual(primitiveProp.bytesProp(), 'bytes')
    assert.strictEqual(primitiveProp.integerProp(), 9)
    assert.strictEqual(primitiveProp.longProp(), 7)
    assert.strictEqual(primitiveProp.floatProp(), 9.7)
    assert.strictEqual(primitiveProp.doubleProp(), 7.9)
    assert.strictEqual(primitiveProp.booleanProp(), true)
    assert.strictEqual(primitiveProp.dateProp().toJSON(), '2019-01-09')
    assert.strictEqual(primitiveProp.timeProp().toJSON(), '14:17:32')
    assert.strictEqual(primitiveProp.dateTimeProp().toJSON(), '2019-01-09T14:17:32')
    assert.strictEqual(primitiveProp.tzDateTimeProp().toJSON(), '2019-01-09T14:17:32-03:00')
    assert.strictEqual(typeof (primitiveProp.tzDateTimeProp()), 'object')
    assert.strictEqual(typeof (primitiveProp.dateTimeProp()), 'object')
    assert.strictEqual(typeof (primitiveProp.dateProp()), 'object')
    assert.strictEqual(typeof (primitiveProp.timeProp()), 'object')
    assert.strictEqual(primitiveProp.objectProp().stringValue('jean'), 'tenbien')
  }

  testDeserializationNormalizedName() {
    let json = '{"dateProp":"2019-01-09"}'
    let primitiveProp = globalFlexioImport.org.generated.PrimitivePropsBuilder.fromJson(json).build()
    assert.strictEqual(primitiveProp.dateProp().toJSON(), '2019-01-09')
  }

  testDateFromObject(){
    let builder = new globalFlexioImport.org.generated.PrimitivePropsBuilder()
    builder.stringProp('str')
    builder.integerProp(9)
    builder.longProp(7)
    builder.floatProp(9.7)
    builder.doubleProp(7.9)
    builder.booleanProp(true)
    builder.dateProp(new FlexDate('2019-01-09'))
    builder.timeProp(new FlexTime('14:17:32'))
    builder.dateTimeProp(new FlexDateTime('2019-01-09T14:17:32'))
    builder.tzDateTimeProp(new FlexZonedDateTime('2019-01-09T14:17:32-03:00'))
    let object1 = builder.build()
    let object2 = globalFlexioImport.org.generated.PrimitivePropsBuilder.fromObject(object1.toObject());
    assert.deepEqual(object2, object1)
  }

  testToObject() {
    let builder = new globalFlexioImport.org.generated.PrimitivePropsBuilder()
    builder.stringProp('str')
//    builder.bytesProp('bytes')
    builder.integerProp(9)
    builder.longProp(7)
    builder.floatProp(9.7)
    builder.doubleProp(7.9)
    builder.booleanProp(true)
    builder.dateProp(new FlexDate('2019-01-09'))
    builder.timeProp(new FlexTime('14:17:32'))
    builder.dateTimeProp(new FlexDateTime('2019-01-09T14:17:32'))
    builder.tzDateTimeProp(new FlexZonedDateTime('2019-01-09T14:17:32-03:00'))
    let ov = globalFlexioImport.io.flexio.flex_types.ObjectValue
      .builder()
      .stringValue('jean', 'tenbien')
      .build()
    builder.objectProp(ov)
    let primitiveProp = builder.build()
    let obj = primitiveProp.toObject();

    assert.strictEqual(obj["objectProp"] instanceof globalFlexioImport.io.flexio.flex_types.ObjectValue, false);
    isStrictObject(obj["objectProp"]);
  }

}

runTest(PrimitivePropsTest)
