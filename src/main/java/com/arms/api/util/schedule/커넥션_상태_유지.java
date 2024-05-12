package com.arms.api.util.schedule;

import com.arms.api.alm.fluentd.repository.플루언트디_저장소;
import com.arms.elasticsearch.query.EsQuery;
import com.arms.elasticsearch.query.base.일반_검색_요청;
import com.arms.elasticsearch.query.esquery.EsQueryBuilder;
import com.arms.elasticsearch.query.factory.creator.old.일반_검색_쿼리_생성기;
import com.arms.elasticsearch.query.filter.TermsQueryFilter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class 커넥션_상태_유지 {

    private final 플루언트디_저장소 프플루언트디_저장소;

    @Scheduled(fixedDelay = 45000, initialDelay = 10000)
    public void 커넥션_상태_유지(){
        log.info("엘라스틱서치 커넥션 상태 유지");
        EsQuery esQuery = new EsQueryBuilder()
            .bool(
                new TermsQueryFilter("id", "313")
            );
        일반_검색_요청 일반_검색_요청 = new 일반_검색_요청() {
        };
        일반_검색_요청.set페이지_처리_여부(false);
        일반_검색_요청.set크기(1);
        프플루언트디_저장소.normalSearch(일반_검색_쿼리_생성기.of(일반_검색_요청,esQuery).생성());
    }
}
