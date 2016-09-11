package org.codingmatters.tests.reflect;

import java.lang.reflect.Member;

import static java.lang.reflect.Modifier.*;

/**
 * Created by nelt on 9/11/16.
 */
public class MemberDeleguate<T> {

    private final MatcherChain<Member> matchers;

    public MemberDeleguate(MatcherChain matchers) {
        this.matchers = matchers;
    }


    public T named(String name, T self) {
        this.matchers.addMatcher("method named " + name, item -> item.getName().equals(name));
        return self;
    }


    public T static_(T self) {
        this.matchers.addMatcher("instance method", item -> isStatic(item.getModifiers()));
        return self;
    }

    public T notStatic(T self) {
        this.matchers.addMatcher("instance method", item -> ! isStatic(item.getModifiers()));
        return self;
    }

    public T public_(T self) {
        this.matchers.addMatcher("public method", item -> isPublic(item.getModifiers()));
        return self;
    }

    public T private_(T self) {
        this.matchers.addMatcher("private method", item -> isPrivate(item.getModifiers()));
        return self;
    }

    public T protected_(T self) {
        this.matchers.addMatcher("protected method", item -> isProtected(item.getModifiers()));
        return self;
    }

    public T packagePrivate(T self) {
        this.matchers.addMatcher("package private method", item -> ! (isPublic(item.getModifiers()) || isPrivate(item.getModifiers()) || isProtected(item.getModifiers())));
        return self;
    }


}
