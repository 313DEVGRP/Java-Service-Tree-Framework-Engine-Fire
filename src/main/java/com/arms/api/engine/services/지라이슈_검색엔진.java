package com.arms.api.engine.services;

import com.arms.elasticsearch.helper.인덱스생성_매핑;
import com.arms.api.engine.models.지라이슈;
import com.arms.api.engine.repositories.지라이슈_저장소;
import com.arms.elasticsearch.util.*;
import com.arms.errors.codes.에러코드;
import com.arms.api.jira.jiraissue.model.지라이슈_데이터;
import com.arms.api.jira.jiraissue.model.지라이슈필드_데이터;
import com.arms.api.jira.jiraissue.model.지라프로젝트_데이터;
import com.arms.api.jira.jiraissue.service.지라이슈_전략_호출;
import com.arms.api.jira.jiraissuestatus.model.지라이슈상태_데이터;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service("지라이슈_서비스")
@AllArgsConstructor
public class 지라이슈_검색엔진 implements 지라이슈_서비스{

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private 지라이슈_저장소 지라이슈저장소;

    private 지라이슈_전략_호출 지라이슈_전략_호출;

    private 인덱스생성_매핑 인덱스생성_매핑;

    @Override
    public 지라이슈 이슈_추가하기(지라이슈 지라이슈) {

        지라이슈 결과 = 지라이슈저장소.save(지라이슈);
        return 결과;
    }

    @Override
    public int 대량이슈_추가하기(List<지라이슈> 대량이슈_리스트) {

        List<IndexQuery> 검색엔진_쿼리 = 대량이슈_리스트.stream()
                .map(지라이슈 -> new IndexQueryBuilder().withId(String.valueOf(지라이슈.getId()))
                        .withObject(지라이슈).build())
                .collect(toList());
        지라이슈저장소.bulkIndex(검색엔진_쿼리);

        return 검색엔진_쿼리.size();
    }

    @Override
    public Iterable<지라이슈> 이슈리스트_추가하기(List<지라이슈> 지라이슈_리스트) {

        Iterable<지라이슈> 결과 = 지라이슈저장소.saveAll(지라이슈_리스트);
        return 결과;
    }

    @Override
    public 지라이슈 이슈_갱신하기(지라이슈 지라_이슈) {

        지라이슈 이슈 = 이슈_조회하기(지라_이슈.getId());

        지라이슈 결과 = 지라이슈저장소.save(이슈);
        return 결과;
    }

    @Override
    public 지라이슈 이슈_삭제하기(지라이슈 지라_이슈) {

        지라이슈 이슈 = 이슈_조회하기(지라_이슈.getId());
        log.info("왠만하면 쓰지 마시지...");

        if ( 이슈 == null ) {
            return null;
        }else{
            지라이슈저장소.delete(이슈);
            return 이슈;
        }
    }

    @Override
    public 지라이슈 이슈_조회하기(String 조회조건_아이디) {
        // 지라이슈저장소.test();
        return 지라이슈저장소.findById(조회조건_아이디).orElse(null);
    }

    @Override
    public List<지라이슈> 이슈_검색하기(검색조건 검색조건) {
        Query query
            = 검색엔진_유틸.buildSearchQuery(검색조건).build();
        return 지라이슈저장소.normalSearch(query);
    }

    @Override
    public 검색결과_목록_메인 특정필드의_값들을_그룹화하여_빈도수가져오기(String indexName, String groupByField) throws IOException {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder
            .withQuery(QueryBuilders.matchAllQuery())
            .withAggregations(
                AggregationBuilders.terms("group_by")
                        .field(groupByField)
                        .size(100)
            );

        return 지라이슈저장소.aggregationSearch(nativeSearchQueryBuilder.build());
    }

    @Override
    public 검색결과_목록_메인 특정필드_검색후_다른필드_그룹결과(String 인덱스이름, String 특정필드, String 특정필드검색어, String 그룹할필드) throws IOException {

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        BoolQueryBuilder boolQuery
                = QueryBuilders.boolQuery()
                .must(QueryBuilders.existsQuery(특정필드))
                .filter(QueryBuilders.termQuery(특정필드, 특정필드검색어));

        nativeSearchQueryBuilder.withQuery(boolQuery)
                .withAggregations( AggregationBuilders.terms("group_by_" + 특정필드)
                        .field(그룹할필드)
                        .size(1000));
        return 지라이슈저장소.aggregationSearch(nativeSearchQueryBuilder.build());
    }

    @Override
    public 지라이슈 이슈_검색엔진_저장(Long 지라서버_아이디, String 이슈_키, Long 제품서비스_아이디, Long 제품서비스_버전) throws Exception {

        if (지라서버_아이디 == null) {
            로그.error("이슈_검색엔진_저장 Error: 서버_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("이슈_검색엔진_저장 Error: 서버_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
        }

        if (이슈_키 == null || 이슈_키.isEmpty()) {
            로그.error("이슈_검색엔진_저장 Error 이슈_키 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("이슈_검색엔진_저장 Error 이슈_키 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        if (제품서비스_아이디 == null || 제품서비스_버전 == null) {
            로그.error("이슈_검색엔진_저장 Error 제품서비스_아이디 또는 제품서비스_버전 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("이슈_검색엔진_저장 Error 제품서비스_아이디 또는 제품서비스_버전 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        지라이슈_데이터 반환된_이슈 = Optional.ofNullable(지라이슈_전략_호출.이슈_상세정보_가져오기(지라서버_아이디, 이슈_키))
                                        .orElse(null);

        if (반환된_이슈 == null) {
            로그.error("이슈_검색엔진_저장 Error 이슈 키에 해당하는 데이터가 없음" + 에러코드.이슈_조회_오류.getErrorMsg());
            throw new IllegalArgumentException("이슈_검색엔진_저장 Error 이슈 키에 해당하는 데이터가 없음" + 에러코드.이슈_조회_오류.getErrorMsg());
        }

        지라이슈 저장할_지라이슈 = ELK_데이터로_변환(지라서버_아이디, 반환된_이슈, true,
                                        "", 제품서비스_아이디, 제품서비스_버전);

        return 이슈_추가하기(저장할_지라이슈);
    }

    @Override
    public int 이슈_링크드이슈_서브테스크_벌크로_추가하기(Long 지라서버_아이디, String 이슈_키 , Long 제품서비스_아이디, Long 제품서비스_버전) throws Exception {


        boolean 인덱스확인 = 인덱스생성_매핑.인덱스확인_및_생성_매핑(지라이슈.class);

        if (!인덱스확인) {
            throw new IllegalArgumentException(에러코드.지라이슈_인덱스_NULL_오류.getErrorMsg());
        }

        if (지라서버_아이디 == null) {
            로그.error("이슈_링크드이슈_서브테스크_벌크로_추가하기 Error: 서버_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("이슈_링크드이슈_서브테스크_벌크로_추가하기 Error: 서버_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
        }

        if (이슈_키 == null || 이슈_키.isEmpty()) {
            로그.error("이슈_링크드이슈_서브테스크_벌크로_추가하기 Error 이슈_키 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("이슈_링크드이슈_서브테스크_벌크로_추가하기 Error 이슈_키 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        if (제품서비스_아이디 == null || 제품서비스_버전 == null) {
            로그.error("이슈_링크드이슈_서브테스크_벌크로_추가하기 Error 제품서비스_아이디 또는 제품서비스_버전 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("이슈_링크드이슈_서브테스크_벌크로_추가하기 Error 제품서비스_아이디 또는 제품서비스_버전 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        List<지라이슈> 벌크_저장_목록 = new ArrayList<지라이슈>();

        지라이슈_데이터 반환된_이슈 = Optional.ofNullable(지라이슈_전략_호출.이슈_상세정보_가져오기(지라서버_아이디, 이슈_키))
                .map(이슈 -> {
                    벌크_저장_목록.add(ELK_데이터로_변환(지라서버_아이디, 이슈, true, "", 제품서비스_아이디, 제품서비스_버전));
                    return 이슈;
                }).orElse(null);

        if (반환된_이슈 == null) {

            반환된_이슈 = new 지라이슈_데이터();
            반환된_이슈.setKey(이슈_키);

            String 프로젝트_키 = 이슈_키.substring(0, 이슈_키.indexOf("-"));

            지라프로젝트_데이터 지라프로젝트_데이터 = new 지라프로젝트_데이터();
            지라프로젝트_데이터.setKey(프로젝트_키);

            지라이슈상태_데이터 지라이슈상태_데이터 = new 지라이슈상태_데이터();
            지라이슈상태_데이터.setId("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");
            지라이슈상태_데이터.setSelf("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");
            지라이슈상태_데이터.setName("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");
            지라이슈상태_데이터.setDescription("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");

            지라이슈필드_데이터 지라이슈필드_데이터 = new 지라이슈필드_데이터();

            지라이슈필드_데이터.setProject(지라프로젝트_데이터);
            지라이슈필드_데이터.setStatus(지라이슈상태_데이터);

            반환된_이슈.setFields(지라이슈필드_데이터);

            벌크_저장_목록.add(ELK_데이터로_변환(지라서버_아이디, 반환된_이슈, true, "", 제품서비스_아이디, 제품서비스_버전));

            try {
                List<지라이슈> 링크드이슈_서브테스크_목록 = Optional.ofNullable(요구사항_링크드이슈_서브테스크_검색하기(지라서버_아이디,
                                                                                        이슈_키, 0, 0))
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(링크드이슈_서브테스크 -> {
                            if (링크드이슈_서브테스크.getStatus() != null) {
                                링크드이슈_서브테스크.getStatus().setId("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");
                                링크드이슈_서브테스크.getStatus().setName("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");
                                링크드이슈_서브테스크.getStatus().setSelf("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");
                                링크드이슈_서브테스크.getStatus().setDescription("해당 요구사항은 지라서버에서 조회가 되지 않는 상태입니다.");
                            }
                            return 링크드이슈_서브테스크;
                        })
                        .collect(toList());

                벌크_저장_목록.addAll(링크드이슈_서브테스크_목록);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            List<지라이슈_데이터> 이슈링크_또는_서브테스크_목록 = new ArrayList<지라이슈_데이터>();

            Optional.ofNullable(지라이슈_전략_호출.이슈링크_가져오기(지라서버_아이디, 이슈_키))
                    .ifPresent(이슈링크_목록 -> 이슈링크_또는_서브테스크_목록.addAll(이슈링크_목록));

            Optional.ofNullable(지라이슈_전략_호출.서브테스크_가져오기(지라서버_아이디, 이슈_키))
                    .ifPresent(서브테스크_목록 -> 이슈링크_또는_서브테스크_목록.addAll(서브테스크_목록));

            if (이슈링크_또는_서브테스크_목록 != null && 이슈링크_또는_서브테스크_목록.size() >= 1) {
                List<지라이슈> 변환된_이슈_목록 = 이슈링크_또는_서브테스크_목록.stream().map(이슈링크또는서브테스크 -> {
                            지라이슈 변환된_이슈 = ELK_데이터로_변환(지라서버_아이디, 이슈링크또는서브테스크,
                                    false, 이슈_키, 제품서비스_아이디, 제품서비스_버전);
                            벌크_저장_목록.add(변환된_이슈);
                            return 변환된_이슈;
                        })
                        .filter(Objects::nonNull)
                        .collect(toList());
            }
        }

        return 대량이슈_추가하기(벌크_저장_목록);
    }

    private 지라이슈 ELK_데이터로_변환(Long 지라서버_아이디, 지라이슈_데이터 지라이슈_데이터,
                                 boolean 요구사항유형_여부, String 부모_요구사항_키,
                                 Long 제품서비스_아이디, Long 제품서비스_버전) {

        지라이슈.프로젝트 프로젝트 = Optional.ofNullable(지라이슈_데이터.getFields().getProject())
                .map(project -> 지라이슈.프로젝트.builder()
                        .id(Optional.ofNullable(project.getId()).orElse(null))
                        .key(Optional.ofNullable(project.getKey()).orElse(null))
                        .name(Optional.ofNullable(project.getName()).orElse(null))
                        .self(Optional.ofNullable(project.getSelf()).orElse(null))
                        .build())
                .orElse(null);

        지라이슈.이슈유형 이슈유형 = Optional.ofNullable(지라이슈_데이터.getFields().getIssuetype())
                .map(issuetype -> 지라이슈.이슈유형.builder()
                        .self(Optional.ofNullable(issuetype.getSelf()).orElse(null))
                        .id(Optional.ofNullable(issuetype.getId()).orElse(null))
                        .description(Optional.ofNullable(issuetype.getDescription()).orElse(null))
                        .name(Optional.ofNullable(issuetype.getName()).orElse(null))
                        .subtask(Optional.ofNullable(issuetype.getSubtask()).orElse(null))
                        .untranslatedName(Optional.ofNullable(issuetype.getUntranslatedName()).orElse(null))
                        .hierarchyLevel(Optional.ofNullable(issuetype.getHierarchyLevel()).orElse(null))
                        .build())
                .orElse(null);

        지라이슈.생성자 생성자 = Optional.ofNullable(지라이슈_데이터.getFields().getCreator())
                .map(creator -> 지라이슈.생성자.builder()
                        .accountId(Optional.ofNullable(creator.getAccountId()).orElse(null))
                        .emailAddress(Optional.ofNullable(creator.getEmailAddress()).orElse(null))
                        .displayName(Optional.ofNullable(creator.getDisplayName()).orElse(null))
                        .build())
                .orElse(null);

        지라이슈.보고자 보고자 = Optional.ofNullable(지라이슈_데이터.getFields().getReporter())
                .map(reporter -> 지라이슈.보고자.builder()
                        .accountId(Optional.ofNullable(reporter.getAccountId()).orElse(null))
                        .emailAddress(Optional.ofNullable(reporter.getEmailAddress()).orElse(null))
                        .displayName(Optional.ofNullable(reporter.getDisplayName()).orElse(null))
                        .build())
                .orElse(null);

        지라이슈.담당자 담당자 = Optional.ofNullable(지라이슈_데이터.getFields().getAssignee())
                .map(assignee -> 지라이슈.담당자.builder()
                        .accountId(Optional.ofNullable(assignee.getAccountId()).orElse(null))
                        .emailAddress(Optional.ofNullable(assignee.getEmailAddress()).orElse(null))
                        .displayName(Optional.ofNullable(assignee.getDisplayName()).orElse(null))
                        .build())
                .orElse(null);

        지라이슈.우선순위 우선순위 = Optional.ofNullable(지라이슈_데이터.getFields().getPriority())
                .map(priority -> 지라이슈.우선순위.builder()
                        .self(Optional.ofNullable(priority.getSelf()).orElse(null))
                        .id(Optional.ofNullable(priority.getId()).orElse(null))
                        .name(Optional.ofNullable(priority.getName()).orElse(null))
                        .description(Optional.ofNullable(priority.getDescription()).orElse(null))
                        .isDefault(Optional.ofNullable(priority.isDefault()).orElse(false)) // 기본값을 false로 설정
                        .build())
                .orElse(null);

        지라이슈.상태 상태 = Optional.ofNullable(지라이슈_데이터.getFields().getStatus())
                .map(status -> 지라이슈.상태.builder()
                        .self(Optional.ofNullable(status.getSelf()).orElse(null))
                        .id(Optional.ofNullable(status.getId()).orElse(null))
                        .name(Optional.ofNullable(status.getName()).orElse(null))
                        .description(Optional.ofNullable(status.getDescription()).orElse(null))
                        .build())
                .orElse(null);

        지라이슈.해결책 해결책 = Optional.ofNullable(지라이슈_데이터.getFields().getResolution())
                .map(resolution -> 지라이슈.해결책.builder()
                        .self(Optional.ofNullable(resolution.getSelf()).orElse(null))
                        .id(Optional.ofNullable(resolution.getId()).orElse(null))
                        .name(Optional.ofNullable(resolution.getName()).orElse(null))
                        .description(Optional.ofNullable(resolution.getDescription()).orElse(null))
                        .isDefault(Optional.ofNullable(resolution.isDefault()).orElse(false)) // 기본값을 false로 설정
                        .build())
                .orElse(null);

        List<지라이슈.워크로그> 워크로그 = Optional.ofNullable(지라이슈_데이터.getFields().getWorklogs())
                .orElse(Collections.emptyList()) // null인 경우 빈 리스트 반환
                .stream()
                .map(워크로그아이템 -> {
                    지라이슈.저자 저자 = Optional.ofNullable(워크로그아이템.getAuthor())
                            .map(author -> new 지라이슈.저자(
                                    Optional.ofNullable(author.getAccountId()).orElse(null),
                                    Optional.ofNullable(author.getEmailAddress()).orElse(null)))
                            .orElse(null);

                    지라이슈.수정한_저자 수정한_저자 = Optional.ofNullable(워크로그아이템.getUpdateAuthor())
                            .map(updateAuthor -> new 지라이슈.수정한_저자(
                                    Optional.ofNullable(updateAuthor.getAccountId()).orElse(null),
                                    Optional.ofNullable(updateAuthor.getEmailAddress()).orElse(null)))
                            .orElse(null);

                    return new 지라이슈.워크로그(Optional.ofNullable(워크로그아이템.getSelf()).orElse(null),
                            저자,
                            수정한_저자,
                            Optional.ofNullable(워크로그아이템.getCreated()).orElse(null),
                            Optional.ofNullable(워크로그아이템.getUpdated()).orElse(null),
                            Optional.ofNullable(워크로그아이템.getStarted()).orElse(null),
                            Optional.ofNullable(워크로그아이템.getTimeSpent()).orElse(null),
                            Optional.ofNullable(워크로그아이템.getTimeSpentSeconds()).orElse(null),
                            Optional.ofNullable(워크로그아이템.getId()).orElse(null),
                            Optional.ofNullable(워크로그아이템.getIssueId()).orElse(null)
                    );
                })
                .collect(toList());

        지라이슈 이슈 = 지라이슈.builder()
                .jira_server_id(지라서버_아이디)
                .issueID(Optional.ofNullable(지라이슈_데이터.getId()).orElse(null))
                .key(Optional.ofNullable(지라이슈_데이터.getKey()).orElse(null))
                .self(Optional.ofNullable(지라이슈_데이터.getSelf()).orElse(null))
                .parentReqKey(부모_요구사항_키)
                .isReq(요구사항유형_여부)
                .project(프로젝트)
                .issuetype(이슈유형)
                .creator(생성자)
                .reporter(보고자)
                .assignee(담당자)
                .labels(Optional.ofNullable(지라이슈_데이터.getFields().getLabels()).orElse(null))
                .priority(우선순위)
                .status(상태)
                .resolution(해결책)
                .resolutiondate(Optional.ofNullable(지라이슈_데이터.getFields().getResolutiondate()).orElse(null))
                .created(Optional.ofNullable(지라이슈_데이터.getFields().getCreated()).orElse(null))
                .updated(Optional.ofNullable(지라이슈_데이터.getFields().getUpdated()).orElse(null))
                .worklogs(워크로그)
                .timespent(Optional.ofNullable(지라이슈_데이터.getFields().getTimespent()).orElse(null))
                .summary(Optional.ofNullable(지라이슈_데이터.getFields().getSummary()).orElse(null))
                .pdServiceId(제품서비스_아이디)
                .pdServiceVersion(제품서비스_버전)
                .build();

        이슈.generateId();

        return 이슈;
    }

    @Override
    public List<지라이슈> 요구사항_링크드이슈_서브테스크_검색하기(Long 서버_아이디, String 이슈_키, int 페이지_번호, int 페이지_사이즈) {
        List<String> 검색_필드 = new ArrayList<>();
        검색_필드.add("parentReqKey");

        검색조건 검색조건 = new 검색조건();
        검색조건.setFields(검색_필드);
        검색조건.setOrder(SortOrder.ASC);
        검색조건.setSearchTerm(이슈_키);
        검색조건.setPage(페이지_번호);
        검색조건.setSize(페이지_사이즈);

        Query query = 검색엔진_유틸.buildSearchQuery(검색조건,서버_아이디).build();

        return 지라이슈저장소.normalSearch(query);
    }

    @Override
    public Map<String,Integer>  요구사항_릴레이션이슈_상태값_전체통계(Long 지라서버_아이디) throws IOException {

        MatchQueryBuilder 사용자별_조회 = QueryBuilders.matchQuery("jira_server_id", 지라서버_아이디);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(사용자별_조회)
                .withAggregations(AggregationBuilders.terms("이슈_상태별_집계").field("status.status_name.keyword"));

       ;

        검색결과_목록_메인 검색결과_목록_메인 = 지라이슈저장소.aggregationSearch(nativeSearchQueryBuilder.build());
        List<검색결과> 이슈_상태별_집계 = 검색결과_목록_메인.get검색결과().get("이슈_상태별_집계");

        Map<String, Integer> 전체상태값_집계 = new HashMap<>();

        if (이슈_상태별_집계.isEmpty()) {
            전체상태값_집계.put("조회된 상태: ",0 );
        }

        for (검색결과 상태 : 이슈_상태별_집계) {
            String statusName = 상태.get필드명();
            long docCount = 상태.get개수();
            전체상태값_집계.put(statusName, (int) docCount);
        }
        return 전체상태값_집계;
    }

    @Override
    public Map<String, Map<String, Integer>> 요구사항_릴레이션이슈_상태값_프로젝트별통계(Long 지라서버_아이디) throws IOException {

        MatchQueryBuilder 사용자별_조회 = QueryBuilders.matchQuery("jira_server_id", 지라서버_아이디);

        TermsAggregationBuilder 프로젝트별_집계
                = AggregationBuilders.terms("프로젝트키별_집계").field("project.project_key.keyword")
                    .subAggregation(
                            AggregationBuilders.terms("생태별_집계").field("status.status_name.keyword")
                    );

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(사용자별_조회)
                .withAggregations(프로젝트별_집계);
        검색결과_목록_메인 검색결과_목록_메인 = 지라이슈저장소.aggregationSearch(nativeSearchQueryBuilder.build());
        List<검색결과> 프로젝트키별_집계 = 검색결과_목록_메인.get검색결과().get("프로젝트키별_집계");
        Map<String, Map<String, Integer>> 프로젝트별상태값_집계= new HashMap<>();

        for (검색결과 프로젝트 : 프로젝트키별_집계) {
            String 프로젝트이름 = 프로젝트.get필드명();
            Map<String,Integer> 상태값_프로젝트별통계= new HashMap<>();
            프로젝트별상태값_집계.put(프로젝트이름 , 상태값_프로젝트별통계 );

            List<검색결과> 생태별_집계 = 프로젝트.get하위검색결과().get("생태별_집계");

            for (검색결과 상태 : 생태별_집계) {
                String 상태이름  = 상태.get필드명();
                int docCount  =(int)상태.get개수();
                상태값_프로젝트별통계.put(상태이름 , docCount );
            }

        }
        return 프로젝트별상태값_집계;
    }

    @Override
    public Map<String, Long> 제품서비스_버전별_상태값_통계(Long 제품서비스_아이디, Long 버전_아이디) throws IOException {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchAllQuery());

        if ( 제품서비스_아이디 != null && 제품서비스_아이디 > 9L) {
            MatchQueryBuilder 제품서비스_조회 = QueryBuilders.matchQuery("pdServiceId", 제품서비스_아이디);
            nativeSearchQueryBuilder.withQuery(제품서비스_조회);
        }

        if ( 버전_아이디 != null && 버전_아이디 > 9L) {
            MatchQueryBuilder 제품서비스_버전_조회 = QueryBuilders.matchQuery("pdServiceVersion", 버전_아이디);
            nativeSearchQueryBuilder.withQuery(제품서비스_버전_조회);
        }

        nativeSearchQueryBuilder.withAggregations(
                AggregationBuilders.terms("status_name_agg").field("status.status_name.keyword")
        );

        // Execute the search
        // Extract the Terms aggregation results
        검색결과_목록_메인 검색결과_목록_메인 = 지라이슈저장소.aggregationSearch(nativeSearchQueryBuilder.build());
        List<검색결과> 상태값통계 = 검색결과_목록_메인.get검색결과().get("status_name_agg");

        // Iterate through the aggregation buckets

        Map<String, Long> 제품서비스_버전별_집계 = new HashMap<>();
        for (검색결과 상태값 : 상태값통계) {
            String statusName = 상태값.get필드명();
            long docCount = 상태값.get개수();
            log.info("Status Name: " + statusName + ", Count: " + docCount);

            제품서비스_버전별_집계.put(statusName, docCount);
        }

        return 제품서비스_버전별_집계;

    }

    @Override
    public Map<String, Long> 제품서비스별_담당자_통계(Long 지라서버_아이디, Long 제품서비스_아이디) throws IOException {

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder 복합조회 = QueryBuilders.boolQuery();

        if ( 제품서비스_아이디 != null && 제품서비스_아이디 > 9L) {
            MatchQueryBuilder 제품서비스_조회 = QueryBuilders.matchQuery("pdServiceId", 제품서비스_아이디);
            복합조회.must(제품서비스_조회);
        }

        nativeSearchQueryBuilder.withQuery(복합조회)
            .withMaxResults(0)
            .withAggregations( AggregationBuilders.terms("담당자별_집계").field("assignee.assignee_emailAddress.keyword"));

        검색결과_목록_메인 검색결과_목록_메인 = 지라이슈저장소.aggregationSearch(nativeSearchQueryBuilder.build());
        long 결과 = 검색결과_목록_메인.get전체합계();
        로그.info("검색결과 개수: " + 결과);

        List<검색결과> 담당자별_집계 = 검색결과_목록_메인.get검색결과().get("담당자별_집계");

        long 담당자_총합 = 0;
        Map<String, Long> 제품서비스별_하위이슈_담당자_집계 = new HashMap<>();
        for (검색결과 담당자 : 담당자별_집계) {
            String 담당자_이메일 = 담당자.get필드명();
            long 개수 = 담당자.get개수();
            log.info("담당자: " + 담당자_이메일 + ", Count: " + 개수);
            담당자_총합+= 개수;
            제품서비스별_하위이슈_담당자_집계.put(담당자_이메일, 개수);
        }
        제품서비스별_하위이슈_담당자_집계.put("담당자 미지정",결과-담당자_총합);

        return 제품서비스별_하위이슈_담당자_집계;
    }

    @Override
    public Map<String, Long> 제품서비스별_소요일_통계(Long 지라서버_아이디, Long 제품서비스_아이디) throws IOException {

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder 복합조회 = QueryBuilders.boolQuery();

        if ( 제품서비스_아이디 != null && 제품서비스_아이디 > 9L) {
            MatchQueryBuilder 제품서비스_조회 = QueryBuilders.matchQuery("pdServiceId", 제품서비스_아이디);
            복합조회.must(제품서비스_조회);
        }

        nativeSearchQueryBuilder.withQuery(복합조회)
            .withMaxResults(10000);

        List<지라이슈> 지라이슈들 = 지라이슈저장소.normalSearch(nativeSearchQueryBuilder.build());

        long 결과 = 지라이슈들.size();
        로그.info("검색결과 개수: " + 결과);

        Map<String, Long> 업데이트날짜차이_결과 = new HashMap<>();

        for (지라이슈 지라이슈 : 지라이슈들) {

            String 생성일 = 지라이슈.getCreated();
            String 수정일 = 지라이슈.getUpdated();

            if (생성일 == null || 수정일 == null || 생성일.isEmpty()|| 수정일.isEmpty()) {
                continue;
            }

            LocalDateTime 생성일_날짜포맷 = parseDateTime(생성일);
            LocalDateTime 수정일_날짜포맷 = parseDateTime(수정일);

            long 날짜차이 = ChronoUnit.DAYS.between(생성일_날짜포맷, 수정일_날짜포맷);

            String key = 날짜차이 + "일";

            if (업데이트날짜차이_결과.containsKey(key)) {
                업데이트날짜차이_결과.put(key, 업데이트날짜차이_결과.get(key) + 1);
            } else {
                업데이트날짜차이_결과.put(key, 1L);
            }
        }

        return 업데이트날짜차이_결과;
    }

    private static LocalDateTime parseDateTime(String dateTimeStr) {

        try {
            // 온프레미스 날짜형식
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (DateTimeParseException e) {
            // 클라우드 날짜형식
            DateTimeFormatter formatterWithoutColonInTimeZone =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            return LocalDateTime.parse(dateTimeStr, formatterWithoutColonInTimeZone);
        }
    }

    @Override
    public Map<String, Long> 제품서비스별_담당자_요구사항_통계(Long 지라서버_아이디, Long 제품서비스_아이디, String 담당자_이메일) throws IOException {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder 복합조회 = QueryBuilders.boolQuery();

        if ( 제품서비스_아이디 != null && 제품서비스_아이디 > 9L) {
            MatchQueryBuilder 제품서비스_조회 = QueryBuilders.matchQuery("pdServiceId", 제품서비스_아이디);
            복합조회.must(제품서비스_조회);
        }

        nativeSearchQueryBuilder.withQuery(복합조회)
                .withMaxResults(10000);

        검색결과_목록_메인 검색결과_목록_메인 = 지라이슈저장소.aggregationSearch(nativeSearchQueryBuilder.build());
        long 요구사항_개수 = 검색결과_목록_메인.get전체합계();

        if ( 담당자_이메일 != null ) {
            MatchQueryBuilder 담당자_조회 = QueryBuilders.matchQuery("assignee.assignee_emailAddress.keyword", 담당자_이메일);
            복합조회.must(담당자_조회);
        }

        NativeSearchQueryBuilder aggregationQuery = new NativeSearchQueryBuilder();

        aggregationQuery.withQuery(복합조회)
                .withAggregations(AggregationBuilders.terms("상태값_집계").field("status.status_name.keyword"))
                .withMaxResults(10000);

        검색결과_목록_메인 검색결과_목록_메인_집계 = 지라이슈저장소.aggregationSearch(aggregationQuery.build());
        long 할당된_요구사항_개수 = 검색결과_목록_메인_집계.get전체합계();

        로그.info("요구사항 개수: " + 요구사항_개수);
        로그.info("할당된 요구사항 개수: " + 할당된_요구사항_개수);

        List<검색결과> 상태값_집계 = 검색결과_목록_메인_집계.get검색결과().get("상태값_집계");

        Map<String, Long> 제품서비스별_담당자_요구사항_통계 = new HashMap<>();
        제품서비스별_담당자_요구사항_통계.put("allReq", 요구사항_개수);
        제품서비스별_담당자_요구사항_통계.put("myReq", 할당된_요구사항_개수);

        for (검색결과 상태값 : 상태값_집계) {

            String 상태 = 상태값.get필드명();
            long 개수 = 상태값.get개수();
            log.info("상태값: " + 상태 + ", Count: " + 개수);

            제품서비스별_담당자_요구사항_통계.put(상태, 개수);
        }

        return 제품서비스별_담당자_요구사항_통계;
    }

    @Override
    public Map<String, Long> 제품서비스별_담당자_연관된_요구사항_통계(Long 지라서버_아이디, Long 제품서비스_아이디, String 이슈키, String 담당자_이메일) throws IOException {

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder 복합조회 = QueryBuilders.boolQuery();

        if ( 지라서버_아이디 != null ) {
            MatchQueryBuilder 지라서버_조회 = QueryBuilders.matchQuery("jira_server_id", 지라서버_아이디);
            복합조회.must(지라서버_조회);
        }

        if ( 제품서비스_아이디 != null && 제품서비스_아이디 > 9L) {
            MatchQueryBuilder 제품서비스_조회 = QueryBuilders.matchQuery("pdServiceId", 제품서비스_아이디);
            복합조회.must(제품서비스_조회);
        }

        if ( 이슈키 != null ) {
            MatchQueryBuilder 요구사항_조회 = QueryBuilders.matchQuery("key", 이슈키);
            MatchQueryBuilder 하위_요구사항_조회 = QueryBuilders.matchQuery("parentReqKey", 이슈키);
            복합조회.should(요구사항_조회);
            복합조회.should(하위_요구사항_조회);
            복합조회.minimumShouldMatch(1);
        }

        nativeSearchQueryBuilder.withQuery(복합조회)
                .withMaxResults(10000);

        long 연관된_요구사항_개수
                = 지라이슈저장소.aggregationSearch(nativeSearchQueryBuilder.build()).get전체합계();

        if ( 담당자_이메일 != null ) {
            MatchQueryBuilder 담당자_조회 = QueryBuilders.matchQuery("assignee.assignee_emailAddress.keyword", 담당자_이메일);
            복합조회.must(담당자_조회);
        }


        NativeSearchQueryBuilder aggregationQuery = new NativeSearchQueryBuilder();

        aggregationQuery.withQuery(복합조회)
                .withAggregations(AggregationBuilders.terms("상태값_집계").field("status.status_name.keyword"))
                .withMaxResults(10000);

        검색결과_목록_메인 검색결과_목록_메인_집계 = 지라이슈저장소.aggregationSearch(aggregationQuery.build());
        long 할당된_요구사항_개수 = 검색결과_목록_메인_집계.get전체합계();

        로그.info("연관된 요구사항 개수: " + 연관된_요구사항_개수);
        로그.info("할당된 요구사항 개수: " + 할당된_요구사항_개수);

        List<검색결과> 상태값_집계 = 검색결과_목록_메인_집계.get검색결과().get("상태값_집계");

        Map<String, Long> 제품서비스별_담당자_연관된_요구사항_통계 = new HashMap<>();
        제품서비스별_담당자_연관된_요구사항_통계.put("allReq", 연관된_요구사항_개수);
        제품서비스별_담당자_연관된_요구사항_통계.put("myReq", 할당된_요구사항_개수);

        for (검색결과 상태값 : 상태값_집계) {
            String 상태 = 상태값.get필드명();
            long 개수 = 상태값.get개수();
            log.info("상태값: " + 상태 + ", Count: " + 개수);

            제품서비스별_담당자_연관된_요구사항_통계.put(상태, 개수);
        }

        return 제품서비스별_담당자_연관된_요구사항_통계;
    }

    @Override
    public List<지라이슈> 제품서비스_버전목록으로_조회(Long pdServiceLink, List<Long> pdServiceVersionLinks) {
        return 지라이슈저장소.findByPdServiceIdAndPdServiceVersionIn(pdServiceLink, pdServiceVersionLinks);
    }
}
