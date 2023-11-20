package com.arms.elasticsearch.util.query;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.ParameterizedTypeReference;

public abstract class EsQuery {

	private ConcurrentHashMap<Type, Object> map = new ConcurrentHashMap<>();

	public  <T> T getQuery(ParameterizedTypeReference<T> typeReference) {
		Type type = typeReference.getType();
		if (type instanceof ParameterizedType) {
			return ((Class<T>) ((ParameterizedType) type).getRawType()).cast(map.get(typeReference.getType()));
		} else {
			return ((Class<T>) type).cast(map.get(typeReference.getType()));
		}
	}

	public <T> void put(ParameterizedTypeReference<T> parameterizedTypeReference, T t){
		map.put(parameterizedTypeReference.getType(), t);
	}

}
