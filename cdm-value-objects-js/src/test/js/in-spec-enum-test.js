import "../org/package"

test( 'test builder', () => {
    var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.InSpecEnumPropertiesBuilder();
    builder.single( window.FLEXIO_IMPORT_OBJECT.org.generated.inspecenumproperties.InSpecEnumPropertiesSingle.SA );
    builder.multiple( [window.FLEXIO_IMPORT_OBJECT.org.generated.inspecenumproperties.InSpecEnumPropertiesMultiple.MB, window.FLEXIO_IMPORT_OBJECT.org.generated.inspecenumproperties.InSpecEnumPropertiesMultiple.MC] );
    var inSpecEnum = builder.build();

    expect( inSpecEnum.single().name ).toEqual( "SA" );
    expect( inSpecEnum.multiple()[0].name ).toEqual( "MB" );
    expect( inSpecEnum.multiple()[1].name ).toEqual( "MC" );
    expect( inSpecEnum.multiple().length ).toEqual( 2 );
} );

test( 'assert object immutable', () => {
    var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.InSpecEnumPropertiesBuilder();
    builder.single( window.FLEXIO_IMPORT_OBJECT.org.generated.inspecenumproperties.InSpecEnumPropertiesSingle.SA );
    builder.multiple( [window.FLEXIO_IMPORT_OBJECT.org.generated.inspecenumproperties.InSpecEnumPropertiesMultiple.MB, window.FLEXIO_IMPORT_OBJECT.org.generated.inspecenumproperties.InSpecEnumPropertiesMultiple.MC] );
    var inSpecEnum = builder.build();

    expect( () => {
        inSpecEnum._single = window.FLEXIO_IMPORT_OBJECT.org.generated.inspecenumproperties.InSpecEnumPropertiesSingle.SB;
    } ).toThrow( TypeError );
} );

test( 'assert object frozen', () => {
    var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.InSpecEnumPropertiesBuilder();
    builder.single( window.FLEXIO_IMPORT_OBJECT.org.generated.inspecenumproperties.InSpecEnumPropertiesSingle.SA );
    builder.multiple( [window.FLEXIO_IMPORT_OBJECT.org.generated.inspecenumproperties.InSpecEnumPropertiesMultiple.MB, window.FLEXIO_IMPORT_OBJECT.org.generated.inspecenumproperties.InSpecEnumPropertiesMultiple.MC] );
    var inSpecEnum = builder.build();

    expect( () => {
        inSpecEnum.toto = "toto";
    } ).toThrow( TypeError );
} );

test( 'test serialization', () => {
    var builder = new window.FLEXIO_IMPORT_OBJECT.org.generated.InSpecEnumPropertiesBuilder();
    builder.single( window.FLEXIO_IMPORT_OBJECT.org.generated.inspecenumproperties.InSpecEnumPropertiesSingle.SA );
    builder.multiple( [window.FLEXIO_IMPORT_OBJECT.org.generated.inspecenumproperties.InSpecEnumPropertiesMultiple.MB, window.FLEXIO_IMPORT_OBJECT.org.generated.inspecenumproperties.InSpecEnumPropertiesMultiple.MC] );
    var inSpecEnum = builder.build();
    expect( JSON.stringify( inSpecEnum ) ).toBe( "{\"single\":\"SA\",\"multiple\":[\"MB\",\"MC\"]}" );
} );

test( 'test deserialization', () => {
    var json = "{\"single\":\"SA\",\"multiple\":[\"MB\",\"MC\"]}";
    var inSpecEnum = window.FLEXIO_IMPORT_OBJECT.org.generated.InSpecEnumPropertiesBuilder.fromJson( json );
    expect( inSpecEnum.single().name ).toEqual( "SA" );
    expect( inSpecEnum.multiple()[0].name ).toEqual( "MB" );
    expect( inSpecEnum.multiple()[1].name ).toEqual( "MC" );
    expect( inSpecEnum.multiple().length ).toEqual( 2 );
} );
