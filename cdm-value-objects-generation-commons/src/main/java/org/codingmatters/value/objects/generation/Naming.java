package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.ClassName;
import org.codingmatters.value.objects.reader.utils.ValueObjectNaming;
import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.vals.Val;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;

import java.util.LinkedList;
import java.util.function.Function;

/**
 * Created by nelt on 5/6/17.
 */
public class Naming extends ValueObjectNaming {


    public boolean isArbitraryObject(TypeDeclaration typeDeclaration) {
        return typeDeclaration.type().equals("object") &&
                ((ObjectTypeDeclaration)typeDeclaration).properties().isEmpty() && typeDeclaration.annotations().stream().noneMatch( annot->annot.annotation().name().equals( "already-defined" )  );
    }

    public boolean isArbitraryObjectArray(TypeDeclaration typeDeclaration) {
        if(typeDeclaration instanceof ArrayTypeDeclaration) {
            ArrayTypeDeclaration arrayDeclaration = (ArrayTypeDeclaration) typeDeclaration;
            System.out.println(arrayDeclaration.items().type() + "-" + arrayDeclaration.name() + "-" + arrayDeclaration.annotations());
            if(arrayDeclaration.items().type() == null) {
                return "object[]".equals(arrayDeclaration.items().name());
            }
            return "object".equals(arrayDeclaration.items().type()) &&
                    ((ObjectTypeDeclaration)arrayDeclaration.items()).properties().isEmpty();
        } else {
            return false;
        }
    }

    public String arbitraryObjectImpl(TypeDeclaration typeDeclaration) {
        if(typeDeclaration.annotations() != null) {
            for (AnnotationRef annotation : typeDeclaration.annotations()) {
                if(annotation.name().equalsIgnoreCase("(object-impl)")) {
                    if(annotation.structuredValue().value().equals("Val")) {
                        return Val.class.getName();
                    }
                }
            }
        }
        return ObjectValue.class.getName();
    }

    public  boolean isAlreadyDefined(TypeDeclaration typeDeclaration) {
        if(typeDeclaration.annotations() == null) return false;
        for (AnnotationRef annotation : typeDeclaration.annotations()) {
            if (annotation.name().equalsIgnoreCase("(already-defined)")) {
                return true;
            }
        }
        return false;
    }

    public  boolean isAlreadyDefinedEnum(TypeDeclaration typeDeclaration) {
        if(typeDeclaration.annotations() == null) return false;
        for (AnnotationRef annotation : typeDeclaration.annotations()) {
            if (annotation.name().equalsIgnoreCase("(already-defined-enum)")) {
                return true;
            }
        }
        return false;
    }

    public String alreadyDefined(TypeDeclaration typeDeclaration) {
        if(typeDeclaration.annotations() == null) return null;
        for (AnnotationRef annotation : typeDeclaration.annotations()) {
            if (annotation.name().equalsIgnoreCase("(already-defined)")) {
                return annotation.structuredValue().value().toString();
            }
        }
        return null;
    }

    public String alreadyDefinedEnum(TypeDeclaration typeDeclaration) {
        if(typeDeclaration.annotations() == null) return null;
        for (AnnotationRef annotation : typeDeclaration.annotations()) {
            if (annotation.name().equalsIgnoreCase("(already-defined-enum)")) {
                return annotation.structuredValue().value().toString();
            }
        }
        return null;
    }

    public ClassName alreadyDefinedClass(TypeDeclaration typeDeclaration) {
        String alreadyDefined = this.alreadyDefined(typeDeclaration);
        System.out.println("ALREADY : " + alreadyDefined + " from : name=" + typeDeclaration.name() + " type=" + typeDeclaration.type());
        String packageName = alreadyDefined.substring(0, alreadyDefined.lastIndexOf("."));
        String className = alreadyDefined.substring(alreadyDefined.lastIndexOf(".") + 1);
        return ClassName.get(packageName, this.type(className));
    }

    public ClassName alreadyDefinedWriter(TypeDeclaration typeDeclaration) {
        String alreadyDefined = this.alreadyDefined(typeDeclaration);
        String packageName = alreadyDefined.substring(0, alreadyDefined.lastIndexOf("."));
        String className = alreadyDefined.substring(alreadyDefined.lastIndexOf(".") + 1);
        return ClassName.get(packageName + ".json", this.type(className, "Writer"));
    }

    public ClassName alreadyDefinedReader(TypeDeclaration typeDeclaration) {
        String alreadyDefined = this.alreadyDefined(typeDeclaration);
        String packageName = alreadyDefined.substring(0, alreadyDefined.lastIndexOf("."));
        String className = alreadyDefined.substring(alreadyDefined.lastIndexOf(".") + 1);
        return ClassName.get(packageName + ".json", this.type(className, "Reader"));
    }

    public boolean isArray(TypeDeclaration type) {
        if(type.type().endsWith("[]")) return true;
        if(type instanceof ArrayTypeDeclaration) return true;

        return false;
    }

    public String apiName( String apiTitle ) {
        return apiTitle.toLowerCase().replaceAll( "\\s+", "-" );
    }

}
