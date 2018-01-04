package org.codingmatters.value.objects.spec;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nelt on 9/3/16.
 */
public class PropertySpec {

    static public Builder property() {
        return new Builder();
    }

    static public class Builder {
        private String name;
        private PropertyTypeSpec type;
        private Set<String> hints;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(PropertyTypeSpec.Builder type) {
            this.type = type.build();
            return this;
        }

        public Builder hints(Set<String> hints) {
            if(hints != null && ! hints.isEmpty()) {
                if(this.hints == null) {
                    this.hints = new HashSet<>(hints);
                }
                this.hints.addAll(hints);
            }
            return this;
        }

        public PropertySpec build() {
            return new PropertySpec(this.name, this.type, this.hints != null ? this.hints.toArray(new String[this.hints.size()]) : new String[0]);
        }
    }

    private final String name;
    private PropertyTypeSpec type;
    private final String[] hints;

    public PropertySpec(String name, PropertyTypeSpec type, String[] hints) {
        this.name = name;
        this.type = type;
        this.hints = hints;
    }

    public String name() {
        return name;
    }

    public PropertyTypeSpec typeSpec() {
        return this.type;
    }

    public String[] hints() {
        return hints;
    }

    public String[] hints(String prefix) {
        if(! prefix.endsWith(":")) {
            prefix = prefix + ":";
        }
        LinkedList<String> result = new LinkedList<>();
        for (String hint : this.hints) {
            if(hint.startsWith(prefix)) {
                result.add(hint);
            }
        }
        return result.toArray(new String[result.size()]);
    }

    public Optional<Matcher> matchingHint(String pattern) {
        Pattern p = Pattern.compile(pattern);
        for (String hint : this.hints) {
            Matcher matcher = p.matcher(hint);
            if(matcher.matches()) {
                return Optional.of(matcher);
            }
        }
        return Optional.ofNullable(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PropertySpec that = (PropertySpec) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(hints, that.hints);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(hints);
        return result;
    }

    @Override
    public String toString() {
        return "PropertySpec{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", hints=" + Arrays.toString(hints) +
                '}';
    }
}
