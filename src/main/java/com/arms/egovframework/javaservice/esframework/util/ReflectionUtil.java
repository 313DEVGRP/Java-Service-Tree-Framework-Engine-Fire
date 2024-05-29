package com.arms.egovframework.javaservice.esframework.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

public class ReflectionUtil {

    private ReflectionUtil(){

    }
    public static <S> List<Object> fieldValues(Iterable<S> entities, Class<? extends Annotation> annotation){
        return StreamSupport.stream(entities.spliterator(), false)
                .collect(toList())
                .stream().map(a -> {
                    try {
                        return fieldInfo(a.getClass(),annotation).get(a);
                    } catch (IllegalAccessException e) {
                        throw new IllegalArgumentException(e);
                    }
                }).collect(toList());
    }

    public static Method methodInfo(Class<?> entityClass, Class<? extends Annotation> annotation){

        Method method = Arrays.stream(entityClass.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(annotation))
                .findAny().orElseThrow(() -> new RuntimeException("해당 어노테이션이 지정 되어있지 않습니다."));
        method.setAccessible(true);
        return method;
    }

    public static Field fieldInfo(Class<?> entityClass, Class<? extends Annotation> annotation){

        Field field = Arrays.stream(entityClass.getDeclaredFields())
                .filter(m -> m.isAnnotationPresent(annotation))
                .findAny().orElseThrow(() -> new RuntimeException("해당 어노테이션이 지정 되어있지 않습니다."));
        field.setAccessible(true);
        return field;
    }

}
