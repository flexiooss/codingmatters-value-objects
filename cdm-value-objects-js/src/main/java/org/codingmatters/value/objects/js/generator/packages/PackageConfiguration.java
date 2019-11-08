package org.codingmatters.value.objects.js.generator.packages;

import org.codingmatters.value.objects.js.generator.GenerationException;

import java.util.*;

public class PackageConfiguration {

    private final String name;
    private final PackageConfiguration parent;
    private final Set<PackageConfiguration> subPackages;
    private final Set<ObjectValueConfiguration> classes;
    private final Set<ObjectValueConfiguration> lists;

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

    public static class ObjectValueConfiguration{
        private final String name;
        private final boolean generateList;

        public ObjectValueConfiguration( String name, boolean generateList) {
            this.name = name;
            this.generateList = generateList;
        }

        public String name() {
            return name;
        }

        public boolean generateList() {
            return generateList;
        }

        @Override
        public boolean equals( Object o ) {
            if( this == o ) return true;
            if( !(o instanceof ObjectValueConfiguration) ) return false;
            ObjectValueConfiguration that = (ObjectValueConfiguration) o;
            return generateList == that.generateList &&
                    Objects.equals( name, that.name );
        }

        @Override
        public int hashCode() {
            return Objects.hash( name, generateList );
        }
    }

    public PackageConfiguration( String name ) {
        this( null, name );
    }

    public void addClass( String currentPackage, String objectName, boolean generateList ) throws GenerationException {
        recursiveAddition( currentPackage, objectName, OBJECT_TYPE.CLASS, generateList );
    }

    public void addEnum( String currentPackage, String objectName, boolean generateList ) throws GenerationException {
        recursiveAddition( currentPackage, objectName, OBJECT_TYPE.LIST, generateList );
    }

    private void recursiveAddition( String currentPackage, String objectName, OBJECT_TYPE type, boolean generateList ) throws GenerationException {
        if( currentPackage.contains( "." ) ){
            String[] packageParts = currentPackage.split( "\\." );
            PackageConfiguration subPackage = subPackage( packageParts[1] );
            if( subPackage == null ){
                throw new GenerationException( "Cannot add class " + currentPackage + "/" + objectName + " sub package is null" );
            }
            subPackage.recursiveAddition( currentPackage.substring( packageParts[0].length() + 1 ), objectName, type, generateList );
        } else if( currentPackage.equals( this.name ) ){
            type.add( this, new ObjectValueConfiguration( objectName, generateList ) );
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

    public ObjectValueConfiguration[] classes() {
        return this.classes.toArray( new ObjectValueConfiguration[classes.size()] );
    }

    public String name() {
        return name;
    }

    public ObjectValueConfiguration[] lists() {
        return this.lists.toArray( new ObjectValueConfiguration[lists.size()] );
    }

    public String fullName() {
        if( parent == null ){
            return this.name;
        } else {
            return parent.fullName() + "." + this.name;
        }
    }

    private enum OBJECT_TYPE {
        CLASS {
            @Override
            public void add( PackageConfiguration packageConfiguration, ObjectValueConfiguration object ) {
                packageConfiguration.classes.add( object );
            }
        },
        LIST {
            @Override
            public void add( PackageConfiguration packageConfiguration, ObjectValueConfiguration object ) {
                packageConfiguration.lists.add( object );
            }
        };

        public abstract void add( PackageConfiguration packageConfiguration, ObjectValueConfiguration object );
    }

}
