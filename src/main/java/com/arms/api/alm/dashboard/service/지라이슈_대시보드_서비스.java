package com.arms.api.alm.dashboard.service;

import com.arms.api.util.model.dto.트리맵_검색__집계_하위_요청;
import com.arms.api.util.model.vo.Worker;
import com.arms.elasticsearch.query.factory.creator.쿼리_생성기;
import com.arms.elasticsearch.버킷_집계_결과_목록_합계;

import java.util.List;

public interface 지라이슈_대시보드_서비스 {

    버킷_집계_결과_목록_합계 집계결과_가져오기(쿼리_생성기 쿼리_생성기);//

    List<Worker> 작업자_별_요구사항_별_관여도(트리맵_검색__집계_하위_요청 트리맵_집계_요청);//

}
