package com.arms.egovframework.javaservice.esframework.test.builder;


public class TermConjunctionBuilder {

    public TermConjunctionBuilder(ValueBuilder valueBuilder){

    }

    public FieldBuilder and(){
        return new FieldBuilder(this);
    }

    public TermBuilder build(){
        return new TermBuilder(this);
    }

}
