package com.arms.api.engine.services;

import static java.util.stream.Collectors.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.arms.api.engine.models.지라이슈_일반_집계_요청;
import com.arms.api.engine.vo.제품_서비스_버전;
import com.arms.api.engine.vo.하위_이슈_사항;
import com.arms.api.engine.vo.하위_이슈_사항들;
import com.arms.elasticsearch.util.query.EsQuery;
import com.arms.elasticsearch.util.query.EsQueryBuilder;
import com.arms.elasticsearch.util.query.bool.TermQueryMust;
import com.arms.elasticsearch.util.query.bool.TermsQueryFilter;
import com.arms.elasticsearch.util.query.일반_집계_요청;
import com.arms.elasticsearch.util.검색결과;
import com.arms.elasticsearch.util.검색결과_목록_메인;

@SpringBootTest
@ActiveProfiles("dev")
public class 지라이슈_검색엔진_대시보드Test {

    @Autowired
    지라이슈_대시보드_서비스 지라이슈_대시보드_서비스;



    @Test
    public void test(){
        검색결과_목록_메인 요구사항 = 요구사항();
        검색결과_목록_메인 하위이슈 = 하위이슈();

        List<검색결과> pdServiceVersions = 요구사항.get검색결과().get("group_by_pdServiceVersion");
        List<검색결과> parentReqKeys = 하위이슈.get검색결과().get("group_by_parentReqKey");

        List<하위_이슈_사항> 하위_이슈_사항들 = parentReqKeys.stream()
                .map(issue -> new 하위_이슈_사항(issue)).collect(toList());

        List<제품_서비스_버전> 제품_서비스_버전들 = pdServiceVersions.stream()
                .map(request -> new 제품_서비스_버전(request,new 하위_이슈_사항들(하위_이슈_사항들))).collect(toList());

        System.out.println(제품_서비스_버전들);

    }

    private 검색결과_목록_메인 요구사항(){
        지라이슈_일반_집계_요청 지라이슈_일반_집계_요청 = new 지라이슈_일반_집계_요청();
        지라이슈_일반_집계_요청.set메인그룹필드("pdServiceVersion");
        지라이슈_일반_집계_요청.set크기(10000);
        지라이슈_일반_집계_요청.set컨텐츠보기여부(false);
        지라이슈_일반_집계_요청.set하위그룹필드들(List.of("key","assignee.assignee_emailAddress.keyword"));

        EsQuery esQuery
                = new EsQueryBuilder()
                .bool( new TermsQueryFilter("pdServiceVersion",List.of(16,17)),
                        new TermQueryMust("pdServiceId",13L),
                        new TermQueryMust("isReq",true)
                );

        return 지라이슈_대시보드_서비스.집계결과_가져오기(일반_집계_요청.of(지라이슈_일반_집계_요청, esQuery));
    }

    private 검색결과_목록_메인 하위이슈(){
        지라이슈_일반_집계_요청 지라이슈_일반_집계_요청 = new 지라이슈_일반_집계_요청();
        지라이슈_일반_집계_요청.set메인그룹필드("parentReqKey");
        지라이슈_일반_집계_요청.set크기(10000);
        지라이슈_일반_집계_요청.set컨텐츠보기여부(false);
        지라이슈_일반_집계_요청.set하위그룹필드들(List.of("assignee.assignee_emailAddress.keyword"));

        EsQuery esQuery
                = new EsQueryBuilder()
                .bool( new TermsQueryFilter("pdServiceVersion",List.of(16,17)),
                        new TermQueryMust("pdServiceId",13L),
                        new TermQueryMust("isReq",false)
                );

        return 지라이슈_대시보드_서비스.집계결과_가져오기(일반_집계_요청.of(지라이슈_일반_집계_요청, esQuery));
    }


}
