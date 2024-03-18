package com.arms.elasticsearch.repository;

import com.arms.api.jira.utils.지라유틸;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

@ActiveProfiles("dev")
@SpringBootTest
public class 공통저장소_구현체Test {


    @Test
    public void test(){
        LocalDateTime localDateTime1 = LocalDateTime.of(2024, 2, 13, 12, 31);
        LocalDateTime localDateTime = 지라유틸.roundToNearest30Minutes(localDateTime1);
        System.out.println(localDateTime);
    }

}