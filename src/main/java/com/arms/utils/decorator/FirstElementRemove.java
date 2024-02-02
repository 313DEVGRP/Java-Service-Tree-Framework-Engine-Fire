package com.arms.utils.decorator;

import java.util.Arrays;

public  class FirstElementRemove<T> extends ElementDecorator<T[]>{

    public FirstElementRemove(PrettyElement<T[]> prettyElement){
        super(prettyElement);
    }
    @Override
    public T[] element() {
        T[] element = super.element();
        return Arrays.stream(element).skip(1)
                .toArray(size->Arrays.copyOf(element,size));
    }

}
