package com.arms.elasticsearch.services;

import com.arms.api.engine.model.dto.일자별_요구사항_연결된이슈_생성개수_및_상태데이터;
import com.arms.api.index_entity.이슈_인덱스;
import com.arms.api.engine.common.constrant.index.인덱스자료;
import com.arms.api.engine.jiraissue.repository.지라이슈_저장소;
import com.arms.elasticsearch.버킷_집계_결과;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.index.AliasAction;
import org.springframework.data.elasticsearch.core.index.AliasActionParameters;
import org.springframework.data.elasticsearch.core.index.AliasActions;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ActiveProfiles;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("dev")
public class 인덱스Test {

    @Autowired
    ElasticsearchOperations 엘라스틱서치_작업;

    @Autowired
    지라이슈_저장소 지라이슈저장소;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void 인덱스백업Test() {
        boolean 결과 = 인덱스클래스로_인덱스백업(이슈_인덱스.class);
        assertTrue(결과);
    }

    public boolean 인덱스클래스로_인덱스백업(Class<?> clazz) {
        String 현재_지라이슈인덱스 = 인덱스자료.이슈_인덱스명;
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        currentDate = "-2024-01-04";
        String 백업_지라이슈인덱스 = 현재_지라이슈인덱스 + currentDate;

        if (!인덱스_존재_확인(백업_지라이슈인덱스)) {
            if (!인덱스_존재_확인(현재_지라이슈인덱스)) {
                System.out.println("현재 인덱스 정보가 없습니다.");
                return true;
            }

            if (!인덱스_백업_생성(백업_지라이슈인덱스, clazz)) {
                return false;
            }

            if(리인덱스(현재_지라이슈인덱스, 백업_지라이슈인덱스, currentDate)) {
                System.out.println("인덱스 재색인을 완료하였습니다.");
                return true;
            }
        } else {
            System.out.println("백업 인덱스 정보가 있습니다.");
            return true;
        }

        return false;
    }

    @Test
    public void 인덱스삭제Test() {
        String 현재_지라이슈인덱스 = 인덱스자료.이슈_인덱스명;
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(2023-11-28));
        String 백업_지라이슈인덱스 = 현재_지라이슈인덱스 + "-2023-11-30";

        boolean 결과 = 인덱스삭제(백업_지라이슈인덱스);
        assertTrue(결과);
    }

    public boolean 인덱스삭제(String 삭제할_인덱스) {
        boolean 삭제결과 = false;
        IndexOperations 인덱스작업 = 엘라스틱서치_작업.indexOps(IndexCoordinates.of(삭제할_인덱스));

        try {
            삭제결과 = 인덱스작업.delete();
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }

        return 삭제결과;
    }

    private boolean 인덱스_존재_확인(String 인덱스명) {
        IndexOperations 인덱스작업 = 엘라스틱서치_작업.indexOps(IndexCoordinates.of(인덱스명));
        return 인덱스작업.exists();
    }

    @Test
    public void Doc백업() {
        String 현재_지라이슈인덱스 = 인덱스자료.이슈_인덱스명;
        String 백업_지라이슈인덱스 = 현재_지라이슈인덱스 + "_backup";

        if (!인덱스_존재_확인(백업_지라이슈인덱스)) {
            if (!인덱스_존재_확인(현재_지라이슈인덱스)) {
                System.out.println("현재 인덱스 정보가 없습니다.");
            }

            if (!인덱스_백업_생성(백업_지라이슈인덱스, 이슈_인덱스.class)) {
            } else {
                System.out.println("백업 인덱스 생성 성공");
            }
        }

        backupIndex(백업_지라이슈인덱스);
    }

    public void backupIndex (String backupIndex) {
        int 페이지 = 0;
        int 페이지크기 = 1000;

        ZonedDateTime kstTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ZonedDateTime utcTime = kstTime.withZoneSameInstant(ZoneOffset.UTC);
        Instant instant = utcTime.toInstant();
        Date 새로운_타임스탬프 = Date.from(instant);
        String 오늘날짜 = utcTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        while (true) {
            MatchAllQueryBuilder 전체조회 = QueryBuilders.matchAllQuery();

            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                    .withQuery(전체조회)
                    .withPageable(PageRequest.of(페이지, 페이지크기));

            List<이슈_인덱스> 페이징결과 = 지라이슈저장소.normalSearch(nativeSearchQueryBuilder.build());

            if (페이징결과 == null || 페이징결과.isEmpty()) {
                break;
            }

            List<IndexQuery> indexQueries = 페이징결과.stream()
                    .map(entity -> {
                        // 기존 ID와 타임스탬프를 합쳐 고유한 문서 ID 생성
                        String docId = entity.getId() + "_" + 오늘날짜;
                        entity.setTimestamp(새로운_타임스탬프);

                        // 새로운 문서 추가
                        return new IndexQueryBuilder()
                                .withId(docId)
                                .withObject(entity)
                                .build();
                    })
                    .collect(Collectors.toList());

            엘라스틱서치_작업.bulkIndex(indexQueries, IndexCoordinates.of(backupIndex));

//            List<지라이슈> 저장할이슈 = 페이징결과.stream()
//                    .map(지라이슈 -> {
//                        지라이슈 새지라이슈 = 지라이슈;
//                        새지라이슈.setTimestamp(새로운_타임스탬프);
//                        새지라이슈.setId(지라이슈.getId() + "_" + 오늘날짜);
//
//                        return 새지라이슈;
//                    })
//                    .collect(Collectors.toList());
//
//            대량이슈_추가하기(저장할이슈, backupIndex);

            페이지++;
        }
    }

    public int 대량이슈_추가하기(List<이슈_인덱스> 대량이슈_리스트, String 백업인덱스) {

        List<IndexQuery> 검색엔진_쿼리 = 대량이슈_리스트.stream()
                .map(지라이슈 -> new IndexQueryBuilder().withId(String.valueOf(지라이슈.getId()))
                        .withObject(지라이슈).build())
                .collect(toList());
        엘라스틱서치_작업.bulkIndex(검색엔진_쿼리, IndexCoordinates.of(백업인덱스));

        return 검색엔진_쿼리.size();
    }

    @Test
    public void backupIndex를또백업() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        Date 새로운_타임스탬프 = calendar.getTime();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        LocalDate endDate = LocalDate.of(2023, 11, 30); // 종료 날짜 설정
        ZonedDateTime kstTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ZonedDateTime utcTime = kstTime.withZoneSameInstant(ZoneOffset.UTC);
        LocalDate currentDate = utcTime.toLocalDate();
        // String 오늘날짜 = utcTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String backupIndexName = 인덱스자료.이슈_인덱스명 +"_backup";

        while (!currentDate.isBefore(endDate)) { // 현재 날짜가 종료 날짜보다 이전이 아닐 때까지 반복
            // 필요한 로직을 실행합니다.
            System.out.println(currentDate); // 예시로 현재 날짜를 출력합니다.

            Date date = convertToDateViaInstant(currentDate);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String formattedDate = sdf.format(date);

            String[] arr = formattedDate.split("T");

            System.out.println(arr[0]); // 예시로 현재 날짜를 출력합니다.
            System.out.println(formattedDate); // 예시로 현재 날짜를 출력합니다.

            String originIndexName = 인덱스자료.이슈_인덱스명 +"-"+arr[0];

            if (인덱스_존재_확인(originIndexName)) {
                if(!인덱스_존재_확인(backupIndexName)) {
                    인덱스_백업_생성(backupIndexName, 이슈_인덱스.class);
                }

                backupIndexQuery(originIndexName, backupIndexName, arr[0], formattedDate);
            }
            else {
            }

            currentDate = currentDate.minusDays(1); // 현재 날짜를 하루 전으로 변경합니다.

        }
    }

    public void backupIndexQuery(String originalIndex, String backupIndex, String 날짜, String date) {
        int page = 0;
        int pageSize = 1000; // 한 번에 처리할 문서의 수

        while (true) {
            NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.matchAllQuery())
                    .withPageable(PageRequest.of(page, pageSize))
                    .build();

            SearchHits<Map> searchHits = 엘라스틱서치_작업.search(searchQuery, Map.class, IndexCoordinates.of(originalIndex));

            if (searchHits.isEmpty()) {
                break;
            }

            List<IndexQuery> indexQueries = new ArrayList<>();

            for (SearchHit<Map> hit : searchHits) {
                String docId = hit.getId() + "_" + 날짜;

                hit.getContent().put("@timestamp", date);

                // 새로운 문서 추가
                IndexQuery indexQuery = new IndexQueryBuilder()
                        .withId(docId)
                        .withObject(hit.getContent())
                        .build();

                indexQueries.add(indexQuery);
            }

            엘라스틱서치_작업.bulkIndex(indexQueries, IndexCoordinates.of(backupIndex));

            page++;
        }
    }

    public Date convertToDateViaInstant(LocalDate dateToConvert) {
        return java.util.Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    private String transformDate(String date) {
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(date);
        return DateTimeFormatter
                .ofPattern("yyyy-MM-dd")
                .format(offsetDateTime);
    }

    private 일자별_요구사항_연결된이슈_생성개수_및_상태데이터 일별_생성개수_및_상태_데이터생성(버킷_집계_결과 결과) {
        Map<String, Long> 요구사항여부결과 = new HashMap<>();
        Map<String, Map<String, Long>> 상태목록결과 = new HashMap<>();

        결과.get하위검색결과().get("요구사항여부").forEach(term -> {
            String 필드명 = term.get필드명();
            Long 개수 = term.get개수();

            요구사항여부결과.put(필드명, 개수);

            List<버킷_집계_결과> 키목록 = Optional.ofNullable(term.get하위검색결과().get("key")).orElse(Collections.emptyList());

            Map<String, Long> status = 키목록.stream()
                    .collect(Collectors.toMap(버킷_집계_결과::get필드명, 버킷_집계_결과::get개수, Long::sum));

            if(status != null) {
                상태목록결과.put(필드명, status);
            }
        });

        long 요구사항_개수 = 요구사항여부결과.getOrDefault("true", 0L);
        long 연결된이슈_개수 = 요구사항여부결과.getOrDefault("false", 0L);

        Map<String, Long> 요구사항_상태목록 = 상태목록결과.getOrDefault("true", null);
        Map<String, Long> 연결된이슈_상태목록 = 상태목록결과.getOrDefault("false", null);

        return new 일자별_요구사항_연결된이슈_생성개수_및_상태데이터(요구사항_개수, 요구사항_상태목록, 연결된이슈_개수, 연결된이슈_상태목록);
    }

    public boolean 리인덱스(String 현재_지라이슈인덱스, String 백업_지라이슈인덱스, String 백업날짜) {
        boolean 결과 = false;

        if (!인덱스_존재_확인(백업_지라이슈인덱스)) {
            if (!인덱스_존재_확인(현재_지라이슈인덱스)) {
                System.out.println("현재 인덱스 정보가 없습니다.");
            }

            if (!인덱스_백업_생성(백업_지라이슈인덱스, 이슈_인덱스.class)) {
            }
        } else {
            System.out.println("백업 인덱스 정보가 있습니다.");
        }

        int 페이지 = 0;
        int 페이지크기 = 1000;

        while (true) {
            NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.matchAllQuery())
                    .withPageable(PageRequest.of(페이지, 페이지크기));

            List<이슈_인덱스> 지라이슈목록 = 지라이슈저장소.normalSearch(searchQuery.build());
            // SearchHits<지라이슈> searchHits = 엘라스틱서치_작업.search(searchQuery, 지라이슈.class, IndexCoordinates.of(현재_지라이슈인덱스));

            if (지라이슈목록.isEmpty()) {
                break;
            }

            List<IndexQuery> indexQueries = new ArrayList<>();

            for (이슈_인덱스 이슈 : 지라이슈목록) {
                String newId = 이슈.getId() + 백업날짜;

                IndexQuery indexQuery = new IndexQueryBuilder()
                        .withId(newId)
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

    //@Test
    public void 지라이슈컨트롤러_인덱스백업_Test() {
//        boolean 결과 = 지라이슈_서비스.지라이슈_인덱스백업();
//        assertTrue(결과);
    }

    //@Test
    public void 지라이슈컨트롤러_인덱스삭제_Test() {
//        boolean 결과 = 지라이슈_서비스.지라이슈_인덱스삭제();
//        assertTrue(결과);
    }

    @Test
    void 별칭추가Test() {
        boolean 결과 = 별칭_지정하기("jiraissue-2024-01-04", "bakup_jiraissue");
        assertTrue(결과);
    }

    // 인덱스 백업 시 인덱스에 alias 지정하는 부분
    private boolean 인덱스_백업_생성(String 백업_지라이슈인덱스, Class<?> clazz) {
        IndexOperations 백업_인덱스작업 = 엘라스틱서치_작업.indexOps(IndexCoordinates.of(백업_지라이슈인덱스));
        Document 매핑정보 = 백업_인덱스작업.createMapping(clazz);
        백업_인덱스작업.create();
        백업_인덱스작업.putMapping(매핑정보);
        별칭_지정하기(백업_지라이슈인덱스, "bakup_jiraissue");

        return 백업_인덱스작업.exists();
    }

    public boolean 별칭_지정하기(String 별칭을_추가할_인덱스명, String 별칭명) {
        AliasActions aliasActions = new AliasActions();
        aliasActions.add(new AliasAction.Add(AliasActionParameters.builder()
                .withIndices(별칭을_추가할_인덱스명)
                .withAliases(별칭명)
                .build()));

        boolean 결과 = false;
        try {
            결과 = 엘라스틱서치_작업.indexOps(IndexCoordinates.of(별칭을_추가할_인덱스명)).alias(aliasActions);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return 결과;
    }

}
