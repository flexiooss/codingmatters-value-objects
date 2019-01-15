import {EmptyObject} from '../org/generated/EmptyObject'
import {EmptyObjectBuilder} from '../org/generated/EmptyObject'

test('test initialization', () => {
    var myEmptyObject = new EmptyObject();
    expect( myEmptyObject ).not.toBeNull();
});

test('test serialization', () => {
    var myEmptyObject = new EmptyObject();
    expect( JSON.stringify(myEmptyObject)).toBe( "{}" );
});

test('test deserialization', () => {
    var myEmptyObject = EmptyObjectBuilder.fromJson("{}");
    expect( myEmptyObject ).not.toBeNull();
    expect( JSON.stringify(myEmptyObject) ).toBe( "{}" );
});

test('test object immutable', () => {
    var myEmptyObject = new EmptyObject();
    expect(() => {
        myEmptyObject.floatProp = 12.5
    }).toThrow(TypeError);
});

