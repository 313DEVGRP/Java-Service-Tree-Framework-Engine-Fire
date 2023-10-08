package com.arms.elasticsearch.repositories;

import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;

public interface QueryAbstractFactory {

	NativeSearchQuery create();
}
