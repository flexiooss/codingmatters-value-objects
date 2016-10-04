package org.codingmatters.value.objects.exception;

import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Created by nelt on 9/4/16.
 */
public class SpecSyntaxException extends Exception {
    public SpecSyntaxException(String msg, Stack<String> context) {
        super(msg.replaceAll("\\{context\\}", '"' + contextString(context) + '"'));
    }

    static private String contextString(Stack<String> context) {
        return context.stream().collect(Collectors.joining("/"));
    }
}
