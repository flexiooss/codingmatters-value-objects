package org.codingmatters.value.objects.reader;

import org.codingmatters.value.objects.exception.SpecSyntaxException;
import org.codingmatters.value.objects.spec.*;

import java.util.*;
import java.util.regex.Pattern;

import static org.codingmatters.value.objects.spec.PropertySpec.property;
import static org.codingmatters.value.objects.spec.PropertyTypeSpec.type;
import static org.codingmatters.value.objects.spec.Spec.spec;
import static org.codingmatters.value.objects.spec.ValueSpec.valueSpec;

/**
 * Created by nelt on 9/4/16.
 */
public class ContextSpecParser {
    private static final Pattern JAVA_IDENTIFIER_PATTERN = Pattern.compile("\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*");
    private static final Pattern FULLY_QUALIFIED_CLASS_NAME_PATTERN = Pattern.compile(JAVA_IDENTIFIER_PATTERN.pattern() + "(\\." + JAVA_IDENTIFIER_PATTERN.pattern() + ")+");

    public static final String LIST_MARK = "$list";
    public static final String HINTS_MARK = "$hints";
    public static final String SET_MARK = "$set";
    public static final String VALUE_OBJECT_MARK = "$value-object";
    public static final String TYPE_MARK = "$type";
    public static final String ENUM_MARK = "$enum";
    public static final String PROTOCOL_MARK = "$conforms-to";

    private final Map<String, ?> root;
    private Stack<String> context;

    public ContextSpecParser(Map<String, ?> root) {
        this.root = root;
    }

    public Spec parse() throws SpecSyntaxException {
        this.context = new Stack<>();
        Spec.Builder spec = spec();
        for (String valueName : root.keySet()) {
            spec.addValue(this.createValueSpec(valueName));
        }
        return spec.build();
    }

    private ValueSpec.Builder createValueSpec(String valueName) throws SpecSyntaxException {
        this.context.push(valueName);
        ValueSpec.Builder value = valueSpec().name(valueName);
        Map<String, ?> properties = (Map<String, ?>) root.get(valueName);
        if(properties != null) {
            for (String propertyName : properties.keySet()) {
                if(PROTOCOL_MARK.equals(propertyName)) {
                    value.addConformsTo(this.protocolList(properties.get(propertyName)));
                } else {
                    value.addProperty(this.createPropertySpec(propertyName, properties.get(propertyName)));
                }
            }
        }
        return value;
    }

    private String[] protocolList(Object value) {
        if(value == null) return new String[0];
        if(value instanceof List) {
            return (String[]) ((List)value).stream().map(o -> o.toString()).toArray(i -> new String[i]);
        } else {
            return new String[] {value.toString()};
        }
    }


    private PropertySpec.Builder createPropertySpec(String name, Object value) throws SpecSyntaxException {
        this.context.push(name);
        try {
            if (!JAVA_IDENTIFIER_PATTERN.matcher(name).matches()) {
                throw new SpecSyntaxException("malformed property name {context} : should be a valid java identifier", this.context);
            }

            PropertyCardinality cardinality;
            if(value instanceof Map && ((Map) value).containsKey(LIST_MARK)) {
                cardinality = PropertyCardinality.LIST;
                value = ((Map) value).get(LIST_MARK);
            } else if(value instanceof Map && ((Map) value).containsKey(SET_MARK)) {
                cardinality = PropertyCardinality.SET;
                value = ((Map) value).get(SET_MARK);
            } else {
                cardinality = PropertyCardinality.SINGLE;
            }

            PropertyTypeSpec.Builder typeSpec;
            if (value instanceof String) {
                typeSpec = this.typeForString((String) value);
            } else if (value instanceof Map && ((Map) value).containsKey(VALUE_OBJECT_MARK)) {
                typeSpec = this.typeForString((String) ((Map) value).get(VALUE_OBJECT_MARK))
                        .typeKind(TypeKind.EXTERNAL_VALUE_OBJECT);
            } else if (value instanceof Map && ((Map) value).containsKey(ENUM_MARK)) {
                typeSpec = this.enumTypeSpec(value);
            } else if (value instanceof Map && ((Map) value).containsKey(TYPE_MARK)) {
                typeSpec = this.typeForString((String) ((Map) value).get(TYPE_MARK));
            } else if(value instanceof Map) {
                typeSpec = type().typeKind(TypeKind.EMBEDDED);
                typeSpec.embeddedValueSpec(this.parseAnonymousValueSpec(((Map)value)));
            } else {
                throw new SpecSyntaxException(String.format("unexpected specification for property {context}: %s", value), this.context);
            }

            typeSpec.cardinality(cardinality);

            HashSet<String> hints = new HashSet<>();
            if(value instanceof Map && ((Map) value).containsKey(HINTS_MARK)) {
                if(((Map) value).get(HINTS_MARK) instanceof Collection) {
                    for (Object hint : ((Collection) ((Map) value).get(HINTS_MARK))) {
                        hints.add(String.valueOf(hint));
                    }
                } else {
                    hints.add(String.valueOf(((Map) value).get(HINTS_MARK)));
                }
            }

            return property()
                    .name(name)
                    .type(typeSpec)
                    .hints(hints)
                    ;
        } finally {
            this.context.pop();
        }
    }

    private PropertyTypeSpec.Builder enumTypeSpec(Object value) throws SpecSyntaxException {
        PropertyTypeSpec.Builder typeSpec;
        if(((Map)value).get(ENUM_MARK) != null && ((Map)value).get(ENUM_MARK) instanceof String) {
            String valueString = (String) ((Map) value).get(ENUM_MARK);

            List<String> values = new LinkedList<>();
            for (String val : valueString.split(",")) {
                values.add(val.trim());
            }
            return PropertyTypeSpec.type()
                    .typeKind(TypeKind.ENUM)
                    .enumValues(values.toArray(new String[values.size()]));
        } else if(((Map)value).get(ENUM_MARK) != null && ((Map)value).get(ENUM_MARK) instanceof Map
                && ((Map)((Map)value).get(ENUM_MARK)).containsKey(TYPE_MARK)
                && ((Map)((Map)value).get(ENUM_MARK)).get(TYPE_MARK) instanceof String) {
            return PropertyTypeSpec.type()
                    .typeKind(TypeKind.ENUM)
                    .typeRef((String) ((Map)((Map)value).get(ENUM_MARK)).get(TYPE_MARK));
        } else {
            throw new SpecSyntaxException(String.format("malformed enum specification for property {context}: %s", value), this.context);
        }
    }

    private PropertyTypeSpec.Builder typeForString(String value) throws SpecSyntaxException {
        String type = value;
        if(type.startsWith("$")) {
            if(this.root.keySet().contains(type.substring(1))) {
                return type()
                        .typeRef(type.substring(1))
                        .typeKind(TypeKind.IN_SPEC_VALUE_OBJECT)
                        ;
            } else {
                throw new SpecSyntaxException("undeclared referenced type for {context} : a referenced type should be declared in the same spec", this.context);
            }
        } else if(FULLY_QUALIFIED_CLASS_NAME_PATTERN.matcher(type).matches()) {
            return type()
                    .typeRef(type)
                    .typeKind(TypeKind.JAVA_TYPE)
                    ;
        } else {
            return type()
                    .typeRef(this.parseType(type).getImplementationType())
                    .typeKind(TypeKind.JAVA_TYPE)
                    ;
        }
    }

    private TypeToken parseType(String typeSpec) throws SpecSyntaxException {
        TypeToken type;
        try {
            type = TypeToken.parse(typeSpec);
        } catch(IllegalArgumentException e) {
            throw new SpecSyntaxException(
                    String.format("invalid type for property {context} : %s, should be one of %s, a reference to an in spec declared type ($type notation) or a fully qualified class name (default package classes cannot be used).", typeSpec, TypeToken.validTypesSpec()),
                    this.context);
        }
        return type;
    }

    private AnonymousValueSpec parseAnonymousValueSpec(Map value) throws SpecSyntaxException {
        AnonymousValueSpec.Builder result = AnonymousValueSpec.anonymousValueSpec();
        for (Object name : value.keySet()) {
            result.addProperty(this.createPropertySpec((String) name, value.get(name)));
        }

        return result.build();
    }
}
