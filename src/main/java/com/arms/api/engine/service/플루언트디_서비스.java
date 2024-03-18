package com.arms.api.engine.service;

import com.arms.api.engine.model.dto.검색어_검색결과;
import com.arms.api.engine.model.dto.검색어_기본_검색_요청;
import com.arms.api.engine.model.dto.검색어_날짜포함_검색_요청;
import com.arms.api.engine.model.dto.검색어_집계_요청;
import com.arms.api.engine.model.플루언트디;
import com.arms.elasticsearch.query.쿼리_추상_팩토리;
import com.arms.elasticsearch.검색결과_목록_메인;
import org.springframework.data.elasticsearch.core.SearchHit;

public interface 플루언트디_서비스 {

    검색결과_목록_메인 전체_집계결과_가져오기(쿼리_추상_팩토리 쿼리추상팩토리);
    검색결과_목록_메인 집계결과_가져오기(쿼리_추상_팩토리 쿼리추상팩토리);

    검색어_검색결과<SearchHit<플루언트디>> 플루언트디_검색(검색어_기본_검색_요청 검색어_기본_검색_요청);

    검색어_검색결과<SearchHit<플루언트디>> 플루언트디_날짜포함_검색(검색어_날짜포함_검색_요청 검색어_날짜포함_검색_요청);

    검색결과_목록_메인 플루언트디_로그네임_집계(검색어_집계_요청 검색어_집계_요청);
}
