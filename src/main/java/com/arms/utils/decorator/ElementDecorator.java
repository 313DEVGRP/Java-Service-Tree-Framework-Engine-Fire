package com.arms.utils.decorator;

public abstract class ElementDecorator<T> implements PrettyElement<T>{

    private final PrettyElement<T> prettyElement;
    public ElementDecorator(PrettyElement<T> prettyElement){
        this.prettyElement = prettyElement;
    }
    @Override
    public T element() {
        return prettyElement.element();
    }

}
