package com.arms.api.alm.fluentd.service;

import com.arms.api.util.model.dto.검색어_검색결과;
import com.arms.api.util.model.dto.검색어_페이징처리_요청;
import com.arms.api.util.model.dto.검색어_날짜포함_검색_요청;
import com.arms.api.util.model.dto.검색어_집계_요청;
import com.arms.api.alm.fluentd.model.플루언트디_엔티티;
import com.arms.elasticsearch.query.factory.creator.쿼리_생성기;
import com.arms.elasticsearch.버킷_집계_결과_목록_합계;
import org.springframework.data.elasticsearch.core.SearchHit;

public interface 플루언트디_서비스 {

    버킷_집계_결과_목록_합계 전체_집계결과_가져오기(쿼리_생성기 쿼리_생성기);
    버킷_집계_결과_목록_합계 집계결과_가져오기(쿼리_생성기 쿼리_생성기);

    검색어_검색결과<SearchHit<플루언트디_엔티티>> 플루언트디_검색(검색어_페이징처리_요청 검색어_기본_검색_요청);

    검색어_검색결과<SearchHit<플루언트디_엔티티>> 플루언트디_날짜포함_검색(검색어_날짜포함_검색_요청 검색어_날짜포함_검색_요청);

    버킷_집계_결과_목록_합계 플루언트디_로그네임_집계(검색어_집계_요청 검색어_집계_요청);

    void 커넥션_상태_유지();
}
