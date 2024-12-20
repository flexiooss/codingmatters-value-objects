import {TestCase} from '@flexio-oss/code-altimeter-js'
import '../org/package'
import {globalFlexioImport} from '@flexio-oss/js-commons-bundle/global-import-registry'

const assert = require('assert')

class ListOListTest extends TestCase {

  testBuilder() {
    let yaay1 = new globalFlexioImport.org.generated.YaayBuilder()
    yaay1.aie('carramba!')
    let yaay2 = new globalFlexioImport.org.generated.YaayBuilder()
    yaay2.aie('carrambar!')
    let yaay3 = new globalFlexioImport.org.generated.YaayBuilder()
    yaay3.aie('car en bar!')
    let list1 = new globalFlexioImport.org.generated.YaayList(yaay1.build())
    let list2 = new globalFlexioImport.org.generated.YaayList(yaay2.build(), yaay3.build())
    let doubleList = new globalFlexioImport.org.generated.yaaydoublelist.YaayDoubleListDoubleListListList(list1, list2)
    let strangeThing = new globalFlexioImport.org.generated.YaayDoubleListBuilder()
    strangeThing.simpleList(list1)
    strangeThing.doubleList(doubleList)

    let yaayDoubleList = strangeThing.build()
    assert.deepEqual(yaayDoubleList.doubleList()[0][0].aie(), 'carramba!')
    assert.deepEqual(yaayDoubleList.doubleList()[1][0].aie(), 'carrambar!')
    assert.deepEqual(yaayDoubleList.doubleList()[1][1].aie(), 'car en bar!')
  }

  testSerialization() {
    let yaay1 = new globalFlexioImport.org.generated.YaayBuilder()
    yaay1.aie('carramba!')
    let yaay2 = new globalFlexioImport.org.generated.YaayBuilder()
    yaay2.aie('carrambar!')
    let yaay3 = new globalFlexioImport.org.generated.YaayBuilder()
    yaay3.aie('car en bar!')
    let list1 = new globalFlexioImport.org.generated.YaayList(yaay1.build())
    let list2 = new globalFlexioImport.org.generated.YaayList(yaay2.build(), yaay3.build())
    let doubleList = new globalFlexioImport.org.generated.yaaydoublelist.YaayDoubleListDoubleListListList(list1, list2)
    let strangeThing = new globalFlexioImport.org.generated.YaayDoubleListBuilder()
    strangeThing.simpleList(list1)
    strangeThing.doubleList(doubleList)

    let yaayDoubleList = strangeThing.build()
    assert.strictEqual(JSON.stringify(yaayDoubleList), '{"simpleList":[{"aie":"carramba!"}],"doubleList":[[{"aie":"carramba!"}],[{"aie":"carrambar!"},{"aie":"car en bar!"}]]}')
  }

  testDeserialization() {
    let json = '{"simpleList":[{"aie":"carramba!"}],"doubleList":[[{"aie":"carramba!"}],[{"aie":"carrambar!"},{"aie":"car en bar!"}]]}'
    let yaayDoubleList = globalFlexioImport.org.generated.YaayDoubleListBuilder.fromJson(json).build()
    assert.deepEqual(yaayDoubleList.doubleList()[0][0].aie(), 'carramba!')
    assert.deepEqual(yaayDoubleList.doubleList()[1][0].aie(), 'carrambar!')
    assert.deepEqual(yaayDoubleList.doubleList()[1][1].aie(), 'car en bar!')
  }

  testListWithNullItem() {
      let json = '{"simpleList":[{"aie":"carramba!"}, null]}'
      let yaayDoubleList = globalFlexioImport.org.generated.YaayDoubleListBuilder.fromJson(json).build()
      assert.deepEqual(yaayDoubleList.simpleList()[0].aie(), 'carramba!')
      assert.strictEqual(yaayDoubleList.simpleList()[1], null)
  }

  testToObject() {
    let yaay1 = new globalFlexioImport.org.generated.YaayBuilder()
    yaay1.aie('carramba!')
    let yaay2 = new globalFlexioImport.org.generated.YaayBuilder()
    yaay2.aie('carrambar!')
    let yaay3 = new globalFlexioImport.org.generated.YaayBuilder()
    yaay3.aie('car en bar!')
    let list1 = new globalFlexioImport.org.generated.YaayList(yaay1.build())
    let list2 = new globalFlexioImport.org.generated.YaayList(yaay2.build(), yaay3.build())
    let doubleList = new globalFlexioImport.org.generated.yaaydoublelist.YaayDoubleListDoubleListListList(list1, list2)
    let strangeThing = new globalFlexioImport.org.generated.YaayDoubleListBuilder()
    strangeThing.simpleList(list1)
    strangeThing.doubleList(doubleList)

    let yaayDoubleList = strangeThing.build()
    let toObject = yaayDoubleList.toObject();
    let firstYaay = toObject['doubleList'][0][0];
    let jsonObject = {}
    assert.strictEqual( firstYaay.constructor.name, jsonObject.constructor.name);
    assert.strictEqual( typeof firstYaay, 'object');
    assert.strictEqual( firstYaay["aie"], 'carramba!');
  }

  testToObjectWithNullValues() {
    let yaay1 = new globalFlexioImport.org.generated.YaayBuilder()
    yaay1.aie('carramba!')

    let yaay3 = new globalFlexioImport.org.generated.YaayBuilder()
    yaay3.aie('car en bar!')
    let list1 = new globalFlexioImport.org.generated.YaayList(yaay1.build())
    let list2 = new globalFlexioImport.org.generated.YaayList(null, yaay3.build())
    let doubleList = new globalFlexioImport.org.generated.yaaydoublelist.YaayDoubleListDoubleListListList(list1, list2, null)
    let strangeThing = new globalFlexioImport.org.generated.YaayDoubleListBuilder()
    strangeThing.simpleList(list1)
    strangeThing.doubleList(doubleList)

    let yaayDoubleList = strangeThing.build()
    let toObject = yaayDoubleList.toObject();
    let firstYaay = toObject['doubleList'][1][0];
    assert.strictEqual( firstYaay, null);
    let secondYaay = toObject['doubleList'][1][1];
    assert.strictEqual( secondYaay["aie"], "car en bar!");
  }

}

runTest(ListOListTest)
