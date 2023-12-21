package com.arms.elasticsearch.helper;

import com.arms.api.engine.models.지라이슈;
import com.arms.api.engine.repositories.지라이슈_저장소;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class 인덱스_유틸 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private final ElasticsearchOperations 엘라스틱서치_작업;

    @Autowired
    private 지라이슈_저장소 지라이슈저장소;

    public 인덱스_유틸(ElasticsearchOperations elasticsearchOperations) {
        this.엘라스틱서치_작업 = elasticsearchOperations;
    }

    public boolean 인덱스확인_및_생성_매핑(String 인덱스명) {
        IndexOperations 인덱스작업 = 엘라스틱서치_작업.indexOps(IndexCoordinates.of(인덱스명));
        boolean 인덱스확인 = 인덱스_존재_확인(인덱스명);

        if (인덱스확인) {
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
        로그.info("인덱스확인_및_생성_매핑 ::: " + 인덱스명);

        return 매핑확인;
    }

    public boolean 인덱스_존재_확인(String 인덱스명) {
        IndexOperations 인덱스작업 = 엘라스틱서치_작업.indexOps(IndexCoordinates.of(인덱스명));
        return 인덱스작업.exists();
    }

    private boolean 인덱스_백업_생성(String 백업_지라이슈인덱스, Class<?> clazz) {
        IndexOperations 백업_인덱스작업 = 엘라스틱서치_작업.indexOps(IndexCoordinates.of(백업_지라이슈인덱스));
        Document 매핑정보 = 백업_인덱스작업.createMapping(clazz);
        백업_인덱스작업.create();
        백업_인덱스작업.putMapping(매핑정보);

        return 백업_인덱스작업.exists();
    }

    public boolean 인덱스삭제(String 삭제할_지라이슈인덱스) {
        boolean 삭제결과 = false;

        IndexOperations 인덱스작업 = 엘라스틱서치_작업.indexOps(IndexCoordinates.of(삭제할_지라이슈인덱스));

        try {
            if (인덱스작업.exists()) {
                삭제결과 = 인덱스작업.delete();
            }
        }
        catch(Exception e) {
            로그.error(e.getMessage());
        }

        return 삭제결과;
    }

    public boolean 리인덱스(String 현재_지라이슈인덱스, String 백업_지라이슈인덱스) {

        if (!인덱스_존재_확인(백업_지라이슈인덱스)) {
            if (!인덱스_존재_확인(현재_지라이슈인덱스)) {
                로그.info("현재 인덱스 정보가 없습니다.");
                return true;
            }

            if (!인덱스_백업_생성(백업_지라이슈인덱스, 지라이슈.class)) {
                return false;
            }
        } else {
            로그.info("백업 인덱스 정보가 있습니다.");
            return true;
        }

        boolean 결과 = false;

        int 페이지 = 0;
        int 페이지크기 = 1000;

        while (true) {
            NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.matchAllQuery())
                    .withPageable(PageRequest.of(페이지, 페이지크기));

            List<지라이슈> 지라이슈목록 = 지라이슈저장소.normalSearch(searchQuery.build());
            // SearchHits<지라이슈> searchHits = 엘라스틱서치_작업.search(searchQuery, 지라이슈.class, IndexCoordinates.of(현재_지라이슈인덱스));

            if (지라이슈목록.isEmpty()) {
                break;
            }

            List<IndexQuery> indexQueries = new ArrayList<>();

            for (지라이슈 이슈 : 지라이슈목록) {
                IndexQuery indexQuery = new IndexQueryBuilder()
                        .withObject(이슈)
                        .build();

                indexQueries.add(indexQuery);
            }

            List<IndexedObjectInformation> indexedObjectInformations = 엘라스틱서치_작업.bulkIndex(indexQueries, IndexCoordinates.of(백업_지라이슈인덱스));
            if (지라이슈목록.size() == indexedObjectInformations.size()) {
                결과 = true;
            }

            페이지++;
        }

        return 결과;
    }

}
