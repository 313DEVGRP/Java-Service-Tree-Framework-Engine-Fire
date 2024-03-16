package com.arms.elasticsearch.util.repository;

import com.arms.elasticsearch.util.검색결과_목록_메인;
import com.arms.elasticsearch.util.검색조건;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@NoRepositoryBean
public interface 공통저장소<T,ID extends Serializable> extends ElasticsearchRepository<T,ID> {

	검색결과_목록_메인 aggregationSearch(Query query) ;

	검색결과_목록_메인 aggregationSearch(Query query, String newIndex) ;

	List<IndexedObjectInformation> bulkIndex(List<IndexQuery> indexQueryList);

	//recentAll(true and false) 전부 조회
	List<T> normalSearchAll();

	List<T> normalRecentTrueAll();

	List<T> normalSearch(Query query);

	List<T> normalSearch(Query query, String newIndex);

	<S extends T> S updateSave(S entity, String indexName) ;

	SearchHits<T> search(Query query);

	List<T>  getAllCreatedSince(Date date, Class<T> clazz);

	List<T>  searchCreatedSince(검색조건 dto, Date date, Class<T> clazz);

	boolean 인덱스확인_및_생성_매핑(String 인덱스명);

	boolean 인덱스_존재_확인(String 인덱스명);

	boolean 인덱스삭제(String 삭제할_지라이슈인덱스);

	boolean 리인덱스(String 현재_인덱스, String 백업_인덱스);
}
