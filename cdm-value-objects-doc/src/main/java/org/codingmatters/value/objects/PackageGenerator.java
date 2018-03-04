package org.codingmatters.value.objects;

import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.TypeKind;
import org.codingmatters.value.objects.spec.ValueSpec;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PackageGenerator {

    private final List<ValueSpec> valueSpecs;
    private final String packageName;

    public PackageGenerator(List<ValueSpec> valueSpecs, String packageName) {
        this.valueSpecs = valueSpecs;
        this.packageName = packageName;
    }

    public void generate(FormattedWriter out) throws IOException {
        out.appendLine("@startuml");
        this.interfaceDeclaration(this.valueSpecs, out);

        out.appendLine("package %s {", this.packageName);
        for (ValueSpec valueSpec : this.valueSpecs) {
            this.generate(valueSpec, out);
        }
        out.appendLine("}");
        out.appendLine("@enduml");
    }

    private void interfaceDeclaration(List<ValueSpec> valueSpecs, FormattedWriter out) throws IOException {
        Set<String> protocols = new LinkedHashSet<>();
        for (ValueSpec valueSpec : valueSpecs) {
            protocols.addAll(valueSpec.protocols());
        }

        if(! protocols.isEmpty()) {
            out.appendLine("");
            for (String protocol : protocols) {
                out.appendLine("interface \"%s\"", protocol);
            }
        }
    }

    private void generate(ValueSpec valueSpec, FormattedWriter out) throws IOException {
        String className = this.capitalizedFirst(valueSpec.name());
        out.appendLine("  ");
        out.appendLine("  class \"%s\" {", className);
        for (PropertySpec propertySpec : valueSpec.propertySpecs()) {
            switch(propertySpec.typeSpec().typeKind()) {
                case JAVA_TYPE:
                    this.javaProperty(valueSpec, propertySpec, out);
                    break;
                case ENUM:
                    break;
            }
        }
        out.appendLine("  }");

        for (String implementedInterface : valueSpec.protocols()) {
            out.appendLine("  \"%s\" <|- \"%s\"", className, implementedInterface);
        }

        for (PropertySpec propertySpec : valueSpec.propertySpecs()) {
            if(propertySpec.typeSpec().typeKind().isValueObject()) {
                this.aggregateProperty(valueSpec, propertySpec, out);
            } else if(propertySpec.typeSpec().typeKind().equals(TypeKind.ENUM)) {
                this.enumClass(valueSpec, propertySpec, out);
                this.aggregateProperty(valueSpec, propertySpec, out);
            }
        }
    }

    private void enumClass(ValueSpec valueSpec, PropertySpec propertySpec, FormattedWriter out) throws IOException {
        out.appendLine("  ");
        out.appendLine("  enum \"%s.%s\" {",
                this.capitalizedFirst(valueSpec.name()),
                this.capitalizedFirst(propertySpec.name())
        );
        for (String enumVal : propertySpec.typeSpec().enumValues()) {
            out.appendLine("    %s", enumVal);
        }

        out.appendLine("  }");
    }

    private void aggregateProperty(ValueSpec valueSpec, PropertySpec propertySpec, FormattedWriter out) throws IOException {
        String fieldFormat = null;
        switch (propertySpec.typeSpec().cardinality()) {
            case SINGLE:
                fieldFormat = "  %s *-- %s : %s";
                break;
            case LIST:
                fieldFormat = "  %s *-- \"*\" %s : %s";
                break;
            case SET:
                fieldFormat = "  %s *-- \"*\" %s : %s\\n[Set]";
                break;
        }
        if(propertySpec.typeSpec().isInSpecEnum()) {
            out.appendLine(fieldFormat,
                    this.capitalizedFirst(valueSpec.name()),
                    this.capitalizedFirst(valueSpec.name()) + "." + this.capitalizedFirst(propertySpec.name()),
                    propertySpec.name());
        } else {
            out.appendLine(fieldFormat,
                    this.capitalizedFirst(valueSpec.name()),
                    this.capitalizedFirst(propertySpec.typeSpec().typeRef()),
                    propertySpec.name());
        }
    }

    private void javaProperty(ValueSpec valueSpec, PropertySpec propertySpec, FormattedWriter out) throws IOException {
        String fieldFormat = null;
        switch (propertySpec.typeSpec().cardinality()) {
            case SINGLE:
                fieldFormat = "    {field} %s : %s";
                break;
            case LIST:
                fieldFormat = "    {field} %s : ValueList<%s>";
                break;
            case SET:
                fieldFormat = "    {field} %s : ValueSet<%s>";
                break;
        }
        out.appendLine(fieldFormat, propertySpec.name(), propertySpec.typeSpec().typeRef());
    }

    private String capitalizedFirst(String str) {
        return str.substring(0,1).toUpperCase() + str.substring(1);
    }
}
