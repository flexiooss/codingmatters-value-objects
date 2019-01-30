import "../org/package"

test('test initialization', () => {
    var myEmptyObject = new window.FLEXIO_IMPORT_OBJECT.org.generated.EmptyObject();
    expect( myEmptyObject ).not.toBeNull();
});

test('test serialization', () => {
    var myEmptyObject = new window.FLEXIO_IMPORT_OBJECT.org.generated.EmptyObject();
    expect( JSON.stringify(myEmptyObject)).toBe( "{}" );
});

test('test deserialization', () => {
    var myEmptyObject = window.FLEXIO_IMPORT_OBJECT.org.generated.EmptyObjectBuilder.fromJson("{}");
    expect( myEmptyObject ).not.toBeNull();
    expect( JSON.stringify(myEmptyObject) ).toBe( "{}" );
});

test('test object immutable', () => {
    var myEmptyObject = new window.FLEXIO_IMPORT_OBJECT.org.generated.EmptyObject();
    expect(() => {
        myEmptyObject.floatProp = 12.5
    }).toThrow(TypeError);
});

