package org.codingmatters.value.objects.js.generator;


import org.codingmatters.value.objects.spec.PropertyCardinality;
import org.codingmatters.value.objects.spec.TypeKind;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JsClassWriter {

    private final File targetDirectory;
    private final JsPackagedValueSpec spec;
    private final String fileName;
    private final String objectName;
    private final String targetFile;
    private final String builderName;
    private int indent;
    private BufferedWriter classWriter;

    public JsClassWriter( File packageDestination, JsPackagedValueSpec valueSpec ) {
        this.targetDirectory = packageDestination;
        this.spec = valueSpec;
        this.objectName = Naming.firstLetterUpperCase( valueSpec.name() );
        this.builderName = this.objectName + "Builder";
        this.fileName = objectName + ".js";
        this.targetFile = String.join( "/", this.targetDirectory.getPath(), fileName );
        this.indent = 0;
    }

    public void writeClassAndBuilder() throws IOException {
        File file = new File( targetFile );
        if( !file.exists() ) {
            file.createNewFile();
        }
        try( BufferedWriter writer = new BufferedWriter( new FileWriter( targetFile ) ) ) {
            this.classWriter = writer;
            generateClass();
            newLine();
            newLine();
            generateBuilder();
            classWriter.flush();
        }
    }

    public void writeList( JsPackagedValueSpec listValue ) throws IOException {
        File file = new File( targetFile );
        if( !file.exists() ) {
            file.createNewFile();
        }
        try( BufferedWriter writer = new BufferedWriter( new FileWriter( targetFile ) ) ) {
            this.classWriter = writer;
            generateList( listValue );
            newLine();
            newLine();
            classWriter.flush();
        }
    }

    private void generateList( JsPackagedValueSpec listValue ) throws IOException {
        write( "import { deepFreezeSeal, FlexDate, FlexDateTime, FlexTime, FlexZonedDateTime } from 'flexio-jshelpers' " );
        newLine();
        newLine();
        write( "class " + builderName + " extends Array {" );
        newLine();
        newLine();
        indent();
        write( "constructor( ... args ){" );
        newLine();
        indent();
        write( "super( ... args );" );
        newLine();
        unindent();
        write( "}" );
        newLine();
        newLine();
        write( "fromObject( jsonObject ){" );
        indent();
        newLine();
        write( "for( int i=0; i<jsonObject.length; i++ ){" );
        indent();
        newLine();
//        System.out.println( "EMBED = " + listValue.propertySpecs().get( 0 ).typeSpec().typeRef() );
//        write( "this[i] = " );
        unindent();
        newLine();
        write( "}" );
        unindent();
        newLine();
        write( "}" );
        unindent();
        newLine();
        write( "}" );
        newLine();
        newLine();
        write( "export {" + builderName + "}" );
    }

    private void generateBuilder() throws IOException {
        write( "class " + builderName + " {" );
        newLine();
        newLine();
        indent();

        write( "constructor(){}" );
        newLine();

        writeSetters();

        writeBuildMethod();

        writeFromObjectMethod();
        writeFromJsonMethod();

        unindent();
        newLine();
        write( "}" );

        newLine();
        newLine();

        write( "export {" + builderName + "}" );
    }

    private void writeFromJsonMethod() throws IOException {
        newLine();
        write( "static fromJson( json ) {" );
        indent();
        newLine();
        write( "var jsonObject = JSON.parse( json )" );
        newLine();
        write( "return this.fromObject( jsonObject )" );
        unindent();
        newLine();
        write( "}" );
    }

    private void writeFromObjectMethod() throws IOException {
        newLine();
        write( "static fromObject( jsonObject ) {" );
        indent();
        newLine();
        write( "var builder = new " + builderName + "()" );
        newLine();
        for( JsPropertySpec property : spec.propertySpecs() ) {
            write( "if( jsonObject[\"" + property.realName() + "\"] != undefined ){" );
            newLine();
            indent();
            if( property.typeSpec().cardinality() == PropertyCardinality.SINGLE ) {
                switch( property.typeSpec().typeKind() ) {
                    case JAVA_TYPE:
                        write( "builder." + property.name() + "( jsonObject[\"" + property.realName() + "\"] );" );
                        break;
                    case IN_SPEC_VALUE_OBJECT:
                        String[] split = property.typeSpec().typeRef().split( "\\." );
                        String builderName = split[split.length - 1] + "Builder";
                        write( "builder." + property.name() + "( " + builderName + ".fromObject( jsonObject[\"" + property.realName() + "\"] ));" );
                        break;
                    case EXTERNAL_VALUE_OBJECT:
                        if( "date".equals( property.typeSpec().typeRef() ) ) {
                            write( "builder." + property.name() + "( new FlexDate( jsonObject[\"" + property.realName() + "\"] ));" );
                        } else if( "time".equals( property.typeSpec().typeRef() ) ) {
                            write( "builder." + property.name() + "( new FlexTime( jsonObject[\"" + property.realName() + "\"] ));" );
                        } else if( "date-time".equals( property.typeSpec().typeRef() ) ) {
                            write( "builder." + property.name() + "( new FlexDateTime( jsonObject[\"" + property.realName() + "\"] ));" );
                        } else if( "tz-date-time".equals( property.typeSpec().typeRef() ) ) {
                            write( "builder." + property.name() + "( new FlexZonedDateTime( jsonObject[\"" + property.realName() + "\"] ));" );
                        } else {
                            write( "builder." + property.name() + "( jsonObject[\"" + property.realName() + "\"].toObject() );" ); //TODO
                        }
                        break;
                    case EMBEDDED:
                        break;
                    case ENUM:
                        write( "builder." + property.name() + "( " + Naming.className( property.typeSpec().typeRef() ) + ".enumValueOf( jsonObject[\"" + property.realName() + "\"] ));" );
                        break;
                }
            } else {
                TypeKind typeKind;
                if( property.typeSpec().typeKind() == TypeKind.ENUM ) {
                    typeKind = TypeKind.ENUM;
                } else {
                    typeKind = property.typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind();
                }
                switch( typeKind ) {
                    case JAVA_TYPE:
                        write( "builder." + property.name() + "( jsonObject[\"" + property.realName() + "\"] );" );
                        break;
                    case IN_SPEC_VALUE_OBJECT:
                        break;
                    case EXTERNAL_VALUE_OBJECT:
                        String typeRef = property.typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef();
                        if( "date".equals( typeRef ) ) {
                            write( "builder." + property.name() + "( jsonObject[\"" + property.realName() + "\"].map( date => new FlexDate( date ) ));" );
                        } else if( "time".equals( typeRef ) ) {
                            write( "builder." + property.name() + "( jsonObject[\"" + property.realName() + "\"].map( time => new FlexTime( time ) ));" );
                        } else if( "date-time".equals( typeRef ) ) {
                            write( "builder." + property.name() + "( jsonObject[\"" + property.realName() + "\"].map( datetime => new FlexDateTime( datetime ) ));" );
                        } else if( "tz-date-time".equals( typeRef ) ) {
                            write( "builder." + property.name() + "( jsonObject[\"" + property.realName() + "\"].map( tzDatetime => new FlexZonedDateTime( tzDatetime ) ));" );
                        } else {
                            write( "builder." + property.name() + "( jsonObject[\"" + property.realName() + "\"].map( x=>x.toObject() ));" ); //TODO
                        }
                        break;
                    case EMBEDDED:
                        break;
                    case ENUM:
//                        System.out.println( "TRef = " );
//                        write( "builder." + property.name() + "( jsonObject[\"" + property.realName() + "\"].map( enumeration => " + Naming.className( property.typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef() ) + ".enumValueOf( enumeration )));" );
                        break;
                }
            }
            newLine();
            unindent();
            write( "}" );
            newLine();
        }
        newLine();
        write( "return builder.build();" );
        unindent();
        newLine();
        write( "}" );
    }


    private void writeBuildMethod() throws IOException {
        newLine();
        write( "build(){" );
        indent();
        newLine();

        write( "return new " + this.objectName + "(" +
                String.join( ",", this.spec.propertySpecs().stream().map( prop->"this." + prop.name() ).collect( Collectors.toList() ) ) +
                ")" );
        newLine();
        unindent();
        write( "}" );
        newLine();
    }

    private void writeSetters() throws IOException {
        newLine();
        for( JsPropertySpec fieldSpec : spec.propertySpecs() ) {
            write( fieldSpec.name() + "( " + fieldSpec.name() + ") {" );
            indent();
            newLine();
            write( "this." + fieldSpec.name() + " = " + fieldSpec.name() + ";" );
            newLine();
            write( "return this;" );
            unindent();
            newLine();
            write( "}" );
            newLine();
        }
    }

    private void newLine() throws IOException {
        classWriter.newLine();
    }

    private void generateClass() throws IOException {
        write( "import { deepFreezeSeal, FlexDate, FlexDateTime, FlexTime, FlexZonedDateTime } from 'flexio-jshelpers' " );
        addImports();
        newLine();
        newLine();
        write( "class " + this.objectName + " {" );
        newLine();
        newLine();
        indent();

        generateConstructor();
        writerGetters();
        writeToObjectMethod();
        writeToJsonMethod();

        unindent();
        write( "}" );

        newLine();
        newLine();

        write( "export {" + this.objectName + "}" );
    }

    private void addImports() throws IOException {
        for( JsPropertySpec property : this.spec.propertySpecs() ) {
            if( property.typeSpec().typeKind() == TypeKind.IN_SPEC_VALUE_OBJECT ) {
                newLine();
                String packageName = Naming.findPackage( this.spec.packageName(), property.typeSpec().typeRef() );
                String builderName = Naming.builderName( property.typeSpec().typeRef() );
                String className = Naming.className( property.typeSpec().typeRef() );
                write( "import { " + className + ", " + builderName + " } from '" + packageName + "'" );
            }
            if( property.typeSpec().typeKind() == TypeKind.ENUM ) {
                newLine();
                String packageName = Naming.findPackage( this.spec.packageName(), property.typeSpec().typeRef() );
                String builderName = Naming.builderName( property.typeSpec().typeRef() );
                String className = Naming.className( property.typeSpec().typeRef() );
                write( "import { " + className + " } from '" + packageName + "'" );
            }
        }
    }

    private void writerGetters() throws IOException {
        newLine();
        for( JsPropertySpec fieldSpec : spec.propertySpecs() ) {
            write( fieldSpec.name() + "() {" );
            indent();
            newLine();
            write( "return this." + fieldSpec.name() );
            unindent();
            newLine();
            write( "}" );
            newLine();
        }
    }

    private void generateConstructor() throws IOException {
        List<String> names = spec.propertySpecs().stream().map( prop->prop.name() ).collect( Collectors.toList() );
        write( "constructor ( " + String.join( ", ", names ) + " ){" );
        indent();
        newLine();
        write( "" );
        classWriter.write( String.join( "\n        ", spec.propertySpecs().stream().map( prop->"this." + prop.name() + " = " + prop.name() ).collect( Collectors.toList() ) ) );
        newLine();
        write( "deepFreezeSeal(this)" );
        unindent();
        newLine();
        write( "}" );
        newLine();
    }

    private void writeToObjectMethod() throws IOException {
        write( "toObject() {" );
        indent();
        newLine();
        write( "var jsonObject = {};" );
        newLine();
        for( JsPropertySpec property : this.spec.propertySpecs() ) {
            if( property.typeSpec().cardinality() == PropertyCardinality.SINGLE ) {
                String assign = "jsonObject[\"" + property.realName() + "\"] = ";
                switch( property.typeSpec().typeKind() ) {
                    case JAVA_TYPE:
                        assign += "this." + property.name() + ";";
                        break;
                    case IN_SPEC_VALUE_OBJECT:
                        assign += "this." + property.name() + ".toObject();";
                        break;
                    case EXTERNAL_VALUE_OBJECT:
                        if( ("date".equals( property.typeSpec().typeRef() )) ||
                                ("time".equals( property.typeSpec().typeRef() )) ||
                                ("date-time".equals( property.typeSpec().typeRef() )) ||
                                ("tz-date-time".equals( property.typeSpec().typeRef() )) ) {
                            assign += "this." + property.name() + ";";
                        } else {
                            assign += "this." + property.name() + ".toObject();";
                        }
                        break;
                    case EMBEDDED:
                        assign += "this." + property.name() + ".toObject();";
                        break;
                    case ENUM:
                        assign += "this." + property.name() + ".name;";
                        break;
                }
                write( assign );
                newLine();
            } else {
                TypeKind typeKind;
                if( property.typeSpec().typeKind() == TypeKind.ENUM ) {
                    typeKind = TypeKind.ENUM;
                } else {
                    typeKind = property.typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind();
                }
                String assign = "jsonObject[\"" + property.realName() + "\"] = ";
                switch( typeKind ) {
                    case JAVA_TYPE:
                        assign += "this." + property.name() + ";";
                        break;
                    case IN_SPEC_VALUE_OBJECT:
                        assign += "this." + property.name() + ".map( x=>x.toObject() ); // in spec";
                        break;
                    case EXTERNAL_VALUE_OBJECT:
                        String typeRef = property.typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef();

                        if( ("date".equals( typeRef )) ||
                                ("time".equals( typeRef )) ||
                                ("date-time".equals( typeRef )) ||
                                ("tz-date-time".equals( typeRef )) ) {
                            assign += "this." + property.name() + ";";
                        } else {
                            assign += "this." + property.name() + ".map( extObj => extObj.toObject() ); // Ext";
                        }
                        break;
                    case EMBEDDED:
                        assign += "this." + property.name() + ".map( obj => obj.toObject() ); // embed";
                        break;
                    case ENUM:
                        assign += "this." + property.name() + ".map( enumeration => enumeration.name );";
                        break;
                }
                write( assign );
                newLine();
            }
        }
        write( "return jsonObject;" );
        newLine();
        unindent();
        write( "}" );
        newLine();
    }

    private void writeToJsonMethod() throws IOException {
        write( "toJSON() {" );
        indent();
        newLine();
        write( "return this.toObject();" );
        newLine();
        unindent();
        write( "}" );
        newLine();
    }

    private void indent() {
        indent++;
    }

    private void unindent() {
        indent--;
    }

    private void write( String code ) throws IOException {
        writeIndent();
        classWriter.write( code );
    }

    private void writeIndent() throws IOException {
        for( int i = 0; i < indent; i++ ) {
            classWriter.write( "    " );
        }
    }

}
