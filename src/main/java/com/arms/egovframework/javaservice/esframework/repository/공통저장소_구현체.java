package com.arms.egovframework.javaservice.esframework.repository;

import com.arms.egovframework.javaservice.esframework.annotation.ElasticSearchIndex;
import com.arms.egovframework.javaservice.esframework.annotation.Recent;
import com.arms.egovframework.javaservice.esframework.annotation.RollingIndexName;
import com.arms.egovframework.javaservice.esframework.EsQuery;
import com.arms.egovframework.javaservice.esframework.esquery.EsQueryBuilder;
import com.arms.egovframework.javaservice.esframework.filter.TermsQueryFilter;
import com.arms.egovframework.javaservice.esframework.model.vo.메트릭_집계_결과_목록_합계;
import com.arms.egovframework.javaservice.esframework.model.vo.버킷_집계_결과_목록_합계;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchEntityInformation;
import org.springframework.data.elasticsearch.repository.support.SimpleElasticsearchRepository;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.arms.egovframework.javaservice.esframework.util.ReflectionUtil.*;
import static java.util.stream.Collectors.*;

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
    public 메트릭_집계_결과_목록_합계 전체메트릭집계(Query query) {
        return new 메트릭_집계_결과_목록_합계(operations.search(query,entityClass));
    }

    @Override
    public 버킷_집계_결과_목록_합계 버킷집계(Query query) {
        NativeSearchQuery nativeSearchQuery = recentQueryMerge((NativeSearchQuery)query);
        return new 버킷_집계_결과_목록_합계(operations.search(nativeSearchQuery,entityClass));
    }

    @Override
    public 버킷_집계_결과_목록_합계 전체버킷집계(Query query) {
        return new 버킷_집계_결과_목록_합계(operations.search(query,entityClass));
    }

    //현재 디펜던시 없음
    public List<IndexedObjectInformation> bulkIndex(List<IndexQuery> indexQueryList){
        Document document = AnnotationUtils.findAnnotation(entityClass, Document.class);

        if (document == null) {
            로그.error("bulkIndex Document null 오류");
            throw new IllegalArgumentException("bulkIndex Document null 오류");
        }

        return operations.bulkIndex(indexQueryList, 인덱스_명());
    }

    @Override
    public List<T> normalSearchAll() {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        return operations.search(nativeSearchQueryBuilder.build(), entityClass).stream()
            .map(SearchHit::getContent).collect(toList());
    }

    @Override
    public List<T> normalRecentTrueAll() {

        String recentFieldName = fieldInfo(entityClass, Recent.class).getName();
        TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery(recentFieldName, true);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(termsQueryBuilder);

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);

        return operations.search(nativeSearchQueryBuilder.build(), entityClass).stream()
                .map(SearchHit::getContent).collect(toList());
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
                    .map(SearchHit::getContent).collect(toList());
            }

            NativeSearchQuery searchQuery = recentQueryMerge((NativeSearchQuery)query);

            return operations.search(searchQuery, entityClass).stream()
                    .map(SearchHit::getContent).collect(toList());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private NativeSearchQuery recentQueryMerge(NativeSearchQuery query) {

        String recentFieldName = fieldInfo(entityClass, Recent.class).getName();

        EsQuery esQuery
            = new EsQueryBuilder()
                    .bool(
                        new TermsQueryFilter(recentFieldName, true)
                    );
        BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {
        });

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .filter(boolQuery);
        Optional.ofNullable(query.getQuery()).ifPresent(boolQueryBuilder::filter);

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withMaxResults(Optional.ofNullable(query.getMaxResults()).orElse(0));

        Optional.ofNullable(query.getAggregations()).ifPresent(args-> args.forEach(nativeSearchQueryBuilder::addAggregation));

        return nativeSearchQueryBuilder.build();

    }

    //우선 증분처리 제외
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

    //현재 디펜던시 없음
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

    public IndexCoordinates 인덱스_명() {
        Document document = AnnotationUtils.findAnnotation(entityClass, Document.class);
        if(document!=null){
            Method method = methodInfo(entityClass, RollingIndexName.class);
            if(method!=null){
                try{
                    Constructor<T> constructor = entityClass.getConstructor();
                    T t = constructor.newInstance();
                    return IndexCoordinates.of(document.indexName()+"-"+method.invoke(t));
                }catch (Exception e) {
                    return IndexCoordinates.of(document.indexName());
                }
            }else{
                return IndexCoordinates.of(document.indexName());
            }
        }
        throw new IllegalArgumentException("인덱스명을 확인해주시길 바랍니다.");
    }

    @Override
    public <S extends T> S updateSave(S entity, String indexName)  {
        return operations.save(entity,IndexCoordinates.of(indexName));
    }

    ///////////////////증분 저장 모음//////////////////////
    @Override
    public <S extends T> S save(S entity){
        ElasticSearchIndex annotation = AnnotationUtils.findAnnotation(entityClass, ElasticSearchIndex.class);
        if(annotation==null){
            return operations.save(entity);
        }

        Iterable<S> s = this.saveAll(List.of(entity));
        return StreamSupport.stream(s.spliterator() ,false).findFirst().orElseThrow(()->new RuntimeException("저장할 데이터가 없습니다."));
    };

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        ElasticSearchIndex annotation = AnnotationUtils.findAnnotation(entityClass, ElasticSearchIndex.class);
        if(annotation==null){
            return operations.save(entities);
        }
        Map<Object, List<SearchHit<T>>> originalEntityMap = this.originalList(entities);

        List<S> recentTrueList = StreamSupport.stream(entities.spliterator(), false)
            .map(newEntity -> {
                try {
                    Object keyObject = fieldInfo(newEntity.getClass(), Id.class).get(newEntity);

                    if(originalEntityMap!=null&&originalEntityMap.containsKey(keyObject)){

                        SearchHit<T> searchHit = originalEntityMap.get(keyObject)
                            .stream()
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("값이 비워 있습니다."));

                        if(newEntity.equals(searchHit.getContent())){
                            return null;
                        }else{
                            fieldInfo(newEntity.getClass(), Recent.class).setBoolean(newEntity, true);
                            return newEntity;
                        }

                    }else{
                        fieldInfo(newEntity.getClass(), Recent.class).setBoolean(newEntity, true);
                        return newEntity;
                    }

                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(e);
                }
            })
            .filter(Objects::nonNull)
            .collect(toList());

        Map<String, List<T>> collect = StreamSupport.stream(entities.spliterator(), false)
            .map(newEntity -> {
                try {
                    Object keyObject = fieldInfo(newEntity.getClass(), Id.class).get(newEntity);
                    return Optional.ofNullable(originalEntityMap)
                        .map(map -> map.getOrDefault(keyObject, Collections.emptyList())
                            .stream()
                            .filter(hit -> !newEntity.equals(hit.getContent()))
                            .map(hit -> {
                                try {
                                    fieldInfo(hit.getContent().getClass(), Recent.class).setBoolean(hit.getContent(),
                                        false);
                                    return hit;
                                } catch (IllegalAccessException e) {
                                    throw new IllegalArgumentException(e);
                                }
                            }).collect(toList()))
                        .orElseGet(() -> null);

                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(e);
                }
            })
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .collect(groupingBy(SearchHit::getIndex, mapping(SearchHit::getContent, toList())));

        collect
            .forEach((key, value) -> operations.save(value, IndexCoordinates.of(key)));
        IndexCoordinates indexCoordinates = 인덱스_명();
        return operations.save(recentTrueList, indexCoordinates);
    }


    private <S extends T> Map<Object,List<SearchHit<T>>> originalList(Iterable<S> entities){
        String recentFieldName = fieldInfo(entityClass, Recent.class).getName();
        String idFieldName = fieldInfo(entityClass,Id.class).getName();

        EsQuery esQuery
            = new EsQueryBuilder()
            .bool(
                new TermsQueryFilter(idFieldName, fieldValues(entities,Id.class)),
                new TermsQueryFilter(recentFieldName,true)
            );

        BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {
        });

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
            .withQuery(boolQuery)
            .build();


        return Optional.ofNullable(this.search(searchQuery)).map(searchHits ->
            searchHits.getSearchHits().stream()
                .sorted(Comparator.comparing(a->a.getIndex(),Comparator.reverseOrder()))
                .collect(groupingBy(a -> {
                    try {
                        return fieldInfo(a.getContent().getClass(), Id.class).get(a.getContent());
                    } catch (IllegalAccessException e) {
                        throw new IllegalArgumentException(e);
                    }
                }))
        ).orElse(null);
    }

    public SearchHits<T> search(Query query) {
        try{
            return operations.search(query, entityClass);
        }catch (NoSuchIndexException e){
            if(e.getMessage()!=null && e.getMessage().contains("no such index")){
                return null;
            }
            throw e;
        }catch (Exception e){
            log.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public Set<String> findIndexNamesByAlias(IndexCoordinates indexCoordinates) {
        IndexOperations indexOperations = operations.indexOps(indexCoordinates);
        return indexOperations.getAliasesForIndex(indexCoordinates.getIndexName()).keySet();
    }

    @Override
    public boolean deleteIndex(IndexCoordinates indexCoordinates) {
        IndexOperations indexOperations = operations.indexOps(indexCoordinates);
        return indexOperations.delete();
    }


}
