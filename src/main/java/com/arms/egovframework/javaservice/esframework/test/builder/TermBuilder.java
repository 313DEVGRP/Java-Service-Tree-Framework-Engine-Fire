package com.arms.egovframework.javaservice.esframework.test.builder;


import java.util.function.Consumer;

public class TermBuilder {


    public TermBuilder(FilterBuilder filterBuilder) {
    }

    public TermBuilder(TermConjunctionBuilder termConjunctionBuilder) {
//        this.filterBuilder = filterBuilder;
    }

    public TermBuilder term(Consumer<FieldBuilder> termConsumer){
        return this;
    }


}
