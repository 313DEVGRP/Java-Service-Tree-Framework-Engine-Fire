package com.arms.api.alm.issue.base.service;

import com.arms.api.alm.issue.base.model.지라이슈_엔티티;
import com.arms.api.util.model.dto.response.검색어_검색결과;
import com.arms.api.util.model.dto.request.검색어_날짜포함_검색_요청;
import com.arms.api.util.model.dto.request.검색어_검색_집계_하위_요청;
import com.arms.egovframework.javaservice.esframework.factory.creator.query.쿼리_생성기;
import com.arms.egovframework.javaservice.esframework.model.vo.버킷_집계_결과_목록_합계;
import org.springframework.data.elasticsearch.core.SearchHit;

public interface 지라이슈_검색_서비스 {

    버킷_집계_결과_목록_합계 전체_집계결과_가져오기(쿼리_생성기 쿼리_생성기);

    검색어_검색결과<SearchHit<지라이슈_엔티티>> 지라이슈_날짜포함_검색(검색어_날짜포함_검색_요청 검색어_날짜포함_검색_요청);

    버킷_집계_결과_목록_합계 이슈_프로젝트명_집계(검색어_검색_집계_하위_요청 검색어_집계_요청);
}
