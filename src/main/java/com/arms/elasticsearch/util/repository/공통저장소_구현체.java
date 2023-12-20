package com.arms.elasticsearch.util.repository;

import com.arms.elasticsearch.helper.인덱스_유틸;
import com.arms.elasticsearch.util.검색결과_목록_메인;
import com.arms.elasticsearch.util.검색엔진_유틸;
import com.arms.elasticsearch.util.검색조건;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Slf4j
public class 공통저장소_구현체<T,ID extends Serializable> extends SimpleElasticsearchRepository<T,ID> implements 공통저장소<T,ID> {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private final Class<T> entityClass;
    private final ElasticsearchOperations operations;

    public 공통저장소_구현체(ElasticsearchEntityInformation<T, ID> metadata
            , ElasticsearchOperations operations) {
        super(metadata, operations);
        this.entityClass = metadata.getJavaType();
        this.operations = operations;
    }

    @Override
    public 검색결과_목록_메인 aggregationSearch(Query query) {
        SearchHits<T> search = operations.search(query, entityClass);
        System.out.println(search);
        return new 검색결과_목록_메인(operations.search(query,entityClass));
    }

    @Override
    public 검색결과_목록_메인 aggregationSearch(Query query, String newIndex) {
        if(newIndex == null || newIndex.isEmpty()) {
            log.error("Failed to parameter newIndex is empty");
            return null;
        }

        SearchHits<T> search = operations.search(query, entityClass, IndexCoordinates.of(newIndex));
        System.out.println(search);
        return new 검색결과_목록_메인(operations.search(query,entityClass, IndexCoordinates.of(newIndex)));
    }

    public List<IndexedObjectInformation> bulkIndex(List<IndexQuery> indexQueryList){
        Document document = AnnotationUtils.findAnnotation(entityClass, Document.class);

        if (document == null) {
            로그.error("bulkIndex Document null 오류");
            throw new IllegalArgumentException("bulkIndex Document null 오류");
        }

        return operations.bulkIndex(indexQueryList, IndexCoordinates.of(document.indexName()));
    }

    public List<T> normalSearch(Query query) {
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

    public List<T> normalSearch(Query query, String newIndex) {
        if (query == null) {
            log.error("Failed to build search request");
            return Collections.emptyList();
        }
        if(newIndex == null || newIndex.isEmpty()) {
            log.error("Failed to index is empty");
            return Collections.emptyList();
        }

        try {
            return operations.search(query, entityClass, IndexCoordinates.of(newIndex)).stream()
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
        return this.normalSearch(query.build());

    }

    @Override
    public List<T>  searchCreatedSince(final 검색조건 dto, final Date date, Class<T> clazz) {

        NativeSearchQueryBuilder query = 검색엔진_유틸.buildSearchQuery(
                dto,
                date
        );
        return this.normalSearch(query.build());

    }

}
