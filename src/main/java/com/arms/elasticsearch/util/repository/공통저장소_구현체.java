package com.arms.elasticsearch.util.repository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchEntityInformation;
import org.springframework.data.elasticsearch.repository.support.SimpleElasticsearchRepository;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.arms.api.engine.models.지라이슈;
import com.arms.elasticsearch.util.검색엔진_유틸;
import com.arms.elasticsearch.util.검색조건;

@Slf4j
public class 공통저장소_구현체<T,ID extends Serializable> extends SimpleElasticsearchRepository<T,ID> implements 공통저장소<T,ID> {

    private final Class<T> entityClass;
    private final ElasticsearchOperations operations;

    public 공통저장소_구현체(ElasticsearchEntityInformation<T, ID> metadata
            , ElasticsearchOperations operations) {
        super(metadata, operations);
        this.entityClass = metadata.getJavaType();
        this.operations = operations;
    }

    @Override
    public SearchHits operationSearch(Query query) {
        return operations.search(query,entityClass);
    }

    public List<IndexedObjectInformation> bulkIndex(List<IndexQuery> indexQueryList){
        Document document = AnnotationUtils.findAnnotation(entityClass, Document.class);
        return operations.bulkIndex(indexQueryList, IndexCoordinates.of(document.indexName()));
    }

    public List<T> internalSearch(Query query) {
        if (query == null) {
            log.error("Failed to build search request");
            return Collections.emptyList();
        }
        try {
            return operations.search(query, entityClass).stream()
                .map(a->a.getContent()).collect(Collectors.toList());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<T>  getAllCreatedSince(final Date date, Class<T> clazz) {

        NativeSearchQueryBuilder query = 검색엔진_유틸.buildSearchQuery(
            "created",
            date
        );
        return this.internalSearch(query.build());

    }

    @Override
    public List<T>  searchCreatedSince(final 검색조건 dto, final Date date, Class<T> clazz) {

        NativeSearchQueryBuilder query = 검색엔진_유틸.buildSearchQuery(
            dto,
            date
        );
        return this.internalSearch(query.build());

    }

}
