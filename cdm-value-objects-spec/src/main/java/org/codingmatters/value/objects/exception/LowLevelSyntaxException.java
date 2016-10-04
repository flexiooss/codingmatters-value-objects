package org.codingmatters.value.objects.exception;

/**
 * Created by nelt on 9/4/16.
 */
public class LowLevelSyntaxException extends Exception {
    public LowLevelSyntaxException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
