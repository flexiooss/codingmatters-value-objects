package org.codingmatters.value.objects.reader;

import org.codingmatters.value.objects.exception.SpecSyntaxException;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.TypeToken;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.ValueSpec;

import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;

/**
 * Created by nelt on 9/4/16.
 */
public class ContextSpecParser {
    private static final Pattern JAVA_IDENTIFIER_PATTERN = Pattern.compile("\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*");
    private static final Pattern FULLY_QUALIFIED_CLASS_NAME_PATTERN = Pattern.compile(JAVA_IDENTIFIER_PATTERN.pattern() + "(\\." + JAVA_IDENTIFIER_PATTERN.pattern() + ")+");

    private final Map<String, ?> root;
    private Stack<String> context;

    public ContextSpecParser(Map<String, ?> root) {
        this.root = root;
    }

    public Spec parse() throws SpecSyntaxException {
        this.context = new Stack<>();
        Spec.Builder spec = spec();
        for (String valueName : root.keySet()) {
            spec.addValue(createValueSpec(valueName));
        }
        return spec.build();
    }

    private ValueSpec.Builder createValueSpec(String valueName) throws SpecSyntaxException {
        this.context.push(valueName);
        ValueSpec.Builder value = valueSpec().name(valueName);
        Map<String, ?> properties = (Map<String, ?>) root.get(valueName);
        if(properties != null) {
            for (String propertyName : properties.keySet()) {
                Object propertyValue = properties.get(propertyName);
                value.addProperty(this.createPropertySpec(propertyName, propertyValue));
            }
        }
        return value;
    }


    private PropertySpec.Builder createPropertySpec(String name, Object value) throws SpecSyntaxException {
        this.context.push(name);
        try {
            if (!JAVA_IDENTIFIER_PATTERN.matcher(name).matches()) {
                throw new SpecSyntaxException("malformed property name {context} : should be a valid java identifier", this.context);
            }

            String referencedType;
            if (value instanceof String) {
                referencedType = this.typeForString((String) value);
            } else if (value instanceof Map && ((Map) value).containsKey("type")) {
                referencedType = this.typeForString((String) ((Map) value).get("type"));
            } else {
                throw new SpecSyntaxException(String.format("unexpected specification for property {context}: %s", value), this.context);
            }

            return property()
                    .name(name)
                    .type(referencedType);
        } finally {
            this.context.pop();
        }
    }

    private String typeForString(String value) throws SpecSyntaxException {
        String type = value;
        if(type.startsWith("$")) {
            if(this.root.keySet().contains(type.substring(1))) {
                return String.format("#ref(%s)", type.substring(1));
            } else {
                throw new SpecSyntaxException("undeclared referenced type for {context} : a referenced type should be declared in the same spec", this.context);
            }
        } else if(FULLY_QUALIFIED_CLASS_NAME_PATTERN.matcher(type).matches()) {
            return type;
        } else {
            return this.parseType(type).getImplementationType();
        }
    }

    private TypeToken parseType(String typeSpec) throws SpecSyntaxException {
        TypeToken type;
        try {
            type = TypeToken.valueOf(typeSpec.toUpperCase());
        } catch(IllegalArgumentException e) {
            throw new SpecSyntaxException(
                    String.format("invalid type for property {context} : %s, should be one of %s, a reference to an in spec declared type ($type notation) or a fully qualified class name (default package classes cannot be used).", typeSpec, TypeToken.validTypesSpec()),
                    this.context);
        }
        return type;
    }
}
