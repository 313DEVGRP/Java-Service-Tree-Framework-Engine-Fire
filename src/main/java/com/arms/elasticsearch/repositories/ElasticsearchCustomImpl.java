package com.arms.elasticsearch.repositories;

import static java.util.stream.Collectors.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
import org.elasticsearch.xcontent.XContentType;
import org.springframework.stereotype.Repository;

import com.arms.elasticsearch.helper.인덱스자료;
import com.arms.elasticsearch.models.지라이슈;
import com.arms.elasticsearch.util.검색엔진_유틸;
import com.arms.elasticsearch.util.검색조건;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@AllArgsConstructor
@Slf4j
public class ElasticsearchCustomImpl implements ElasticsearchCustom {

	private final RestHighLevelClient client;

	private final ObjectMapper objectMapper;


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
	public <T> List<T>  getAllCreatedSince(final Date date,Class<T> valueType) {
		final SearchRequest request = 검색엔진_유틸.buildSearchRequest(
			인덱스자료.지라이슈_인덱스명,
			"created",
			date
		);

		return this.internalSearch(request,valueType);
	}

	@Override
	public <T> List<T>  searchCreatedSince(final 검색조건 dto, final Date date, Class<T> valueType) {
		final SearchRequest request = 검색엔진_유틸.buildSearchRequest(
			인덱스자료.지라이슈_인덱스명,
			dto,
			date
		);

		return this.internalSearch(request,valueType);
	}

	@Override
	public Boolean index(final 지라이슈 지라_이슈) {
		try {
			final String vehicleAsString = objectMapper.writeValueAsString(지라_이슈);

			final IndexRequest request = new IndexRequest(인덱스자료.지라이슈_인덱스명);
			request.id(지라_이슈.getId());
			request.source(vehicleAsString, XContentType.JSON);

			final IndexResponse response = client.index(request, RequestOptions.DEFAULT);

			return response != null && response.status().equals(RestStatus.OK);
		} catch (final Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}

	@Override
	public <T> T getById(final String 이슈_아이디,Class<T> valueType) {
		try {
			final GetResponse documentFields = client.get(
				new GetRequest(인덱스자료.지라이슈_인덱스명, 이슈_아이디),
				RequestOptions.DEFAULT
			);
			if (documentFields == null || documentFields.isSourceEmpty()) {
				return null;
			}

			return objectMapper.readValue(documentFields.getSourceAsString(), valueType);
		} catch (final Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}


}
