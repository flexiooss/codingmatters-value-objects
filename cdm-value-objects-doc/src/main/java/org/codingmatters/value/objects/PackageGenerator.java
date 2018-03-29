package org.codingmatters.value.objects;

import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.codingmatters.value.objects.spec.ValueSpec;

import java.io.IOException;
import java.util.*;

public class PackageGenerator {

    static public final String VALUE_OBJECT_STEREOTYPE =  "<< (V,#0099ff) >>";
    static public final String EXT_VALUE_OBJECT_STEREOTYPE =  "<< (V,#0099ff) EXT>>";

    private final List<ValueSpec> valueSpecs;
    private final String packageName;
    private final String prefix;

    public PackageGenerator(List<ValueSpec> valueSpecs, String packageName) {
        this(valueSpecs, packageName, "");
    }

    public PackageGenerator(List<ValueSpec> valueSpecs, String packageName, String prefix) {
        this.valueSpecs = valueSpecs;
        this.packageName = packageName;
        this.prefix = prefix;
    }

    public void generate(FormattedWriter out) throws IOException {
        this.interfaceDeclaration(this.valueSpecs, out);
        Set<String> externalValueObjects = new TreeSet<>();

        for (ValueSpec valueSpec : this.valueSpecs) {
            externalValueObjects.addAll(this.externalValueObjects(valueSpec));
            this.valueClass(valueSpec, out);
            List<ValueSpec> embeddedValues = this.embeddedValues(valueSpec);
            if(! embeddedValues.isEmpty()) {
                new PackageGenerator(embeddedValues, this.packageName + "." + valueSpec.name(), this.prefix).generate(out);
            }
        }

        this.externalValueObjectClasses(externalValueObjects, out);
    }

    public void generate(ValueSpec valueSpec, FormattedWriter out) throws IOException {
        this.interfaceDeclaration(Arrays.asList(valueSpec), out);

        Set<String> externalValueObjects = new TreeSet<>();
        externalValueObjects.addAll(this.externalValueObjects(valueSpec));
        this.valueClass(valueSpec, out);
        List<ValueSpec> embeddedValues = this.embeddedValues(valueSpec);
        if(! embeddedValues.isEmpty()) {
            new PackageGenerator(embeddedValues, this.packageName + "." + valueSpec.name(), this.prefix).generate(out);
        }

        this.externalValueObjectClasses(externalValueObjects, out);
    }

    private List<String> externalValueObjects(ValueSpec valueSpec) {
        List<String> result = new LinkedList<>();
        for (PropertySpec propertySpec : valueSpec.propertySpecs()) {
            if(propertySpec.typeSpec().typeKind().equals(TypeKind.EXTERNAL_VALUE_OBJECT)) {
                result.add(propertySpec.typeSpec().typeRef());
            }
        }
        return result;
    }

    private void externalValueObjectClasses(Set<String> externalValueObjects, FormattedWriter out) throws IOException {
        for (String externalValueObject : externalValueObjects) {
            out.appendLine(this.prefix + "  class \"%s\" %s {", externalValueObject,  EXT_VALUE_OBJECT_STEREOTYPE);
            out.appendLine(this.prefix + "  }");
        }
    }

    private void interfaceDeclaration(List<ValueSpec> valueSpecs, FormattedWriter out) throws IOException {
        Set<String> protocols = new LinkedHashSet<>();
        for (ValueSpec valueSpec : valueSpecs) {
            protocols.addAll(valueSpec.protocols());
        }

        if(! protocols.isEmpty()) {
            out.appendLine(this.prefix);
            for (String protocol : protocols) {
                out.appendLine(this.prefix + "interface \"%s\"", protocol);
            }
        }
    }

    private void valueClass(ValueSpec valueSpec, FormattedWriter out) throws IOException {
        String className = this.capitalizedFirst(valueSpec.name());
        out.appendLine(this.prefix + "  ");
        out.appendLine(this.prefix + "  class \"%s.%s\" %s {", this.packageName, className,  VALUE_OBJECT_STEREOTYPE);
        for (PropertySpec propertySpec : valueSpec.propertySpecs()) {
            switch(propertySpec.typeSpec().typeKind()) {
                case JAVA_TYPE:
                    this.javaProperty(valueSpec, propertySpec, out);
                    break;
                case ENUM:
                    break;
            }
        }
        out.appendLine(this.prefix + "  }");

        for (String implementedInterface : valueSpec.protocols()) {
            out.appendLine(this.prefix + "  \"%s.%s\" <|- \"%s\"", this.packageName, className, implementedInterface);
        }

        for (PropertySpec propertySpec : valueSpec.propertySpecs()) {
            if(propertySpec.typeSpec().typeKind().isValueObject() || propertySpec.typeSpec().typeKind().equals(TypeKind.EMBEDDED)) {
                this.aggregateProperty(valueSpec, propertySpec, out);
            } else if(propertySpec.typeSpec().typeKind().equals(TypeKind.ENUM)) {
                this.enumClass(valueSpec, propertySpec, out);
                this.aggregateProperty(valueSpec, propertySpec, out);
            }
        }
    }

    private void enumClass(ValueSpec valueSpec, PropertySpec propertySpec, FormattedWriter out) throws IOException {
        out.appendLine(this.prefix + "  ");
        out.appendLine(this.prefix + "  enum \"%s.%s.%s\" {",
                this.packageName,
                this.capitalizedFirst(valueSpec.name()),
                this.capitalizedFirst(propertySpec.name())
        );
        for (String enumVal : propertySpec.typeSpec().enumValues()) {
            out.appendLine(this.prefix + "    %s", enumVal);
        }

        out.appendLine(this.prefix + "  }");
    }

    private void aggregateProperty(ValueSpec valueSpec, PropertySpec propertySpec, FormattedWriter out) throws IOException {
        String fieldFormat = null;
        switch (propertySpec.typeSpec().cardinality()) {
            case SINGLE:
                fieldFormat = this.prefix + "  \"%s\" *-- \"%s\" : %s";
                break;
            case LIST:
                fieldFormat = this.prefix + "  \"%s\" *-- \"*\" \"%s\" : %s";
                break;
            case SET:
                fieldFormat = this.prefix + "  \"%s\" *-- \"*\" \"%s\" : %s\\n[Set]";
                break;
        }
        if(propertySpec.typeSpec().isInSpecEnum()) {
            out.appendLine(fieldFormat,
                    this.packageName + "." + this.capitalizedFirst(valueSpec.name()),
                    this.packageName + "." + this.capitalizedFirst(valueSpec.name()) + "." + this.capitalizedFirst(propertySpec.name()),
                    propertySpec.name());
        } else if(propertySpec.typeSpec().typeKind().equals(TypeKind.EMBEDDED)) {
            out.appendLine(fieldFormat,
                    this.packageName + "." + this.capitalizedFirst(valueSpec.name()),
                    this.packageName + "." + valueSpec.name() + "." + this.capitalizedFirst(propertySpec.name()),
                    propertySpec.name());
        } else if(propertySpec.typeSpec().typeKind().equals(TypeKind.IN_SPEC_VALUE_OBJECT)) {
            out.appendLine(fieldFormat,
                    this.packageName + "." + this.capitalizedFirst(valueSpec.name()),
                    this.packageName + "." + this.capitalizedFirst(propertySpec.typeSpec().typeRef()),
                    propertySpec.name());
        } else {
            out.appendLine(fieldFormat,
                    this.packageName + "." + this.capitalizedFirst(valueSpec.name()),
                    propertySpec.typeSpec().typeRef(),
                    propertySpec.name());
        }
    }

    private List<ValueSpec> embeddedValues(ValueSpec valueSpec) {
        List<ValueSpec> result = new LinkedList<>();

        for (PropertySpec propertySpec : valueSpec.propertySpecs()) {
            if(propertySpec.typeSpec().typeKind().equals(TypeKind.EMBEDDED)) {
                ValueSpec.Builder builder = ValueSpec.valueSpec().name(propertySpec.name());
                for (PropertySpec spec : propertySpec.typeSpec().embeddedValueSpec().propertySpecs()) {
                    builder.addProperty(spec);
                }
                result.add(builder.build());
            }
        }

        return result;
    }

    private void javaProperty(ValueSpec valueSpec, PropertySpec propertySpec, FormattedWriter out) throws IOException {
        String fieldFormat = null;
        switch (propertySpec.typeSpec().cardinality()) {
            case SINGLE:
                fieldFormat = this.prefix + "    {field} %s : %s";
                break;
            case LIST:
                fieldFormat = this.prefix + "    {field} %s : ValueList<%s>";
                break;
            case SET:
                fieldFormat = this.prefix + "    {field} %s : ValueSet<%s>";
                break;
        }
        out.appendLine(fieldFormat, propertySpec.name(), propertySpec.typeSpec().typeRef());
    }

    private String capitalizedFirst(String str) {
        return str.substring(0,1).toUpperCase() + str.substring(1);
    }
}
