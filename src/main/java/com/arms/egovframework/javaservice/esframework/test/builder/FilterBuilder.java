package com.arms.egovframework.javaservice.esframework.test.builder;


import java.util.function.Consumer;

public class FilterBuilder {

    public FilterBuilder filter(Consumer<TermBuilder> termBuilder){
        return this;
    }

    public TermBuilder and(){
        return new TermBuilder(this);
    }
}
