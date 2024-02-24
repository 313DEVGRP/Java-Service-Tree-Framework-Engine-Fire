package com.arms.elasticsearch.util.repository;

import com.arms.api.engine.models.지라이슈;
import com.arms.api.serverinfo.model.서버정보_엔티티;
import com.arms.elasticsearch.util.custom.index.ElasticSearchIndex;
import com.arms.elasticsearch.util.검색결과_목록_메인;
import com.arms.elasticsearch.util.검색엔진_유틸;
import com.arms.elasticsearch.util.검색조건;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchEntityInformation;
import org.springframework.data.elasticsearch.repository.support.SimpleElasticsearchRepository;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
        return new 검색결과_목록_메인(operations.search(query,entityClass,indexName()));
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

        return operations.bulkIndex(indexQueryList, indexName());
    }


    public List<SearchHit<T>> fetchSearchHits(Query query) {

        if (query == null) {
            log.error("Failed to build search request");
            return Collections.emptyList();
        }

        try {

            ElasticSearchIndex annotation = AnnotationUtils.findAnnotation(entityClass, ElasticSearchIndex.class);

            if(annotation==null){
                return operations.search(query, entityClass).stream()
                        .collect(Collectors.toList());
            }

            // 확인필요
            return operations.search(query, entityClass,indexName()).stream()
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }


    public List<T> normalSearch(Query query) {
        if (query == null) {
            log.error("Failed to build search request");
            return Collections.emptyList();
        }

        try {

            ElasticSearchIndex annotation = AnnotationUtils.findAnnotation(entityClass, ElasticSearchIndex.class);

            if(annotation==null){
                return operations.search(query, entityClass).stream()
                    .map(SearchHit::getContent).collect(Collectors.toList());
            }

            return operations.search(query, entityClass,indexName()).stream()
                    .map(SearchHit::getContent).collect(Collectors.toList());

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

    @Override
    public boolean 인덱스확인_및_생성_매핑(String 인덱스명) {
        IndexOperations 인덱스작업 = operations.indexOps(IndexCoordinates.of(인덱스명));
        boolean 인덱스확인 = 인덱스_존재_확인(인덱스명);

        if (인덱스확인) {
            return true;
        }

        boolean 생성확인 = 인덱스작업.create();
        if (!생성확인) {
            로그.error(this.getClass().getName() + " :: 인덱스확인_및_생성_매핑(String 인덱스명) :: " + 인덱스명 +" -> 인덱스 생성에 실패하였습니다." );
            return 생성확인;
        }

        boolean 매핑확인 = 인덱스작업.putMapping(인덱스작업.createMapping());

        if (!매핑확인) {
            로그.error(this.getClass().getName() + " :: 인덱스확인_및_생성_매핑(String 인덱스명) :: " + 인덱스명 +" -> 인덱스 매핑 추가에 실패하였습니다." );
            return 매핑확인;
        }

        인덱스작업.refresh();
        로그.info(this.getClass().getName() +  " :: 인덱스확인_및_생성_매핑(String 인덱스명) -> " + 인덱스명);

        return 매핑확인;
    }

    @Override
    public boolean 인덱스_존재_확인(String 인덱스명) {
        IndexOperations 인덱스작업 = operations.indexOps(IndexCoordinates.of(인덱스명));
        return 인덱스작업.exists();
    }

    private boolean 인덱스_백업_생성(String 백업_인덱스, Class<?> clazz) {
        IndexOperations 백업_인덱스작업 = operations.indexOps(IndexCoordinates.of(백업_인덱스));
        var 매핑정보 = 백업_인덱스작업.createMapping(clazz);
        백업_인덱스작업.create();
        백업_인덱스작업.putMapping(매핑정보);

        return 백업_인덱스작업.exists();
    }

    @Override
    public boolean 인덱스삭제(String 삭제할_지라이슈인덱스) {
        boolean 삭제결과 = false;

        IndexOperations 인덱스작업 = operations.indexOps(IndexCoordinates.of(삭제할_지라이슈인덱스));

        try {
            if (인덱스작업.exists()) {
                삭제결과 = 인덱스작업.delete();
            }
        }
        catch(Exception e) {
            로그.error(this.getClass().getName() + " :: 인덱스삭제(String 삭제할_지라이슈인덱스) :: 삭제할_지라이슈인덱스 -> " + 삭제할_지라이슈인덱스);
            로그.error(this.getClass().getName() + " :: 인덱스삭제(String 삭제할_지라이슈인덱스) :: 에러 메세지 -> " + e.getMessage());
        }

        return 삭제결과;
    }

    @Override
    public boolean 리인덱스(String 현재_인덱스, String 백업_인덱스) {

        if (!인덱스_존재_확인(백업_인덱스)) {
            if (!인덱스_존재_확인(현재_인덱스)) {
                로그.info("현재 인덱스 정보가 없습니다.");
                return true;
            }

            if (!인덱스_백업_생성(백업_인덱스, entityClass)) {
                로그.error(this.getClass().getName() + " :: 리인덱스(String 현재_인덱스, String 백업_인덱스) :: 백업 인덱스 명 -> " + 백업_인덱스);
                로그.error(this.getClass().getName() + " :: 리인덱스(String 현재_인덱스, String 백업_인덱스) :: 인덱스 백업 생성 실패!");

                return false;
            }
        } else {
            로그.info("백업 인덱스 정보가 있습니다.");
            return true;
        }

        boolean 결과 = false;

        int 페이지 = 0;
        int 페이지크기 = 1000;

        while (true) {
            NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withPageable(PageRequest.of(페이지, 페이지크기));

            List<T> 목록 = this.normalSearch(searchQuery.build());

            if (목록.isEmpty()) {
                break;
            }

            List<IndexQuery> indexQueries = new ArrayList<>();

            for (T 이슈 : 목록) {
                IndexQuery indexQuery = new IndexQueryBuilder()
                    .withObject(이슈)
                    .build();

                indexQueries.add(indexQuery);
            }

            List<IndexedObjectInformation> indexedObjectInformations = operations.bulkIndex(indexQueries, IndexCoordinates.of(백업_인덱스));
            if (목록.size() == indexedObjectInformations.size()) {
                결과 = true;
            }

            페이지++;
        }

        return 결과;
    }

    public IndexCoordinates indexName() {
        Document document = AnnotationUtils.findAnnotation(entityClass, Document.class);
        if(document!=null){
            String indexName;
            if(StringUtils.contains(document.indexName(),"fluentd")) {
                // 포맷 지정
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                // 포맷 적용
                String formattedDate = LocalDate.now().format(formatter);
                indexName = document.indexName() + "-" + formattedDate;
            } else {
                indexName = document.indexName() + "-" + LocalDate.now();
            }

            return IndexCoordinates.of(indexName);
        }
       throw new RuntimeException("인덱스명을 확인해주시길 바랍니다.");
    }

    @Override
    public <S extends T> S save(S entity){
        ElasticSearchIndex annotation = AnnotationUtils.findAnnotation(entityClass, ElasticSearchIndex.class);
        if(annotation==null){
            return operations.save(entity);
        }
        return operations.save(entity, indexName());
    };

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        ElasticSearchIndex annotation = AnnotationUtils.findAnnotation(entityClass, ElasticSearchIndex.class);
        if(annotation==null){
            return operations.save(entities);
        }
        return operations.save(entities, indexName());
    }


}
