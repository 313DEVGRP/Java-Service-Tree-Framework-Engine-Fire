package com.arms.elasticsearch.repositories;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;

import com.arms.elasticsearch.models.지라이슈;
import com.arms.elasticsearch.util.검색조건;

public interface ElasticsearchCustom {

	<T> List<T> internalSearch(SearchRequest request,Class<T> clazz);

	SearchResponse search(SearchRequest searchRequest, RequestOptions options) throws IOException;

	<T> List<T>  getAllCreatedSince(Date date, Class<T> clazz);

	<T> List<T>  searchCreatedSince(검색조건 dto, Date date, Class<T> clazz);

	<T>Boolean index(T t);

	<T> T getById(String 이슈_아이디, Class<T> clazz);
}