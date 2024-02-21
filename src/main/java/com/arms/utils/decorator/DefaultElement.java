package com.arms.utils.decorator;

public class DefaultElement<T> implements PrettyElement<T>{

    private final T t;
    public DefaultElement(T t){
        this.t = t;
    }
    @Override
    public T element() {
        return t;
    }
}
