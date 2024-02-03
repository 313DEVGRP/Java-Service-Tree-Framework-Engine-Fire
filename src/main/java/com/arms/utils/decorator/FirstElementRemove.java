package com.arms.utils.decorator;

import java.util.Arrays;

public  class FirstElementRemove extends ElementDecorator<String[]>{

    public FirstElementRemove(PrettyElement<String[]> prettyElement){
        super(prettyElement);
    }
    @Override
    public String[] element() {
        String[] element = super.element();
        return Arrays.stream(element).skip(1)
                .toArray(size->Arrays.copyOf(element,size));
    }

}
