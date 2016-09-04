package org.codingmatters.value.objects.reader;

import org.codingmatters.value.objects.exception.SpecSyntaxException;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.PropertyType;
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
        if(! JAVA_IDENTIFIER_PATTERN.matcher(name).matches()) {
            throw new SpecSyntaxException("malformed property name {context} : should be a valid java identifier", this.context);
        }
        PropertyType type;
        try {
            type = PropertyType.valueOf(((String) value).toUpperCase());
        } catch(IllegalArgumentException e) {
            throw new SpecSyntaxException(
                    String.format("invalid type for property {context} : strrrrring, should be one of %s", PropertyType.validTypesSpec()),
                    this.context);
        }
        return property()
                .name(name)
                .type(type);
    }
}
