package org.codingmatters.value.objects.js.generator.packages;

import org.codingmatters.value.objects.js.generator.GenerationException;

import java.util.*;

public class PackageConfiguration {

    private final String name;
    private final PackageConfiguration parent;
    private final Set<PackageConfiguration> subPackages;
    private final Set<String> classes;
    private final Set<String> lists;

    public PackageConfiguration( PackageConfiguration parent, String name ) {
        if( name.contains( "." ) ){
            String[] packageParts = name.split( "\\." );
            this.name = packageParts[0];
            this.subPackages = new HashSet<>();
            String[] elements = Arrays.copyOfRange( packageParts, 1, packageParts.length );
            subPackages.add( new PackageConfiguration( this, String.join( ".", elements ) ) );
        } else {
            this.name = name;
            this.subPackages = new HashSet<>();
        }
        this.lists = new HashSet<>();
        this.classes = new HashSet<>();
        this.parent = parent;
    }

    public PackageConfiguration( String name ) {
        this( null, name );
    }

    public void addClass( String currentPackage, String objectName ) throws GenerationException {
        recursiveAddition( currentPackage, objectName, OBJECT_TYPE.CLASS );
    }

    public void addList( String currentPackage, String objectName ) throws GenerationException {
        recursiveAddition( currentPackage, objectName, OBJECT_TYPE.LIST );
    }

    private void recursiveAddition( String currentPackage, String objectName, OBJECT_TYPE type ) throws GenerationException {
        if( currentPackage.contains( "." ) ){
            String[] packageParts = currentPackage.split( "\\." );
            PackageConfiguration subPackage = subPackage( packageParts[1] );
            if( subPackage == null ){
                throw new GenerationException( "Cannot add class " + currentPackage + "/" + objectName + " sub package is null" );
            }
            subPackage.recursiveAddition( currentPackage.substring( packageParts[0].length() + 1 ), objectName, type );
        } else if( currentPackage.equals( this.name ) ){
            type.add( this, objectName );
        } else {
            throw new GenerationException( "Cannot add class " + currentPackage + "/" + objectName + " sub package is null" );
        }
    }

    public PackageConfiguration subPackage( String rootPackage ) {
        Optional<PackageConfiguration> subPackage = this.subPackages.stream().filter( pack->pack.name.equals( rootPackage ) ).findFirst();
        if( subPackage.isPresent() ){
            return subPackage.get();
        } else {
            PackageConfiguration newSubPackage = new PackageConfiguration( this, rootPackage );
            this.subPackages.add( newSubPackage );
            return newSubPackage;
        }
    }

    public PackageConfiguration[] subPackages() {
        return subPackages.toArray( new PackageConfiguration[0] );
    }

    public String[] classes() {
        return this.classes.toArray( new String[classes.size()] );
    }

    public String name() {
        return name;
    }

    public String[] lists() {
        return this.lists.toArray( new String[lists.size()] );
    }

    public String fullName() {
        if( parent == null ){
            return this.name;
        } else {
            return parent.fullName() + "." + this.name;
        }
    }

    public String wrapInObject( String object ) {
        if( parent == null ){
            return "{\"" + this.name + "\":" + object + "}";
        } else {
            return parent.wrapInObject( "{\"" + this.name + "\":" + object + "}" );
        }
    }

    private enum OBJECT_TYPE {
        CLASS {
            @Override
            public void add( PackageConfiguration packageConfiguration, String object ) {
                packageConfiguration.classes.add( object );
            }
        },
        LIST {
            @Override
            public void add( PackageConfiguration packageConfiguration, String object ) {
                packageConfiguration.lists.add( object );
            }
        };

        public abstract void add( PackageConfiguration packageConfiguration, String object );
    }

}
