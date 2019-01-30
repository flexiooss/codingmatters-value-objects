package org.codingmatters.value.objects.js.generator.packages;

import org.codingmatters.value.objects.js.generator.GenerationException;
import org.codingmatters.value.objects.js.generator.JsFileWriter;

public class PackageFilesGenerator {

    private final PackageFilesBuilder filesBuilder;
    private final String targetDirectory;

    public PackageFilesGenerator( PackageFilesBuilder filesBuilder, String targetDirectory ) {
        this.targetDirectory = targetDirectory;
        this.filesBuilder = filesBuilder;
    }

    public void generateFiles() throws GenerationException {
        generateFilesRecursively( filesBuilder.packageConfig().values().toArray( new PackageConfiguration[0] ) );
    }

    private void generateFilesRecursively( PackageConfiguration[] packages ) throws GenerationException {
        for( PackageConfiguration rootPackage : packages ){
            generatePackageFile( rootPackage );
            generateFilesRecursively( rootPackage.subPackages() );
        }
    }

    private void generatePackageFile( PackageConfiguration rootPackage ) throws GenerationException {
        String targetFile = targetDirectory + "/" + rootPackage.fullName().replace( ".", "/" ) + "/package.js";
        try( JsFileWriter fileWriter = new JsFileWriter( targetFile ) ) {
            fileWriter.line( "import{ FLEXIO_IMPORT_OBJECT, deepKeyAssigner } from 'flexio-jshelpers' " );

            for( String className : rootPackage.classes() ){
                String builder = className + "Builder";
                fileWriter.line( "import {" + className + ", " + builder + "} from \"./" + className + "\";" );
            }
            for( String className : rootPackage.lists() ){
                fileWriter.line( "import {" + className + "} from \"./" + className + "\";" );
            }
            fileWriter.newLine();
            for( String classe : rootPackage.classes() ){
                String builder = classe + "Builder";
                fileWriter.line( line( rootPackage, classe ) );
                fileWriter.line( line( rootPackage, builder ) );
            }
            for( String classe : rootPackage.lists() ){
                fileWriter.line( line( rootPackage, classe ) );
            }
            fileWriter.newLine();
            for( PackageConfiguration subPackage : rootPackage.subPackages() ){
                fileWriter.line( "import './" + subPackage.name() + "/package';" );
            }
        } catch( Exception e ){
            throw new GenerationException( "Error generating package files", e );
        }
    }

    private String line( PackageConfiguration rootPackage, String classe ) {
        return "deepKeyAssigner( window.FLEXIO_IMPORT_OBJECT, '" + rootPackage.fullName() + "." + classe + "' ," + classe + " );";
    }

}
