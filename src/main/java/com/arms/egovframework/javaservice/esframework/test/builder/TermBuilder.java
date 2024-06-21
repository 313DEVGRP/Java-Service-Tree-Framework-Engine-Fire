package com.arms.egovframework.javaservice.esframework.test.builder;

public class TermBuilder {

    private final FilterBuilder filterBuilder;

    public TermBuilder(FilterBuilder filterBuilder) {
        this.filterBuilder = filterBuilder;
    }

    public TermBuilder field(String name){
        return this;
    }

    public FilterBuilder value(String name){
        return filterBuilder;
    }

}
