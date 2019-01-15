package org.codingmatters.value.objects.js.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.codingmatters.value.objects.js.error.SyntaxError;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

public class YamlSpecParser {

    public static final ObjectMapper MAPPER = new ObjectMapper( new YAMLFactory() );
    private Stack<String> context;

    public ParsedYAMLSpec parse( InputStream inputStream ) throws SyntaxError {
        Map<String, ?> root = null;
        try {
            root = MAPPER.readValue( inputStream, Map.class );
            return this.extractValueObjects( root );
        } catch( IOException e ) {
            throw new SyntaxError( e );
        }
    }

    private ParsedYAMLSpec extractValueObjects( Map<String, ?> valueSpecs ) throws SyntaxError {
        ParsedYAMLSpec parsedYAMLSpec = new ParsedYAMLSpec();
        for( String valueObjectName : valueSpecs.keySet() ) {
            this.context = new Stack<>();
            parsedYAMLSpec.valueObjects().add( this.parseValueObject( valueSpecs, valueObjectName ) );
        }
        return parsedYAMLSpec;
    }

    private ParsedValueObject parseValueObject( Map<String, ?> valueSpecs, String valueObjectName ) throws SyntaxError {
        this.context.push( valueObjectName );
        ParsedValueObject valueObject = new ParsedValueObject( NamingUtils.nestedTypeName( context ) );
        Map<String, ?> properties = (Map<String, ?>) valueSpecs.get( valueObjectName );
        if( properties != null ) {
            parseProperties( valueObject, properties );
        }
        this.context.pop();
        return valueObject;
    }

    private void parseProperties( ParsedValueObject valueObject, Map<String, ?> properties ) throws SyntaxError {
        for( String propertyName : properties.keySet() ) {
            valueObject.properties().add( new ValueObjectProperty(
                    propertyName,
                    this.parseType( propertyName, properties.get( propertyName ) )
            ) );
        }
    }

    private ValueObjectType parseType( String propertyName, Object object ) throws SyntaxError {
        this.context.push( propertyName );
        try {
            if( object instanceof String ) {
                if( isPrimitiveType( (String) object ) ) {
                    return new ValueObjectTypePrimitiveType( (String) object );
                } else if( isInternalValueObject( (String) object ) ) {
                    return new ObjectTypeInSpecValueObject( ((String) object).substring( 1 ) );
                } else {
                    throw new SyntaxError( "Cannot parse this type" );
                }
            } else if( object instanceof Map ) {
                if( isList( (Map) object ) ) {
                    return this.parseList( (Map) object );
                } else if( isEnum( (Map) object ) ) {
                    return this.parseEnum( (Map) object );
                } else if( isExternalValueObject( (Map) object ) ) {
                    return new ObjectTypeExternalValue( (String) ((Map) object).get( "$value-object" ) );
                } else {
                    return this.parseNestedTypeProperty( (Map) object );
                }
            } else {
                throw new SyntaxError( "Cannot parse this value object property: " + propertyName );
            }
        } finally {
            this.context.pop();
        }
    }

    private boolean isInternalValueObject( String object ) {
        return object.startsWith( "$" );
    }

    private boolean isExternalValueObject( Map object ) {
        return object.keySet().size() == 1 && (object.get( "$value-object" ) != null);
    }

    private boolean isPrimitiveType( String type ) {
        return ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.from( type ) != null;
    }

    private boolean isEnum( Map object ) {
        return object.keySet().size() == 1 && (object.get( "$enum" ) != null);
    }

    private boolean isList( Map object ) {
        return object.keySet().size() == 1 && (object.get( "$list" ) != null || object.get( "$list" ) != null);
    }

    private ValueObjectType parseList( Map object ) throws SyntaxError {
        Object listType = object.getOrDefault( "$list", object.get( "$set" ) );
        if( listType == null ) {
            throw new SyntaxError( "The list cannot be parsed" );
        }
        ValueObjectType type = parseType( context.peek(), listType );
        String name = NamingUtils.camelCase( context.get( context.size() - 2 ) ) + NamingUtils.camelCase( context.get( context.size() - 1 ) ) + "List";
        return new ValueObjectTypeList( name, type, context.subList( 0, 1 ) );
    }

    private ValueObjectType parseEnum( Map object ) throws SyntaxError {
        if( object.get( "$enum" ) instanceof String ) {
            String enumValue = (String) object.get( "$enum" );
            if( enumValue.contains( "," ) ) {
                String name = NamingUtils.camelCase( context.get( 0 ) ) + NamingUtils.camelCase( context.get( 1 ) );
                return new YamlEnumInSpecEnum( name, context, Arrays.stream( enumValue.split( "," ) ).map( field->field.trim().toUpperCase() ).collect( Collectors.toList() ) );
            }
        } else if( object.get( "$enum" ) instanceof Map ) {
            String enumReference = (String) ((Map) object.get( "$enum" )).get( "$type" );
            return new YamlEnumExternalEnum( enumReference );
        }
        throw new SyntaxError( "Cannot parse this enum" );
    }

    private ValueObjectType parseNestedTypeProperty( Map<String, ?> properties ) throws SyntaxError {
        List<String> subList = this.context.subList( this.context.size() - 2, this.context.size() );
        ParsedValueObject nestValueObject = new ParsedValueObject( NamingUtils.nestedTypeName( subList ) );
        this.parseProperties( nestValueObject, properties );
        return new ObjectTypeNested( nestValueObject, String.join( ".", this.context.subList( this.context.size() - 2, this.context.size() - 1 ) ) );
    }

}
