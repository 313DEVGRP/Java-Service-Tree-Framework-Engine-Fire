package com.arms.api.alm.issue.base.service;

import com.arms.api.alm.issue.base.model.지라이슈_엔티티;
import com.arms.api.util.model.dto.검색어_검색결과;
import com.arms.api.util.model.dto.검색어_페이징처리_요청;
import com.arms.api.util.model.dto.검색어_날짜포함_검색_요청;
import com.arms.api.util.model.dto.검색어_집계_요청;
import com.arms.elasticsearch.query.EsQuery;
import com.arms.elasticsearch.query.base.기본_정렬_요청;
import com.arms.elasticsearch.query.esquery.EsQueryBuilder;
import com.arms.elasticsearch.query.esquery.EsQueryString;
import com.arms.elasticsearch.query.esquery.EsSortQuery;
import com.arms.elasticsearch.query.factory.creator.old.일반_검색_쿼리_생성기;
import com.arms.elasticsearch.query.factory.creator.하위_계층_집계_쿼리_생성기;
import com.arms.elasticsearch.query.filter.QueryStringFilter;
import com.arms.elasticsearch.query.filter.RangeQueryFilter;
import com.arms.elasticsearch.query.factory.creator.쿼리_생성기;
import com.arms.elasticsearch.버킷_집계_결과_목록_합계;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("지라이슈_검색_서비스")
@AllArgsConstructor
public class 지라이슈_검색_서비스_프로세스 implements 지라이슈_검색_서비스 {

    private com.arms.api.alm.issue.base.repository.지라이슈_저장소 지라이슈_저장소;

    @Override
    public 버킷_집계_결과_목록_합계 전체_집계결과_가져오기(쿼리_생성기 쿼리_생성기) {

        return 지라이슈_저장소.전체버킷집계(
                쿼리_생성기.생성()
        );
    }

    @Override
    public 검색어_검색결과<SearchHit<지라이슈_엔티티>> 지라이슈_검색(검색어_페이징처리_요청 검색어_기본_검색_요청) {
        EsQuery esQuery = new EsQueryBuilder().queryString(new EsQueryString(검색어_기본_검색_요청.get검색어()));
        SearchHits<지라이슈_엔티티> 지라이슈_검색결과= 지라이슈_저장소.search(일반_검색_쿼리_생성기.of(검색어_기본_검색_요청, esQuery).생성());
        검색어_검색결과<SearchHit<지라이슈_엔티티>> 검색결과_목록 = new 검색어_검색결과<>();
        검색결과_목록.set검색결과_목록(지라이슈_검색결과.getSearchHits());
        검색결과_목록.set결과_총수(지라이슈_검색결과.getTotalHits());
        return 검색결과_목록;
    }

    @Override
    public 검색어_검색결과<SearchHit<지라이슈_엔티티>> 지라이슈_날짜포함_검색(검색어_날짜포함_검색_요청 검색어_날짜포함_검색_요청) {
        String start_date = null;
        String end_date = null;
        if(검색어_날짜포함_검색_요청.get시작_날짜() != null && !검색어_날짜포함_검색_요청.get시작_날짜().isBlank()) {
            start_date = 검색어_날짜포함_검색_요청.get시작_날짜();
        }
        if(검색어_날짜포함_검색_요청.get끝_날짜() != null &&!검색어_날짜포함_검색_요청.get끝_날짜().isBlank()) {
            end_date = 검색어_날짜포함_검색_요청.get끝_날짜();
        }

        EsQuery esQuery = new EsQueryBuilder()
                .bool(new RangeQueryFilter("@timestamp", start_date, end_date,"fromto"),
                        new QueryStringFilter(검색어_날짜포함_검색_요청.get검색어()))
                .sort(new EsSortQuery(
                        List.of(
                                기본_정렬_요청.builder().필드("@timestamp").정렬기준("desc").build()
                        )
                ));
        SearchHits<지라이슈_엔티티> 지라이슈_검색결과= 지라이슈_저장소.search(일반_검색_쿼리_생성기.of(검색어_날짜포함_검색_요청, esQuery).생성());
        검색어_검색결과<SearchHit<지라이슈_엔티티>> 검색결과_목록 = new 검색어_검색결과<>();
        if(지라이슈_검색결과 != null && !지라이슈_검색결과.isEmpty()) {
            검색결과_목록.set검색결과_목록(지라이슈_검색결과.getSearchHits());
            검색결과_목록.set결과_총수(지라이슈_검색결과.getTotalHits());
        }
        return 검색결과_목록;
    }

    @Override
    public 버킷_집계_결과_목록_합계 이슈_프로젝트명_집계(검색어_집계_요청 검색어_집계_요청) {
        String start_date = null;
        String end_date = null;
        if(검색어_집계_요청.get시작_날짜() != null && !검색어_집계_요청.get시작_날짜().isBlank()) {
            start_date = 검색어_집계_요청.get시작_날짜();
        }
        if(검색어_집계_요청.get끝_날짜() != null &&!검색어_집계_요청.get끝_날짜().isBlank()) {
            end_date = 검색어_집계_요청.get끝_날짜();
        }

        EsQuery esQuery = new EsQueryBuilder()
                .bool(new RangeQueryFilter("@timestamp", start_date, end_date,"fromto"),
                        new QueryStringFilter(검색어_집계_요청.get검색어()));

        버킷_집계_결과_목록_합계 집계_결과 = this.전체_집계결과_가져오기(하위_계층_집계_쿼리_생성기.of(검색어_집계_요청, esQuery));
        return 집계_결과;
    }
}
