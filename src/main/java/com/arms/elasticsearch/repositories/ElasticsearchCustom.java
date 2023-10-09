package com.arms.elasticsearch.repositories;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;

import com.arms.elasticsearch.util.검색조건;

public interface ElasticsearchCustom {

	<T> List<T> internalSearch(SearchRequest request,Class<T> clazz);

	SearchResponse search(SearchRequest searchRequest, RequestOptions options) throws IOException;

	<T> List<T>  getAllCreatedSince(Date date, Class<T> clazz);

	<T> List<T>  searchCreatedSince(검색조건 dto, Date date, Class<T> clazz);

	<T>Boolean index(T t);

	<T> T getById(String 이슈_아이디, Class<T> clazz);

	<T> SearchHits search(Query query, Class<T> clazz) ;

	<T,B> List<B> getBucket(NativeSearchQuery query, Class<T> clazz, BucketRowMapper<B> bucketRowMapper);
}
