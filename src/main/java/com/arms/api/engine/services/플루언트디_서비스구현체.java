package com.arms.api.engine.services;

import com.arms.api.engine.dtos.검색어_기본_검색_요청;
import com.arms.api.engine.models.플루언트디;
import com.arms.api.engine.repositories.플루언트디_저장소;
import com.arms.elasticsearch.util.query.EsQuery;
import com.arms.elasticsearch.util.query.EsQueryBuilder;
import com.arms.elasticsearch.util.query.query_string.QueryString;
import com.arms.elasticsearch.util.query.일반_검색_요청;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("플루언트디_서비스")
@AllArgsConstructor
public class 플루언트디_서비스구현체 implements 플루언트디_서비스{

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private 플루언트디_저장소 플루언트디_저장소;

    public List<SearchHit<플루언트디>> 플푸언트디_검색(검색어_기본_검색_요청 검색어_기본_검색_요청){
        EsQuery esQuery = new EsQueryBuilder().queryString(new QueryString(검색어_기본_검색_요청.get검색어()));
        return 플루언트디_저장소.fetchSearchHits(일반_검색_요청.of(검색어_기본_검색_요청, esQuery).생성());
    };
}
