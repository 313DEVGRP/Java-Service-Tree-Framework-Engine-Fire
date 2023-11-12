package com.arms.api.engine.repositories;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.search.aggregations.Aggregation;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;

import com.arms.elasticsearch.util.검색조건;

public interface ElasticsearchCustom {


	SearchResponse search(SearchRequest searchRequest, RequestOptions options) throws IOException;



}
