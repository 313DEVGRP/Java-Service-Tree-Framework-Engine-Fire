package com.arms.elasticsearch.services;

import com.arms.elasticsearch.models.지라이슈;
import com.arms.elasticsearch.repositories.지라이슈_저장소;
import com.arms.elasticsearch.util.검색결과;
import com.arms.elasticsearch.util.검색엔진_유틸;
import com.arms.elasticsearch.util.검색조건;
import com.arms.jira.jiraissue.model.지라이슈_데이터;
import com.arms.jira.jiraissue.service.지라이슈_전략_호출;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.arms.elasticsearch.helper.인덱스자료.지라이슈_인덱스명;

@Slf4j
@Service("지라이슈_서비스")
@AllArgsConstructor
public class 지라이슈_검색엔진 implements 지라이슈_서비스{

    private 지라이슈_저장소 지라이슈저장소;

    private 검색엔진_유틸 검색엔진_유틸;

    private ElasticsearchOperations 검색엔진_실행기;

    private 지라이슈_전략_호출 지라이슈_전략_호출;

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
                .collect(Collectors.toList());
        검색엔진_실행기.bulkIndex(검색엔진_쿼리, IndexCoordinates.of(지라이슈_인덱스명));

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

        if( 이슈 == null ){
            return null;
        }else{
            지라이슈저장소.delete(이슈);
            return 이슈;
        }
    }

    @Override
    public 지라이슈 이슈_조회하기(String 조회조건_아이디) {
        return 지라이슈저장소.findById(조회조건_아이디).orElse(null);
    }

    @Override
    public List<지라이슈> 이슈_검색하기(검색조건 검색조건) {
        final SearchRequest request = 검색엔진_유틸.buildSearchRequest(
                지라이슈_인덱스명,
                검색조건
        );

        return 검색엔진_유틸.searchInternal(request,지라이슈.class);
    }

    @Override
    public Map<String, Long> 특정필드의_값들을_그룹화하여_빈도수가져오기(String indexName, String groupByField) throws IOException {
        return 검색엔진_유틸.특정필드의_값들을_그룹화하여_빈도수가져오기(indexName, groupByField);
    }

    @Override
    public List<검색결과> 특정필드_검색후_다른필드_그룹결과(String 인덱스이름, String 특정필드, String 특정필드검색어, String 그룹할필드) throws IOException {
        return 검색엔진_유틸.특정필드_검색후_다른필드_그룹결과(인덱스이름, 특정필드, 특정필드검색어, 그룹할필드 );
    }


    @Override
    public int 이슈_링크드이슈_서브테스크_벌크로_추가하기(Long 지라서버_아이디, String 이슈_키) throws Exception {

        List<지라이슈> 벌크_저장_목록 = new ArrayList<지라이슈>();

        지라이슈_데이터 받아온_이슈 = 지라이슈_전략_호출.이슈_상세정보_가져오기(지라서버_아이디, 이슈_키);
        지라이슈 저장할_요구사항_이슈 = ELK_데이터로_변환(지라서버_아이디, 받아온_이슈,
                true, "");

        벌크_저장_목록.add(저장할_요구사항_이슈);

        List<지라이슈_데이터> 받아온_이슈링크_목록 = 지라이슈_전략_호출.이슈링크_가져오기(지라서버_아이디, 이슈_키);
        List<지라이슈_데이터> 받아온_서브테스크_목록 = 지라이슈_전략_호출.서브테스크_가져오기(지라서버_아이디, 이슈_키);

        List<지라이슈_데이터> 이슈링크_또는_서브테스크_목록 = new ArrayList<지라이슈_데이터>();

        이슈링크_또는_서브테스크_목록.addAll(받아온_이슈링크_목록);
        이슈링크_또는_서브테스크_목록.addAll(받아온_서브테스크_목록);

        for (지라이슈_데이터 이슈링크_또는_서브테스크 : 이슈링크_또는_서브테스크_목록) {

            지라이슈 ELK로_저장할_이슈링크_또는_서브테스크 = ELK_데이터로_변환(지라서버_아이디, 이슈링크_또는_서브테스크,
                    false, 이슈_키);
            벌크_저장_목록.add(ELK로_저장할_이슈링크_또는_서브테스크);
        }

        return 대량이슈_추가하기(벌크_저장_목록);
    }

    private 지라이슈 ELK_데이터로_변환(Long 지라서버_아이디, 지라이슈_데이터 지라이슈_데이터,
                             boolean 요구사항유형_여부, String 부모_요구사항_키) {

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
                        .build())
                .orElse(null);

        지라이슈.보고자 보고자 = Optional.ofNullable(지라이슈_데이터.getFields().getReporter())
                .map(reporter -> 지라이슈.보고자.builder()
                        .accountId(Optional.ofNullable(reporter.getAccountId()).orElse(null))
                        .emailAddress(Optional.ofNullable(reporter.getEmailAddress()).orElse(null))
                        .build())
                .orElse(null);

        지라이슈.담당자 담당자 = Optional.ofNullable(지라이슈_데이터.getFields().getAssignee())
                .map(assignee -> 지라이슈.담당자.builder()
                        .accountId(Optional.ofNullable(assignee.getAccountId()).orElse(null))
                        .emailAddress(Optional.ofNullable(assignee.getEmailAddress()).orElse(null))
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
                .collect(Collectors.toList());

        지라이슈 이슈 = 지라이슈.builder()
                .jira_server_id(지라서버_아이디)
                .issueID(지라이슈_데이터.getId().toString())
                .key(지라이슈_데이터.getKey())
                .self(지라이슈_데이터.getSelf())
                .parentReqKey(부모_요구사항_키)
                .isReq(요구사항유형_여부)
                .project(프로젝트)
                .issuetype(이슈유형)
                .creator(생성자)
                .reporter(보고자)
                .assignee(담당자)
                .labels(지라이슈_데이터.getFields().getLabels())
                .priority(우선순위)
                .status(상태)
                .resolution(해결책)
                .resolutiondate(지라이슈_데이터.getFields().getResolutiondate())
                .created(지라이슈_데이터.getFields().getCreated())
                .worklogs(워크로그)
                .timespent(지라이슈_데이터.getFields().getTimespent())
                .summary(지라이슈_데이터.getFields().getSummary())
                .build();

        이슈.generateId();

        return 이슈;
    }

    @Override
    public List<지라이슈> 페이징_이슈_조회하기(Long 지라서버_아이디, String 이슈_키, int 페이지_번호) {

        List<String> 검색_필드 = new ArrayList<>(Arrays.asList("key", "parentReqKey"));
        int 페이지_단위 = 10;

        검색조건 검색조건 = new 검색조건();
        검색조건.setFields(검색_필드);
        검색조건.setSearchTerm(이슈_키);
        검색조건.setPage(페이지_번호);
        검색조건.setSize(페이지_단위);

        final SearchRequest request = 검색엔진_유틸.buildSearchRequest(
                지라이슈_인덱스명,
                검색조건,
                지라서버_아이디
        );

        return 검색엔진_유틸.searchInternal(request, 지라이슈.class);
    }
}

