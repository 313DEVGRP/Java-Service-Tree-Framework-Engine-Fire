package com.arms.api.engine.common.component;


import com.arms.api.index_entity.지라이슈;
import com.arms.api.engine.jiraissue.repository.지라이슈_저장소;
import com.arms.elasticsearch.query.builder.검색_쿼리_빌더;
import com.arms.elasticsearch.검색조건;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class 서브테스크_조회 {

    private 지라이슈_저장소 지라이슈저장소;

    public List<지라이슈> 요구사항_링크드이슈_서브테스크_검색하기(Long 서버_아이디, String 이슈_키, int 페이지_번호, int 페이지_사이즈) {
        List<String> 검색_필드 = new ArrayList<>();
        검색_필드.add("parentReqKey");

        검색조건 검색조건 = new 검색조건();
        검색조건.setFields(검색_필드);
        검색조건.setOrder(SortOrder.ASC);
        검색조건.setSearchTerm(이슈_키);
        검색조건.setPage(페이지_번호);
        검색조건.setSize(페이지_사이즈);

        Query query = 검색_쿼리_빌더.buildSearchQuery(검색조건,서버_아이디).build();

        return 지라이슈저장소.normalSearch(query);
    }

}
