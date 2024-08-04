package com.arms.api.alm.issue.base.service.jiraissue_schedule.subtask_repository;


import com.arms.api.alm.issue.base.model.dto.지라이슈_엔티티;
import com.arms.api.alm.issue.base.model.vo.지라이슈_벌크_추가_요청;
import com.arms.api.alm.issue.base.repository.지라이슈_저장소;
import com.arms.egovframework.javaservice.esframework.EsQuery;
import com.arms.egovframework.javaservice.esframework.filter.ExistsQueryFilter;
import com.arms.egovframework.javaservice.esframework.model.dto.기본_검색_요청;
import com.arms.egovframework.javaservice.esframework.esquery.EsQueryBuilder;
import com.arms.egovframework.javaservice.esframework.factory.creator.기본_쿼리_생성기;
import com.arms.egovframework.javaservice.esframework.filter.TermsQueryFilter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class 서브테스크_조회 {

    private 지라이슈_저장소 지라이슈_저장소;

    public List<지라이슈_엔티티> 요구사항_링크드이슈_서브테스크_검색하기(Long 서버_아이디, String 이슈_키) {

        EsQuery esQuery = new EsQueryBuilder()
            .bool(
                 new TermsQueryFilter("parentReqKey",이슈_키)
                ,new TermsQueryFilter("jira_server_id",서버_아이디)
            );

        return 지라이슈_저장소.normalSearch(기본_쿼리_생성기.기본검색(new 기본_검색_요청() {}, esQuery).생성());
    }

    public List<지라이슈_엔티티> 요구사항_서브테스크_검색하기(지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값) {
        return this.요구사항_서브테스크_검색하기(지라이슈_벌크_추가_요청값.get지라서버_아이디(),지라이슈_벌크_추가_요청값.get이슈_키());
    }

    public List<지라이슈_엔티티> 요구사항_서브테스크_검색하기(Long 서버_아이디, String 이슈_키) {

        EsQuery esQuery = new EsQueryBuilder()
                .bool(
                        new TermsQueryFilter("parentReqKey",이슈_키)
                        ,new TermsQueryFilter("jira_server_id",서버_아이디)
                        ,new ExistsQueryFilter("upperKey")
                );

        return 지라이슈_저장소.normalSearch(기본_쿼리_생성기.기본검색(new 기본_검색_요청() {}, esQuery).생성());
    }

    public List<지라이슈_엔티티> 요구사항_연결이슈_검색하기 (지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값) {


        EsQuery 요구사항조회_쿼리 = new EsQueryBuilder()
                .bool(
                        new TermsQueryFilter("id",지라이슈_벌크_추가_요청값.조회조건_아이디())
                );
        지라이슈_엔티티 요구사항= 지라이슈_저장소.normalSearch(기본_쿼리_생성기.기본검색(new 기본_검색_요청(){}, 요구사항조회_쿼리).생성()).stream()
                .findFirst().orElseGet(지라이슈_엔티티::new);

        String[] 연결이슈_목록 = 요구사항.getLinkedIssues();
        List<String> linkedIssues;

        if (연결이슈_목록 == null) {
            linkedIssues = new ArrayList<>();
        } else {
            linkedIssues = List.of(연결이슈_목록); // 배열을 리스트로 변환
        }
        EsQuery 연결이슈조회_쿼리 = new EsQueryBuilder()
                .bool(
                        new TermsQueryFilter("id", linkedIssues)
                );

        return 지라이슈_저장소.normalSearch(기본_쿼리_생성기.기본검색(new 기본_검색_요청() {}, 연결이슈조회_쿼리).생성());
    }


    public List<SearchHit<지라이슈_엔티티>> 요구사항_링크드이슈_서브테스크_인덱스포함_검색하기(Long 서버_아이디, String 이슈_키) {

        EsQuery esQuery = new EsQueryBuilder()
                .bool(
                        new TermsQueryFilter("parentReqKey",이슈_키)
                        ,new TermsQueryFilter("jira_server_id",서버_아이디)
                );

        return 지라이슈_저장소.normalSearchHits(기본_쿼리_생성기.기본검색(new 기본_검색_요청() {}, esQuery).생성());
    }


}
