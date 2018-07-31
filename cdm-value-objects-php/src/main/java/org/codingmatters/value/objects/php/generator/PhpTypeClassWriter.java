package org.codingmatters.value.objects.php.generator;


import org.codingmatters.value.objects.php.phpmodel.PhpEnum;
import org.codingmatters.value.objects.php.phpmodel.PhpMethod;
import org.codingmatters.value.objects.php.phpmodel.PhpPackagedValueSpec;
import org.codingmatters.value.objects.php.phpmodel.PhpPropertySpec;
import org.codingmatters.value.objects.spec.PropertyCardinality;
import org.codingmatters.value.objects.spec.TypeKind;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class PhpTypeClassWriter {

    public final String packageName;
    private final File targetDirectory;
    private final String fileName;
    public final BufferedWriter writer;
    public final String objectName;
    private final Set<String> imports;
    private final String indent = "    ";

    public PhpTypeClassWriter( File targetDirectory, String packageName, String name ) throws IOException {
        if( (!targetDirectory.exists() && !targetDirectory.mkdirs()) || !targetDirectory.isDirectory() ) {
            throw new IOException( "Target directory not exist or is not a directory" );
        }
        this.packageName = packageName;
        this.targetDirectory = targetDirectory;
        this.objectName = firstLetterUpperCase( name );
        this.fileName = objectName + ".php";
        String targetFile = String.join( "/", this.targetDirectory.getPath(), fileName );
//        System.out.println( "Generating in " + targetFile );
        this.writer = new BufferedWriter( new FileWriter( targetFile ) );
        this.imports = new HashSet<>();
    }


    public void writeEnum( PhpEnum enumValue ) throws IOException {
        startPhpFile();

        writer.write( "use \\Exception;" );
        twoLine( 0 );
        writer.write( "use \\JsonSerializable;" );
        twoLine( 0 );

        writer.write( "class " + enumValue.name() + " implements JsonSerializable {" );
        twoLine( 1 );
        writer.write( "protected $value;" );
        twoLine( 1 );
        writer.write( "private function __construct( $value ){" );
        newLine( 2 );
        writer.write( "$this->value = $value;" );
        newLine( 1 );
        writer.write( "}" );
        twoLine( 1 );
        writer.write( "public function value(){" );
        newLine( 2 );
        writer.write( "return $this->value;" );
        newLine( 1 );
        writer.write( "}" );
        newLine( 1 );

        for( String value : enumValue.enumValues() ) {
            newLine( 1 );
            writer.write( "public static function " + value + "(): " + enumValue.name() + " { " );
            writer.newLine();
            indent( 2 );
            writer.write( "return new " + enumValue.name() + "( '" + value + "' );" );
            writer.newLine();
            indent( 1 );
            writer.write( "}" );
            newLine( 0 );
        }
        newLine( 1 );
        writer.write( "public static function valueOf( string $value ): " + enumValue.name() + " {" );
        newLine( 2 );
        writer.write( "if( in_array($value, " + enumValue.name() + "::values())){" );
        newLine( 3 );
        writer.write( "return new " + enumValue.name() + "( $value );" );
        newLine( 2 );
        writer.write( "} else {" );
        newLine( 3 );
        writer.write( "throw new Exception( 'No enum constant '.$value );" );
        newLine( 2 );
        writer.write( "}" );
        newLine( 1 );
        writer.write( "}" );
        twoLine( 1 );

        writer.write( "public static function values(){" );
        newLine( 2 );
        writer.write( "return array('" + String.join( "', '", enumValue.enumValues() ) + "');" );
        newLine( 1 );
        writer.write( "}" );

        newLine( 1 );
        writer.write( "public function jsonSerialize() {" );
        newLine( 2 );
        writer.write( "return $this->value;" );
        newLine( 1 );
        writer.write( "}" );

        newLine( 0 );
        writer.write( "}" );

        writer.flush();
        writer.close();
    }

    private void startPhpFile() throws IOException {
        writer.write( "<?php" );
        twoLine( 0 );

        writer.write( "namespace " + String.join( "\\", this.packageName.split( "\\." ) ) + ";" );
        twoLine( 0 );
    }

    public void writeValueObject( PhpPackagedValueSpec spec, boolean serializable ) throws IOException {
        startPhpFile();

        for( String importation : spec.imports() ) {
            writer.write( "use " + importation.replace( ".", "\\" ) + ";" );
            writer.newLine();
        }
        writer.newLine();

        writer.write( "class " + this.objectName );
        if( spec.extender() != null ) {
            writer.write( " extends " + spec.extender().typeRef() );
        }
        if( serializable ) {
            writer.write( " implements \\JsonSerializable" );
        }
        writer.write( " {" );
        twoLine( 1 );

        for( PhpPropertySpec fieldSpec : spec.propertySpecs() ) {
            writer.write( "private $" + getFieldName( fieldSpec ) + ";" );
            newLine( 1 );
        }
        newLine( 0 );

        for( PhpMethod phpMethod : spec.methods() ) {
            indent( 1 );
            writer.write( "public function " );
            writer.write( phpMethod.name() );
            writer.write( "(" );
            writer.write( String.join( ", ", phpMethod.parameters().stream().map( param->param.type() + " $" + param.name() ).collect( Collectors.toList() ) ) );
            writer.write( ")" );
            String returnType = phpMethod.type();
            if( returnType != null ) {
                writer.write( ": " + returnType );
            }
            writer.write( " {" );
            writer.newLine();
            for( String instruction : phpMethod.instructions() ) {
                indent( 2 );
                writer.write( instruction );
                writer.write( ";" );
                writer.newLine();
            }
            indent( 1 );
            writer.write( "}" );
            twoLine( 0 );
        }
        if( serializable ) {
            indent( 1 );
            writer.write( "public function jsonSerialize() {" );
            newLine( 2 );
            writer.write( "return get_object_vars($this);" );
            newLine( 1 );
            writer.write( "}" );
            newLine( 0 );
        }
        writer.write( "}" );

        writer.flush();
        writer.close();
    }


    public void writeWriter( PhpPackagedValueSpec spec ) throws IOException {
        startPhpFile();

        writer.write( "use " + spec.packageName().replace( ".", "\\" ) + "\\" + spec.name() + ";" );
        twoLine( 0 );
        writer.write( "class " + this.objectName + " {" );
        twoLine( 1 );
        String objectToWrite = this.objectName.substring( 0, this.objectName.length() - 6 );

        writer.write( "public function write( " + objectToWrite + " $object ) : string {" );
        newLine( 2 );
        writer.write( "return json_encode( $object );" );
        newLine( 1 );
        writer.write( "}" );
        newLine( 0 );
        writer.write( "}" );

        writer.flush();
        writer.close();
    }

    public void writeReader( PhpPackagedValueSpec spec ) throws IOException {
        startPhpFile();

        writer.write( "use " + spec.packageName().replace( ".", "\\" ) + "\\" + spec.name() + ";" );
        writer.newLine();
        for( String importation : spec.imports() ) {
            writer.write( "use " + importation.replace( ".", "\\" ) + ";" );
            writer.newLine();
        }

        twoLine( 0 );
        writer.write( "class " + this.objectName + " {" );
        twoLine( 1 );
        String objectToRead = this.objectName.substring( 0, this.objectName.length() - 6 );
        writer.write( "public function read( string $json ) : " + objectToRead + " {" );
        newLine( 2 );
        writer.write( "$decode = json_decode( $json, true );" );
        newLine( 2 );
        writer.write( "return $this->readArray( $decode );" );
        newLine( 1 );
        writer.write( "}" );
        twoLine( 1 );
        writer.write( "public function readArray( array $decode ) : " + objectToRead + " {" );
        newLine( 2 );
        String resultVar = "$" + firstLetterLowerCase( objectToRead );
        writer.write( resultVar + " = new " + objectToRead + "();" );
        newLine( 2 );
        for( PhpPropertySpec property : spec.propertySpecs() ) {
            if( property.typeSpec().cardinality() == PropertyCardinality.LIST || property.typeSpec().cardinality() == PropertyCardinality.SET ) {
                processFieldList( spec, resultVar, property );
            } else {
                processSingleField( resultVar, property );
            }
        }
        writer.write( "return " + resultVar + ";" );

        newLine( 1 );
        writer.write( "}" );

        twoLine( 0 );
        writer.write( "}" );
        writer.flush();
        writer.close();
    }

    private void processFieldList( PhpPackagedValueSpec spec, String resultVar, PhpPropertySpec property ) throws IOException {
        writer.write( "if( isset( $decode['" + property.name() + "'] )){" );
        newLine( 3 );
        String listType = "\\" + property.typeSpec().typeRef().replace( ".", "\\" );
        writer.write( "$list = new " + listType + "();" );
        newLine( 3 );
        writer.write( "foreach( $decode['" + property.name() + "'] as $item ){" );
        newLine( 4 );
        if( property.typeSpec().typeKind() == TypeKind.ENUM ) {
            writer.write( "$list[] = \\" + property.typeSpec().typeRef().substring( 0, property.typeSpec().typeRef().length() - 4 ).replace( ".", "\\" ) + "::valueOf( $item );" );
        } else if( property.typeSpec().typeKind() == TypeKind.EXTERNAL_VALUE_OBJECT && property.typeSpec().embeddedValueSpec() != null ) {
            if( property.typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind() == TypeKind.JAVA_TYPE ) {
                writer.write( "$list[] = $item;" );
            } else if( "io.flexio.utils.FlexDate".equals( property.typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef() ) ) {
                writer.write( "$list[] = \\io\\flexio\\utils\\FlexDate::parse( $item );" );
            } else {
                writer.write( "$reader = new \\" + getReaderFromReference( property.typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef() ) + "();" );
                newLine( 4 );
                writer.write( "$list[] = $reader->readArray( $item );" );
            }
        } else {
            writer.write( "$list[] = $item;" );
        }
        newLine( 3 );
        writer.write( "}" );
        newLine( 3 );
        writer.write( resultVar + "->with" + firstLetterUpperCase( property.name() ) + "( $list );" );
        newLine( 2 );
        writer.write( "}" );
        newLine( 2 );
    }

    private void processSingleField( String resultVar, PhpPropertySpec property ) throws IOException {
        if( property.typeSpec().typeKind() == TypeKind.ENUM ) {
            writer.write( "if( isset( $decode['" + property.name() + "'] )){" );
            newLine( 3 );
            writer.write( resultVar + "->with" + firstLetterUpperCase( property.name() ) + "( \\" + property.typeSpec().typeRef().replace( ".", "\\" ) + "::valueOf( $decode['" + property.name() + "'] ));" );
            newLine( 2 );
            writer.write( "}" );
            newLine( 2 );
        } else if( "io.flexio.utils.FlexDate".equals( property.typeSpec().typeRef() ) ) {
            writer.write( "if( isset( $decode['" + property.name() + "'] )){" );
            newLine( 3 );
            writer.write( resultVar + "->with" + firstLetterUpperCase( property.name() ) + "( \\io\\flexio\\utils\\FlexDate::parse( $decode['" + property.name() + "'] ));" );
            newLine( 2 );
            writer.write( "}" );
            newLine( 2 );
        } else if( property.typeSpec().typeKind() == TypeKind.IN_SPEC_VALUE_OBJECT || property.typeSpec().typeKind() == TypeKind.EXTERNAL_VALUE_OBJECT ) {
            writer.write( "if( isset( $decode['" + property.name() + "'] )){" );
            newLine( 3 );
            writer.write( "$reader = new \\" + getReaderFromReference( property.typeSpec().typeRef() ) + "();" );
            newLine( 3 );
            writer.write( resultVar + "->with" + firstLetterUpperCase( property.name() ) + "( $reader->readArray( $decode['" + property.name() + "'] ));" );
            newLine( 2 );
            writer.write( "}" );
            newLine( 2 );
        } else {
            writer.write( "if( isset( $decode['" + property.name() + "'] )){" );
            newLine( 3 );
            writer.write( resultVar + "->with" + firstLetterUpperCase( property.name() ) + "( $decode['" + property.name() + "'] );" );
            newLine( 2 );
            writer.write( "}" );
            newLine( 2 );
        }
    }

    private String getReaderFromReference( String typeRef ) {
        int index = typeRef.lastIndexOf( "." );
        return (typeRef.substring( 0, index ) + ".json" + typeRef.substring( index ) + "Reader").replace( ".", "\\" );

    }

    private String getDateClass( String dateClass ) {
        return String.join( "", Arrays.stream( dateClass.split( "-" ) ).map( this::firstLetterUpperCase ).collect( Collectors.toList() ) );
    }

    private boolean isDate( PhpPropertySpec property ) {
        return property.typeSpec().typeRef().equals( "date" ) || property.typeSpec().typeRef().equals( "time" ) || property.typeSpec().typeRef().equals( "date-time" );
    }

    private void twoLine( int indentSize ) throws IOException {
        newLine( 0 );
        newLine( indentSize );
    }

    private void newLine( int indentSize ) throws IOException {
        writer.newLine();
        indent( indentSize );
    }

    private void indent( int indent ) throws IOException {
        for( int i = 0; i < indent; i++ ) {
            writer.write( this.indent );
        }
    }

    private String getFieldName( PhpPropertySpec fieldSpec ) {
        if( fieldSpec.name().equals( "$list" ) ) {
            return fieldSpec.typeSpec().typeRef() + "s";
        }
        return fieldSpec.name();
    }

    private String firstLetterUpperCase( String name ) {
        return name.substring( 0, 1 ).toUpperCase( Locale.ENGLISH ) + name.substring( 1 );
    }


    private String firstLetterLowerCase( String name ) {
        return name.substring( 0, 1 ).toLowerCase( Locale.ENGLISH ) + name.substring( 1 );
    }

    public static String replaceLast( String string, String toReplace, String replacement ) {
        int pos = string.lastIndexOf( toReplace );
        if( pos > -1 ) {
            return string.substring( 0, pos )
                    + replacement
                    + string.substring( pos + toReplace.length(), string.length() );
        } else {
            return string;
        }
    }
}
