package com.arms.api.alm.issue.base.service;

import com.arms.api.alm.issue.base.model.dto.지라이슈_엔티티;
import com.arms.api.alm.issue.base.repository.지라이슈_저장소;
import com.arms.egovframework.javaservice.esframework.EsQuery;
import com.arms.egovframework.javaservice.esframework.esquery.EsQueryBuilder;
import com.arms.egovframework.javaservice.esframework.factory.creator.기본_쿼리_생성기;
import com.arms.egovframework.javaservice.esframework.model.dto.기본_검색_요청;
import com.arms.egovframework.javaservice.esframework.must.TermQueryMust;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("dev")
@SpringBootTest
class 이슈_스케쥴_서비스_프로세스Test {

    @Autowired
    private com.arms.api.alm.issue.base.repository.지라이슈_저장소 지라이슈_저장소;

    @Test
    public void test(){
        EsQuery esQuery = new EsQueryBuilder()
                .bool(
                        new TermQueryMust("id", "0123213")
                );

        지라이슈_엔티티 지라이슈_엔티티 = 지라이슈_저장소.normalSearch(기본_쿼리_생성기.기본검색(new 기본_검색_요청(){}, esQuery).생성())
                .stream()
                .findFirst().orElseGet(지라이슈_엔티티::new);

        System.out.println(지라이슈_엔티티);
    }

}