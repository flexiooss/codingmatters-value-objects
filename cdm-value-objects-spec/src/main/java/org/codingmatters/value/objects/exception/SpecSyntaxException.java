package org.codingmatters.value.objects.exception;

import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Created by nelt on 9/4/16.
 */
public class SpecSyntaxException extends Exception {
    public SpecSyntaxException(String msg, Stack<String> context) {
        super(replaceContext(msg, context));
    }

    static private String replaceContext(String msg, Stack<String> context) {
        String stackString = "\"" + contextString(context) + "\"";
        int contextIndex = msg.indexOf("{context}");
        while(contextIndex != -1) {
            String before = msg.substring(0, contextIndex);
            msg = before + stackString + msg.substring(contextIndex + "{context}".length());
            contextIndex = msg.indexOf("{context}");
        }
        return msg;
    }

    static private String contextString(Stack<String> context) {
        return context.stream().collect(Collectors.joining("/"));
    }
}
