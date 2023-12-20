package com.arms.elasticsearch.util.repository;

import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.arms.elasticsearch.util.검색결과_목록_메인;
import com.arms.elasticsearch.util.검색조건;

@NoRepositoryBean
public interface 공통저장소<T,ID extends Serializable> extends ElasticsearchRepository<T,ID> {

	검색결과_목록_메인 aggregationSearch(Query query) ;

	검색결과_목록_메인 aggregationSearch(Query query, String newIndex) ;

    List<IndexedObjectInformation> bulkIndex(List<IndexQuery> indexQueryList);

    List<T> normalSearch(Query query);

	List<T> normalSearch(Query query, String newIndex);

	List<T>  getAllCreatedSince(Date date, Class<T> clazz);

	List<T>  searchCreatedSince(검색조건 dto, Date date, Class<T> clazz);
}
