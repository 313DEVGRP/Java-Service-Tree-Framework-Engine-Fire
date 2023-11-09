package com.arms.elasticsearch.util.repository;

import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface 공통저장소<T,ID extends Serializable> extends ElasticsearchRepository<T,ID> {

    SearchHits operationSearch(Query query) ;

}
