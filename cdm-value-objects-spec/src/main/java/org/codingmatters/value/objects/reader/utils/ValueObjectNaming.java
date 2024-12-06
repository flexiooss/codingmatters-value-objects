package org.codingmatters.value.objects.reader.utils;

import java.util.LinkedList;
import java.util.function.Function;

public class ValueObjectNaming {
    public String type(String ... parts) {
        return this.name(this::upperCaseFirst, parts);
    }

    public String property(String ... parts) {
        return this.name(this::lowerCaseFirst, parts);
    }

    private String name(Function<String, String> firstPartTransformer, String ... parts) {
        if(parts == null) return null;
        if(parts.length == 0) return "";
        parts = this.normalize(parts);

        StringBuilder result = new StringBuilder(firstPartTransformer.apply(parts[0]));
        for(int i = 1 ; i < parts.length ; i++) {
            result.append(this.upperCaseFirst(parts[i]));
        }
        return result.toString();
    }

    private String [] normalize(String ... parts) {
        LinkedList<String> result = new LinkedList<>();
        for (String part : parts) {
            for (String subpart : part.split("(\\s+)|([\\-.$]+)")) {
                subpart = subpart.replaceAll("\\p{Punct}", "");
                if(! subpart.isEmpty()) {
                    result.add(subpart);
                }
            }
        }
        return result.toArray(new String[result.size()]);
    }

    private String upperCaseFirst(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    private String lowerCaseFirst(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

}
