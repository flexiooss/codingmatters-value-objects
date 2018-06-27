package org.codingmatters.value.objects.php;

import org.codingmatters.value.objects.exception.SpecSyntaxException;
import org.codingmatters.value.objects.spec.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

import static org.codingmatters.value.objects.reader.ContextSpecParser.LIST_MARK;
import static org.codingmatters.value.objects.reader.ContextSpecParser.SET_MARK;
import static org.codingmatters.value.objects.reader.ContextSpecParser.VALUE_OBJECT_MARK;
import static org.codingmatters.value.objects.reader.ContextSpecParser.ENUM_MARK;
import static org.codingmatters.value.objects.reader.ContextSpecParser.TYPE_MARK;
import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;

public class ContextSpecParserPhp {

    private static final Pattern JAVA_IDENTIFIER_PATTERN = Pattern.compile( "\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*" );
    private static final Pattern FULLY_QUALIFIED_CLASS_NAME_PATTERN = Pattern.compile( JAVA_IDENTIFIER_PATTERN.pattern() + "(\\." + JAVA_IDENTIFIER_PATTERN.pattern() + ")+" );

    private final Map<String, ?> root;
    private Stack<String> context;
    private List<String> imports;

    public ContextSpecParserPhp( Map<String, ?> root ) {
        this.root = root;
    }

    public Spec parse() throws SpecSyntaxException {
        this.context = new Stack<>();
        this.imports = new ArrayList<>();
        Spec.Builder spec = Spec.spec();
        for( String valueName : root.keySet() ) {
            spec.addValue( this.createValueSpec( valueName ) );
        }
        return spec.build();
    }

    private ValueSpec.Builder createValueSpec( String valueName ) throws SpecSyntaxException {
        this.context.push( valueName );
        ValueSpec.Builder value = valueSpec().name( valueName );
        Map<String, ?> properties = (Map<String, ?>) root.get( valueName );
        if( properties != null ) {
            for( String propertyName : properties.keySet() ) {
                value.addProperty( this.createPropertySpec( propertyName, properties.get( propertyName ) ) );
            }
        }
        return value;
    }

    private PropertySpec.Builder createPropertySpec( String propertyName, Object object ) throws SpecSyntaxException {
        this.context.push( propertyName );
        try {
            PropertyTypeSpec.Builder typeSpec;

            PropertyCardinality cardinality;
            if( object instanceof Map && (((Map) object).containsKey( LIST_MARK ) || ((Map) object).containsKey( SET_MARK )) ) {
                cardinality = PropertyCardinality.LIST;
            } else {
                cardinality = PropertyCardinality.SINGLE;
            }

            if( object instanceof String ) {
                typeSpec = this.typeForString( (String) object );
            } else if( object instanceof Map && ((Map) object).containsKey( VALUE_OBJECT_MARK ) ) {
                throw new SpecSyntaxException( "Not implemented yet", this.context );
//                typeSpec = this.typeForString( (String) ((Map) object).get( VALUE_OBJECT_MARK ) )
//                        .typeKind( TypeKind.EXTERNAL_VALUE_OBJECT );
            } else if( object instanceof Map && ((Map) object).containsKey( ENUM_MARK ) ) {
                throw new SpecSyntaxException( "Not implemented yet", this.context );
//                typeSpec = this.enumTypeSpec( object );
            } else if( object instanceof Map && ((Map) object).containsKey( TYPE_MARK ) ) {
                throw new SpecSyntaxException( "Not implemented yet", this.context );
//                typeSpec = this.typeForString( (String) ((Map) object).get( TYPE_MARK ) );
            } else if( object instanceof Map ) {
                typeSpec = type().typeKind( TypeKind.EMBEDDED );
                typeSpec.embeddedValueSpec( this.parseAnonymousValueSpec( ((Map) object) ) );
            } else {
                throw new SpecSyntaxException( String.format( "unexpected specification for property : %s", object ), this.context );
            }

            typeSpec.cardinality( cardinality );

            // TODO HINTS ?

            return property()
                    .name( propertyName )
                    .type( typeSpec );
        } finally {
            this.context.pop();
        }
    }

    private PropertyTypeSpec.Builder typeForString( String type ) throws SpecSyntaxException {
        if( type.startsWith( "$" ) ) {
            if( this.root.keySet().contains( type.substring( 1 ) ) ) {
                return type()
                        .typeRef( type.substring( 1 ) )
                        .typeKind( TypeKind.IN_SPEC_VALUE_OBJECT )
                        ;
            } else {
                throw new SpecSyntaxException( "undeclared referenced type for {context} : a referenced type should be declared in the same spec", this.context );
            }
        } else if( FULLY_QUALIFIED_CLASS_NAME_PATTERN.matcher( type ).matches() ) {
            return type()
                    .typeRef( type )
                    .typeKind( TypeKind.JAVA_TYPE )
                    ;
        } else {
            return type()
                    .typeRef( this.parseType( type ).getImplementationType() )
                    .typeKind( TypeKind.JAVA_TYPE )
                    ;
        }
    }

    private TypeTokenPhp parseType( String typeSpec ) throws SpecSyntaxException {
        TypeTokenPhp type;
        try {
            type = TypeTokenPhp.parse( typeSpec );
        } catch( IllegalArgumentException e ) {
            throw new SpecSyntaxException(
                    String.format( "invalid type for property {context} : %s, should be one of %s, a reference to an in spec declared type ($type notation) or a fully qualified class name (default package classes cannot be used).", typeSpec, TypeToken.validTypesSpec() ),
                    this.context );
        }
        return type;
    }


    private AnonymousValueSpec parseAnonymousValueSpec(Map value) throws SpecSyntaxException {
        AnonymousValueSpec.Builder result = AnonymousValueSpec.anonymousValueSpec();
        for (Object name : value.keySet()) {
            result.addProperty(this.createPropertySpec((String) name, value.get(name)));
        }
        return result.build();
    }

}
