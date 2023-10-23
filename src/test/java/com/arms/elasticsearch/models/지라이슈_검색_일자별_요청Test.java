package com.arms.elasticsearch.models;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("dev")
class 지라이슈_검색_일자별_요청Test {

    @Test
    public void test(){
        지라이슈_검색_일자별_요청 x = new 지라이슈_검색_일자별_요청();
        x.setSize(100);
        x.set그룹필드들(List.of("a","b"));
        x.set시간그룹필드("updated");
        NativeSearchQuery 생성 = x.생성();
        System.out.println(생성);
    }
}