package com.arms.elasticsearch.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Component;

@Component
public class 인덱스생성_매핑 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private final ElasticsearchOperations 엘라스틱서치_작업;

    public 인덱스생성_매핑(ElasticsearchOperations elasticsearchOperations) {
        this.엘라스틱서치_작업 = elasticsearchOperations;
    }

    public boolean 인덱스확인_및_생성_매핑(Class<?> clazz) {
        IndexOperations 인덱스작업 = 엘라스틱서치_작업.indexOps(clazz);

        if (인덱스작업.exists()) {
            return true;
        }

        boolean 생성확인 = 인덱스작업.create();
        if (!생성확인) {
            throw new IllegalStateException("인덱스 생성에 실패하였습니다.");
        }

        boolean 매핑확인 = 인덱스작업.putMapping(인덱스작업.createMapping());

        if (!매핑확인) {
            throw new IllegalStateException("인덱스 매핑 설정에 실패하였습니다.");
        }

        인덱스작업.refresh();
        로그.info("Created index: " + clazz.getSimpleName().toLowerCase());

        return 매핑확인;
    }
}
