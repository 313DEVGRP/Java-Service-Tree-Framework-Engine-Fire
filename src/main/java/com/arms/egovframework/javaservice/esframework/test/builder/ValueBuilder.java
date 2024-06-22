package com.arms.egovframework.javaservice.esframework.test.builder;



public class ValueBuilder {

    public ValueBuilder(FieldBuilder fieldBuilder){

    }

    public TermConjunctionBuilder value(String value){
        return new TermConjunctionBuilder(this);
    }


}
