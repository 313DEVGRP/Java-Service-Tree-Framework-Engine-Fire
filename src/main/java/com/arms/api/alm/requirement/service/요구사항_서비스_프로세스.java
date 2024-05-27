package com.arms.api.alm.requirement.service;

import com.arms.api.alm.issue.base.model.지라이슈_엔티티;
import com.arms.api.alm.issue.base.repository.지라이슈_저장소;
import com.arms.api.util.model.dto.지라이슈_기본_검색__집계_하위_요청;
import com.arms.api.util.model.dto.지라이슈_제품_및_제품버전_검색__집계_하위_요청;
import com.arms.api.util.model.enums.IsReqType;
import com.arms.elasticsearch.query.EsQuery;
import com.arms.elasticsearch.query.base.기본_검색_집계_요청;
import com.arms.elasticsearch.query.base.기본_검색_집계_하위_요청;
import com.arms.elasticsearch.query.base.집계_하위_요청;
import com.arms.elasticsearch.query.esquery.EsQueryBuilder;
import com.arms.elasticsearch.query.must.TermQueryMust;
import com.arms.elasticsearch.query.factory.creator.기본_쿼리_생성기;
import com.arms.elasticsearch.query.factory.creator.중첩_집계_쿼리_생성기;
import com.arms.elasticsearch.query.filter.ExistsQueryFilter;
import com.arms.elasticsearch.query.filter.TermsQueryFilter;
import com.arms.elasticsearch.query.factory.creator.query.쿼리_생성기;
import com.arms.elasticsearch.버킷_집계_결과;
import com.arms.elasticsearch.버킷_집계_결과_목록_합계;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service("요구사항_서비스")
@AllArgsConstructor
public class 요구사항_서비스_프로세스 implements 요구사항_서비스 {
    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private 지라이슈_저장소 지라이슈_저장소;

    @Override
    public Map<String, Long> 제품서비스별_담당자_이름_통계(Long 지라서버_아이디, Long 제품서비스_아이디) {

        EsQuery esQuery
            = new EsQueryBuilder().bool(new TermQueryMust("pdServiceId", 제품서비스_아이디));

        기본_검색_집계_요청 일반_집계_요청 = new 기본_검색_집계_요청() {};
        일반_집계_요청.set메인그룹필드("assignee.assignee_displayName.keyword");
        일반_집계_요청.set컨텐츠보기여부(false);

        기본_쿼리_생성기.집계검색(일반_집계_요청,esQuery);

        // 요구사항 vs 연결된이슈&서브테스크 구분안하고 한번에
        버킷_집계_결과_목록_합계 버킷_집계_결과_목록_합계 = 지라이슈_저장소.버킷집계( 기본_쿼리_생성기.집계검색(일반_집계_요청,esQuery).생성());
        Long 결과 = 버킷_집계_결과_목록_합계.get전체합계();
        로그.info("검색결과 개수: " + 결과);

        List<버킷_집계_결과> 담당자별_집계 = 버킷_집계_결과_목록_합계.get검색결과().get("group_by_assignee.assignee_displayName.keyword");

        long 담당자_총합 = 0;
        Map<String, Long> 제품서비스별_하위이슈_담당자_집계 = new HashMap<>();
        for (버킷_집계_결과 담당자 : 담당자별_집계) {
            String 담당자_이메일 = 담당자.get필드명();
            long 개수 = 담당자.get개수();
            log.info("담당자: " + 담당자_이메일 + ", Count: " + 개수);
            담당자_총합 += 개수;
            제품서비스별_하위이슈_담당자_집계.put(담당자_이메일, 개수);
        }
        제품서비스별_하위이슈_담당자_집계.put("담당자 미지정", 결과 - 담당자_총합);

        return 제품서비스별_하위이슈_담당자_집계;
    }

    @Override
    public List<지라이슈_엔티티> 지라이슈_조회(쿼리_생성기 쿼리_생성기) {
        return 지라이슈_저장소.normalSearch(
                쿼리_생성기.생성()
        );
    }

    @Override
    public Map<String, Long> 제품서비스_버전별_상태값_통계(Long 제품서비스_아이디, Long[] 버전_아이디들){

        EsQuery esQuery = new EsQueryBuilder()
                .bool(
                         new TermsQueryFilter("pdServiceId", Optional.ofNullable(제품서비스_아이디).filter(a->a!=null&&a>9L).map(a->List.of(a)).orElse(null))
                        ,new TermsQueryFilter("pdServiceVersions", Arrays.stream(버전_아이디들).filter(a->a!=null&&a>9L).collect(toList()))
                );
        지라이슈_기본_검색__집계_하위_요청 지라이슈_일반_집계_요청 = new 지라이슈_기본_검색__집계_하위_요청();
        지라이슈_일반_집계_요청.set메인그룹필드("status.status_name.keyword");
        지라이슈_일반_집계_요청.set컨텐츠보기여부(false);
        버킷_집계_결과_목록_합계 버킷_집계_결과_목록_합계 = 지라이슈_저장소.버킷집계(중첩_집계_쿼리_생성기.포괄(지라이슈_일반_집계_요청, esQuery).생성());

        List<버킷_집계_결과> 상태값통계 = 버킷_집계_결과_목록_합계.get검색결과().get("group_by_status.status_name.keyword");

        Map<String, Long> 제품서비스_버전별_집계 = new HashMap<>();
        for (버킷_집계_결과 상태값 : 상태값통계) {
            String statusName = 상태값.get필드명();
            long docCount = 상태값.get개수();
            log.info("Status Name: " + statusName + ", Count: " + docCount);

            제품서비스_버전별_집계.put(statusName, docCount);
        }

        return 제품서비스_버전별_집계;

    }

    @Override
    public List<버킷_집계_결과> 제품_요구사항별_담당자_목록(지라이슈_제품_및_제품버전_검색__집계_하위_요청 지라이슈_제품_및_제품버전_집계_요청) {

        boolean 요구사항여부 = false;
        if (지라이슈_제품_및_제품버전_집계_요청.getIsReqType() == IsReqType.REQUIREMENT) {
            요구사항여부 = true;
        }
        else if (지라이슈_제품_및_제품버전_집계_요청.getIsReqType() == IsReqType.ISSUE) {
            요구사항여부 = false;
        }

        EsQuery esQuery = new EsQueryBuilder()
                .bool(new TermQueryMust("pdServiceId", 지라이슈_제품_및_제품버전_집계_요청.getPdServiceLink()),
                        new TermQueryMust("isReq", 요구사항여부),
                        new ExistsQueryFilter("assignee")
                );

        기본_검색_집계_하위_요청 하위_집계_요청 = new 기본_검색_집계_하위_요청() {};

        하위_집계_요청.set집계_하위_요청_필드들(
                List.of(
                        집계_하위_요청.builder()
                                .하위_필드명_별칭("assignees")
                                .하위_필드명("assignee.assignee_accountId.keyword")
                                .크기(지라이슈_제품_및_제품버전_집계_요청.get크기())
                                .결과_갯수_기준_오름차순(false)
                                .build(),
                        집계_하위_요청.builder()
                                .하위_필드명_별칭("displayNames")
                                .하위_필드명("assignee.assignee_displayName.keyword")
                                .크기(지라이슈_제품_및_제품버전_집계_요청.get크기())
                                .build(),
                        집계_하위_요청.builder()
                                .하위_필드명_별칭("cReqLink")
                                .하위_필드명("cReqLink")
                                .크기(지라이슈_제품_및_제품버전_집계_요청.get크기())
                                .build()
                )
        );


        if(요구사항여부){
            하위_집계_요청.set메인그룹필드("key");
        }else{
            하위_집계_요청.set메인그룹필드("parentReqKey");
        }
        버킷_집계_결과_목록_합계 _버킷_집계_결과_목록_합계 = 지라이슈_저장소.버킷집계(중첩_집계_쿼리_생성기.포괄(하위_집계_요청, esQuery).생성());

        return _버킷_집계_결과_목록_합계.get검색결과().get("group_by_"+하위_집계_요청.get메인그룹필드());
    }
}
