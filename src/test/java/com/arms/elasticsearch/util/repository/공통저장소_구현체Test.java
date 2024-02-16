package com.arms.elasticsearch.util.repository;

import com.arms.utils.지라유틸;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

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