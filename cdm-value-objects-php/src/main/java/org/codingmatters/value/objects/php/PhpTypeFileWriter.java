package org.codingmatters.value.objects.php;


import org.codingmatters.value.objects.generation.preprocessor.PackagedValueSpec;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.codingmatters.value.objects.spec.ValueSpec;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class PhpTypeFileWriter {

    private final ValueSpec spec;
    private final String packageName;
    private final File targetDirectory;
    private final String fileName;
    private final BufferedWriter writer;
    private final String indent = "    ";
    private final String objectName;
    private final Set<String> imports;

    public PhpTypeFileWriter( File targetDirectory, String packageName, PackagedValueSpec valueInterface ) throws IOException {
        if( (!targetDirectory.exists() && !targetDirectory.mkdirs()) || !targetDirectory.isDirectory() ) {
            throw new IOException( "Target directory not exist or is not a directory" );
        }
        this.spec = valueInterface.valueSpec();
        this.packageName = packageName;
        this.targetDirectory = targetDirectory;
        this.objectName = firstLetterUpperCase( valueInterface.valueSpec().name() );
        this.fileName = objectName + ".php";
        String targetFile = String.join( "/", this.targetDirectory.getPath(), fileName );
        this.writer = new BufferedWriter( new FileWriter( targetFile ) );
        this.imports = new HashSet<>();
    }

    private String firstLetterUpperCase( String name ) {
        return name.substring( 0, 1 ).toUpperCase( Locale.ENGLISH ) + name.substring( 1 ).toLowerCase( Locale.ENGLISH );
    }

    public void write() throws IOException {
        writer.write( "<?php" );
        twoLine( 0 );

        writer.write( "namespace " + String.join( "\\", packageName.split( "\\." ) ) + ";" );
        twoLine( 0 );

        //TODO import

        writer.write( "class " + this.objectName + " {" );
        twoLine( 1 );

        for( PropertySpec fieldSpec : spec.propertySpecs() ) {
            writer.write( "private $" + fieldSpec.name() + ";" );
            newLine( 1 );
        }

        for( PropertySpec fieldSpec : spec.propertySpecs() ) {
            this.addGetter( fieldSpec );
        }
        newLine( 1 );

        for( PropertySpec fieldSpec : spec.propertySpecs() ) {
            this.addSetter( fieldSpec );
        }
        newLine( 0 );
        writer.write( "}" );

        writer.flush();
        writer.close();
    }

    private void twoLine( int indentSize ) throws IOException {
        newLine( 0 );
        newLine( indentSize );
    }

    void addSetter( PropertySpec fieldSpec ) throws IOException {
        String fieldName = fieldSpec.name();
        String fieldType = getTypeName( fieldSpec );

        writer.write( String.format(
                "\n    public function with%s( %s $%s ){\n" +
                        "        $this->%s = $%s;\n" +
                        "        return $this;\n" +
                        "    }\n"
                , firstLetterUpperCase( fieldName ), fieldType, fieldName, fieldName, fieldName )
        );
    }

    private String getTypeName( PropertySpec fieldSpec ) throws IOException {
        if( fieldSpec.typeSpec().typeKind() == TypeKind.JAVA_TYPE ) {
            return TypeTokenPhp.parse( fieldSpec.typeSpec().typeRef() ).getImplementationType();
        } else if( fieldSpec.typeSpec().typeKind() == TypeKind.IN_SPEC_VALUE_OBJECT ) {
            return firstLetterUpperCase( fieldSpec.typeSpec().typeRef() );
        } else if( fieldSpec.typeSpec().typeKind() == TypeKind.EXTERNAL_VALUE_OBJECT ) {
            // TODO Got package name to import here
            return firstLetterUpperCase(
                    fieldSpec.typeSpec().typeRef().substring( fieldSpec.typeSpec().typeRef().lastIndexOf( "." ) + 1 )
            );
        } else {
            throw new IOException( "Impossible to recognize type : " + fieldSpec.typeSpec().typeRef() );
        }
    }

    private void addGetter( PropertySpec fieldSpec ) throws IOException {
        String fieldName = fieldSpec.name();
        writer.write( String.format(
                "\n    public function %s(){\n" +
                        "        return $this->%s;\n" +
                        "    }\n"
                , fieldName, fieldName )
        );
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

}
