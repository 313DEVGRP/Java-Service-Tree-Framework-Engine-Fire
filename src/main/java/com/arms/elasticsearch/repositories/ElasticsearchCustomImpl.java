package com.arms.elasticsearch.repositories;

import static java.util.stream.Collectors.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.AggregationsContainer;
import org.springframework.data.elasticsearch.core.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import com.arms.elasticsearch.util.검색엔진_유틸;
import com.arms.elasticsearch.util.검색조건;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class ElasticsearchCustomImpl implements ElasticsearchCustom {

	private final RestHighLevelClient client;

	private final ObjectMapper objectMapper;

	private ElasticsearchOperations elasticsearchOperations;


	@Override
	public <T>List<T> internalSearch(SearchRequest request ,Class<T> clazz) {
		if (request == null) {
			log.error("Failed to build search request");
			return Collections.emptyList();
		}
		try {
			SearchResponse response = client.search(request, RequestOptions.DEFAULT);
			SearchHit[] searchHits = response.getHits().getHits();
			return Arrays.stream(searchHits)
				.map(hit-> {
					try {
						return objectMapper.readValue(hit.getSourceAsString(),clazz);
					} catch (JsonProcessingException e) {
						throw new RuntimeException(e);
					}
				})
				.collect(toList());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	@Override
	public SearchResponse search(SearchRequest searchRequest, RequestOptions options) throws IOException {
		return client.search(searchRequest,options);
	}

	@Override
	public <T> List<T>  getAllCreatedSince(final Date date,Class<T> clazz) {

		Document document = AnnotationUtils.findAnnotation(clazz, Document.class);
		final SearchRequest request = 검색엔진_유틸.buildSearchRequest(
			document.indexName(),
			"created",
			date
		);
		return this.internalSearch(request,clazz);

	}

	@Override
	public <T> List<T>  searchCreatedSince(final 검색조건 dto, final Date date, Class<T> clazz) {

		Document document = AnnotationUtils.findAnnotation(clazz, Document.class);
		final SearchRequest request = 검색엔진_유틸.buildSearchRequest(
			document.indexName(),
			dto,
			date
		);
		return this.internalSearch(request,clazz);


	}

	@Override
	public <T>Boolean index(T t) {
		try {

			Document annotation = t.getClass().getAnnotation(Document.class);

			final String vehicleAsString = objectMapper.writeValueAsString(t);

			final IndexRequest request = new IndexRequest(annotation.indexName());
			request.id(this.getIdValue(t));
			request.source(vehicleAsString, XContentType.JSON);

			final IndexResponse response = client.index(request, RequestOptions.DEFAULT);

			return response != null && response.status().equals(RestStatus.OK);
		} catch (final Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}

	@Override
	public <T> T getById(final String 이슈_아이디,Class<T> clazz) {
		try {
			Document document = AnnotationUtils.findAnnotation(clazz, Document.class);
			final GetResponse documentFields = client.get(
				new GetRequest(document.indexName(), 이슈_아이디),
				RequestOptions.DEFAULT
			);
			if (documentFields == null || documentFields.isSourceEmpty()) {
				return null;
			}

			return objectMapper.readValue(documentFields.getSourceAsString(), clazz);
		} catch (final Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}


	private <T> String getIdValue(T t) {
		Field[] fields = t.getClass().getDeclaredFields();

		return Arrays.stream(fields).flatMap(field ->
			Arrays.stream(field.getDeclaredAnnotations())
				.filter(annotation -> (annotation.annotationType()== Id.class))
				.map(annotation -> {
					try {
						field.setAccessible(true);
						if(field.get(t)!=null){
							return (String)field.get(t);
						}else{
							throw new RuntimeException("Id 값이 없습니다.");
						}
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				})
		).findFirst().orElseThrow();

	}
	@Override
	public <T> SearchHits search(Query query, Class<T> clazz) {
		return elasticsearchOperations.search(query,clazz);
	}

	@Override
	public <T,B> List<B> getBucket(NativeSearchQuery query, Class<T> clazz, BucketRowMapper<B> bucketRowMapper) {
		String groupName = bucketGroupName(query);

		List<? extends MultiBucketsAggregation.Bucket> buckets = getTerm(
			elasticsearchOperations.search(query, clazz).getAggregations()
			, groupName).getBuckets();

		return buckets.stream()
			.map(a-> bucketRowMapper.bucketRow(a))
			.collect(toList());
	}


	//방향성 고민으로 임시 주석
	//List<? extends Terms.Bucket> buckets = ((Terms)bucket.getAggregations().get(groupName)).getBuckets();
/*	@Override
	public <T> List<Terms.Bucket> getMultiBucket(NativeSearchQuery query, Class<T> clazz) {

		String groupName = bucketGroupName(query);

		return (List<Terms.Bucket>)getTerm(
			elasticsearchOperations.search(query, clazz).getAggregations()
			, groupName).getBuckets();

	}*/

	private MultiBucketsAggregation getTerm(AggregationsContainer aggregationsContainer,String groupName){
		Aggregations aggregations = getAggregations(aggregationsContainer);
		return aggregations.get(groupName);
	}

	private Terms getTerm(Aggregations aggregations,String groupName){
		return aggregations.get(groupName);
	}

	private Aggregations getAggregations(AggregationsContainer aggregationsContainer){
		return ((ElasticsearchAggregations) aggregationsContainer).aggregations();
	}

	private String bucketGroupName(NativeSearchQuery query) {
		return query.getAggregations()
			.stream()
			.findAny()
			.map(a->a.getName())
			.orElse("");
	}

	private String bucketSubGroupName(NativeSearchQuery query) {
		 return query.getAggregations()
			.stream()
			.findAny()
			.map(a->a.getSubAggregations().stream().map(b->b.getName()).findAny().orElse(""))
			 .orElse("");
	}




}
