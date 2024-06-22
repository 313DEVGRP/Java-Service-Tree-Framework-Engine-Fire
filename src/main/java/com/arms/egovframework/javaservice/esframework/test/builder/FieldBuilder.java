package com.arms.egovframework.javaservice.esframework.test.builder;


public class FieldBuilder {

    public FieldBuilder(TermConjunctionBuilder termConjunctionBuilder){

    }

    public ValueBuilder field(String name){
        return new ValueBuilder(this);
    }

}
