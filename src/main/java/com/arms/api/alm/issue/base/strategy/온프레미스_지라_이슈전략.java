package com.arms.api.alm.issue.base.strategy;

import com.arms.api.alm.issue.base.model.dto.*;
import com.arms.api.alm.issue.priority.model.이슈우선순위_데이터;
import com.arms.api.alm.issue.resolution.model.이슈해결책_데이터;
import com.arms.api.alm.issue.status.model.이슈상태_데이터;
import com.arms.api.alm.issue.type.model.이슈유형_데이터;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import com.arms.api.alm.utils.지라API_정보;
import com.arms.api.alm.utils.지라유틸;
import com.arms.api.util.errors.codes.에러코드;
import com.arms.api.util.errors.에러로그_유틸;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import io.atlassian.util.concurrent.Promise;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

@Component
public class 온프레미스_지라_이슈전략 implements 이슈전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private final 지라유틸 지라유틸;
    private final 지라API_정보 지라API_정보;

    @Autowired
    public 온프레미스_지라_이슈전략(지라유틸 지라유틸,
                                지라API_정보 지라API_정보) {
        this.지라유틸 = 지라유틸;
        this.지라API_정보 = 지라API_정보;
    }

    @Override
    public List<지라이슈_데이터> 이슈_목록_가져오기(서버정보_데이터 서버정보, String 프로젝트_키_또는_아이디) {

        try {
            JiraRestClient restClient = 지라유틸.온프레미스_통신기_생성(서버정보.getUri(),
                    서버정보.getUserId(),
                    서버정보.getPasswordOrToken());

            String 조회할_프로젝트 = "project = " + 프로젝트_키_또는_아이디;

            int 검색_시작_지점 = 0;
            int 최대_검색수 = 지라API_정보.getParameter().getMaxResults();
            Set<String> 필드 = new HashSet<>(Arrays.asList("*all")); // 검색 필드

            // 이슈 건수가 1000이 넘을 때 이슈 조회를 위한 처리
            List<지라이슈_데이터> 프로젝트_이슈_목록 = new ArrayList<>();
            while (true) {
                SearchResult 프로젝트_이슈_검색결과 = restClient.getSearchClient()
                        .searchJql(조회할_프로젝트, 최대_검색수, 검색_시작_지점, 필드)
                        .claim();

                for (Issue 지라이슈 : 프로젝트_이슈_검색결과.getIssues()) {
                    프로젝트_이슈_목록.add(지라이슈_데이터로_변환(지라이슈));
                }

                if (프로젝트_이슈_목록.size() >= 프로젝트_이슈_검색결과.getTotal()) {
                    break;
                }

                검색_시작_지점 += 최대_검색수;
            }

            return 프로젝트_이슈_목록;
        }
        catch (Exception e) {
            String 에러로그 = 에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "온프레미스 지라("+ 서버정보.getUri() +") :: 프로젝트("+ 프로젝트_키_또는_아이디+ ") :: 전체이슈_목록_가져오기에 실패하였습니다.");
            throw new IllegalArgumentException(에러코드.이슈_조회_오류.getErrorMsg() + " :: " + 에러로그);
        }
    }

    /* ***
     * 수정사항: null 체크하여 에러 처리 필요
     *** */
    @Override
    public 지라이슈_데이터 이슈_생성하기(서버정보_데이터 서버정보, 지라이슈생성_데이터 지라이슈생성_데이터) {

        JiraRestClient restClient = null;
        try {
            restClient = 지라유틸.온프레미스_통신기_생성(서버정보.getUri(),
                                                                서버정보.getUserId(),
                                                                서버정보.getPasswordOrToken());
        }
        catch (URISyntaxException | IOException e) {
            에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "온프레미스 지라(" + 서버정보.getUri() + ") :: 이슈_생성하기 온프레미스_통신기_생성 중 오류");
            return null;
        }

        지라이슈생성필드_데이터 필드_데이터 = 지라이슈생성_데이터.getFields();
        if (필드_데이터 == null) {
            String 에러로그 = 서버정보.getType() + " :: "+ 서버정보.getUri() +" 이슈 생성 필드 데이터가 존재 하지 않습니다.";
            throw new IllegalArgumentException(에러코드.요청본문_오류체크.getErrorMsg() + " :: " + 에러로그);
        }

        String 프로젝트키 = null;
        Long 이슈유형아이디 = null;
        String 제목 = null;
        String 내용 = null;
        String 보고자 = null;
        Long 우선순위아이디 = null;
        Date 종료기한일자 = null;

        if (필드_데이터.getProject() != null) {
            프로젝트키 = 필드_데이터.getProject().getKey();
        }

        if (필드_데이터.getIssuetype() != null) {
            이슈유형아이디 = Long.valueOf(필드_데이터.getIssuetype().getId());
        }

        if (필드_데이터.getSummary() != null) {
            제목 = 필드_데이터.getSummary();
        }

        if (필드_데이터.getDescription() != null) {
            내용 = 필드_데이터.getDescription();
        }

        if (필드_데이터.getReporter() != null) {
            보고자 = 필드_데이터.getReporter().getAccountId();
        }

        if (필드_데이터.getPriority() != null) {
            우선순위아이디 = Long.valueOf(필드_데이터.getPriority().getId());
        }

        if (필드_데이터.getDueDate() != null) {
            종료기한일자 = 필드_데이터.getDueDate();
        }

        IssueInputBuilder 입력_생성 = new IssueInputBuilder(프로젝트키, 이슈유형아이디, 제목);

        입력_생성.setDescription(내용);

        if (보고자 != null) {
            입력_생성.setReporterName(보고자);
        }

        if (우선순위아이디 != null) {
            입력_생성.setPriorityId(우선순위아이디);
        }

        if (종료기한일자 != null) {
            DateTime dateTime = new DateTime(종료기한일자);
            입력_생성.setDueDate(dateTime);
        }

        IssueInput 입력_데이터 = 입력_생성.build();

        BasicIssue 생성된_이슈 = restClient.getIssueClient().createIssue(입력_데이터).claim();

        if (생성된_이슈 == null) {
            String 에러로그 = "온프레미스 지라(" + 서버정보.getUri() + ") ::  프로젝트 :: "+프로젝트키+
                    " :: 이슈유형 :: " + 이슈유형아이디 + " :: 생성 필드 :: "+ 입력_데이터.toString() + ", 이슈 생성하기 중 오류";

            로그.error(에러로그);
            throw new IllegalArgumentException(에러코드.이슈생성_오류.getErrorMsg() + 에러로그);
        }

        지라이슈_데이터 반환할_지라이슈_데이터 = new 지라이슈_데이터();
        반환할_지라이슈_데이터.setId(생성된_이슈.getId().toString());
        반환할_지라이슈_데이터.setKey(생성된_이슈.getKey());
        반환할_지라이슈_데이터.setSelf(생성된_이슈.getSelf().toString());

        return 반환할_지라이슈_데이터;
    }

    @Override
    public Map<String, Object> 이슈_수정하기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디, 지라이슈생성_데이터 지라이슈생성_데이터) {

        JiraRestClient restClient;
        try {
            restClient = 지라유틸.온프레미스_통신기_생성(서버정보.getUri(),
                                                                서버정보.getUserId(),
                                                                서버정보.getPasswordOrToken());
        }
        catch (URISyntaxException | IOException e) {
            에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "온프레미스 지라(" + 서버정보.getUri() + ") :: 이슈_수정하기 온프레미스_통신기_생성 중 오류");
            return null;
        }

        Map<String, Object> 결과 = new HashMap<>();
        지라이슈생성필드_데이터 필드_데이터 = 지라이슈생성_데이터.getFields();

        IssueInputBuilder 입력_생성 = new IssueInputBuilder();

        if (필드_데이터.getSummary() != null) {
            입력_생성.setSummary(필드_데이터.getSummary());
        }

        if (필드_데이터.getDescription() != null) {
            입력_생성.setDescription(필드_데이터.getDescription());
        }

        if (필드_데이터.getLabels() != null) {
            입력_생성.setFieldValue("labels", 필드_데이터.getLabels());
        }

        IssueInput 수정_데이터 = 입력_생성.build();
        try {
            Promise<Void> 수정결과 = restClient.getIssueClient().updateIssue(이슈_키_또는_아이디, 수정_데이터);
            수정결과.claim();

            결과.put("success", true);
            결과.put("message", "이슈 수정 성공");
        }
        catch (Exception e) {
            String 에러로그 = 에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "온프레미스 지라(" + 서버정보.getUri() +") ::  생성 필드 :: "
                            + 필드_데이터.toString() + ", :: 이슈_수정하기 중 오류 :: " + 이슈_키_또는_아이디);
            로그.error(에러로그);

            결과.put("success", false);
            결과.put("message", 에러로그);
        }

        return 결과;
    }

    @Override
    public Map<String, Object> 이슈_상태_변경하기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디, String 상태_아이디) {

        JiraRestClient restClient;
        try {
            restClient = 지라유틸.온프레미스_통신기_생성(서버정보.getUri(),
                    서버정보.getUserId(),
                    서버정보.getPasswordOrToken());
        }
        catch (URISyntaxException | IOException e) {
            에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "온프레미스 지라(" + 서버정보.getUri() + ") :: 이슈_상태_변경하기 온프레미스_통신기_생성 중 오류");
            return null;
        }

        Map<String, Object> 결과 = new HashMap<>();
        String 이슈전환_아이디 = null;

        try {
            Issue 지라이슈 = restClient.getIssueClient().getIssue(이슈_키_또는_아이디).claim();
            이슈전환_아이디 = 이슈전환_아이디_조회하기(서버정보, 이슈_키_또는_아이디, 상태_아이디);

            if (이슈전환_아이디 != null) {
                TransitionInput transitionInput = new TransitionInput(Integer.parseInt(이슈전환_아이디));
                Promise<Void> 변경결과 = restClient.getIssueClient().transition(지라이슈, transitionInput);
                변경결과.claim();

                결과.put("success", true);
                결과.put("message", "이슈 상태 변경 성공");
            }
            else {
                String 에러로그 = "온프레미스 지라(" + 서버정보.getUri() + ") :: 이슈 키(" + 이슈_키_또는_아이디 + ") :: 상태 아이디(" + 상태_아이디 + ") :: 해당 업무 흐름으로 변경이 불가능 합니다.";
                로그.error(에러로그);

                결과.put("success", false);
                결과.put("message", "변경할 이슈 상태가 존재하지 않습니다.");
            }

            return 결과;

        }
        catch (Exception e) {
            String 에러로그 = 에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "온프레미스 지라(" + 서버정보.getUri() + ") :: 이슈 키(" + 이슈_키_또는_아이디 + ") :: 상태 아이디(" + 상태_아이디 + ") :: 전환 아이디(" + 이슈전환_아이디 + ") :: 이슈_상태_변경하기에 실패하였습니다.");
            throw new IllegalArgumentException(에러코드.이슈전환_오류.getErrorMsg() + " :: " + 에러로그);
        }
    }

    public String 이슈전환_아이디_조회하기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디, String 상태_아이디) {

        try {
            WebClient webClient = 지라유틸.클라우드_통신기_생성(서버정보.getUri(), 서버정보.getUserId(), 서버정보.getPasswordOrToken());

            String endpoint = "/rest/api/2/issue/" + 이슈_키_또는_아이디 + "/transitions";

            지라이슈전환_데이터 이슈전환_데이터 = 지라유틸.get(webClient, endpoint, 지라이슈전환_데이터.class).block();

            return Optional.ofNullable(이슈전환_데이터)
                    .map(지라이슈전환_데이터::getTransitions)
                    .orElse(Collections.emptyList()).stream()
                    .filter(데이터 -> {
                        if (데이터.getTo() != null && !StringUtils.isBlank(데이터.getTo().getId())) {
                            return 상태_아이디.equals(데이터.getTo().getId());
                        }
                        return false;
                    })
                    .findFirst()
                    .map(지라이슈전환_데이터.Transition::getId)
                    .orElse(null);

        } catch (Exception e) {
            String 에러로그 = 에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "온프레미스 지라(" + 서버정보.getUri() + ") :: 이슈 키(" + 이슈_키_또는_아이디 + ") :: 상태 아이디(" + 상태_아이디 + ") :: 이슈전환_아이디_조회하기에 실패하였습니다.");
            throw new IllegalArgumentException(에러코드.이슈전환_조회_오류.getErrorMsg() + " :: " + 에러로그);
        }
    }

    @Override
    public Map<String, Object> 이슈_삭제하기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디) {

        JiraRestClient restClient;
        try {
            restClient = 지라유틸.온프레미스_통신기_생성(서버정보.getUri(),
                    서버정보.getUserId(),
                    서버정보.getPasswordOrToken());
        }
        catch (URISyntaxException | IOException e) {
            에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "온프레미스 지라(" + 서버정보.getUri() + ") :: 이슈_삭제하기 온프레미스_통신기_생성 중 오류");
            return null;
        }

        Map<String, Object> 결과 = new HashMap<String, Object>();
        try {
            boolean 하위이슈_삭제유무 = 지라API_정보.getParameter().isDeleteSubtasks();
            Promise<Void> 삭제결과 = restClient.getIssueClient().deleteIssue(이슈_키_또는_아이디, 하위이슈_삭제유무);
            삭제결과.claim();

            결과.put("success", true);
            결과.put("message", "이슈 삭제 성공");
        }
        catch (Exception e) {
            String 에러로그 = 에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "온프레미스 지라(" + 서버정보.getUri() + ") :: 이슈_삭제하기 중 오류 :: " + 이슈_키_또는_아이디);
            로그.error(에러로그);

            결과.put("success", false);
            결과.put("message", 에러로그);
        }

        return 결과;
    }

    public 지라이슈_데이터 지라이슈_데이터로_변환(Issue 지라이슈) {
        return 지라이슈_데이터로_변환(지라이슈, null);
    }

    public 지라이슈_데이터 지라이슈_데이터로_변환(Issue 지라이슈, String 이슈_키_또는_아이디) {
        지라이슈_데이터 지라이슈_데이터 = new 지라이슈_데이터();
        지라이슈필드_데이터 지라이슈필드_데이터 = new 지라이슈필드_데이터();

        지라이슈_데이터.setId(지라이슈.getId().toString());
        지라이슈_데이터.setKey(지라이슈.getKey());
        지라이슈_데이터.setSelf(지라이슈.getSelf().toString());

        if (이슈_키_또는_아이디 != null) {
            지라이슈_데이터.setUpperKey(이슈_키_또는_아이디);
        }

        // 필드
        // 초기화
        지라프로젝트_데이터 프로젝트 = 지라프로젝트_데이터.builder().build();
        지라사용자_데이터 보고자 = 지라사용자_데이터.builder().build();
        지라사용자_데이터 담당자 = 지라사용자_데이터.builder().build();

        // 프로젝트
        if (지라이슈.getProject() != null) {

            프로젝트.setSelf(지라이슈.getProject().getSelf().toString());
            프로젝트.setId(지라이슈.getProject().getId().toString());
            프로젝트.setKey(지라이슈.getProject().getKey());
            프로젝트.setName(지라이슈.getProject().getName());

            지라이슈필드_데이터.setProject(프로젝트);
        }

        // 이슈 유형
        if (지라이슈.getIssueType() != null) {

            String 이슈유형_주소 = String.valueOf(지라이슈.getIssueType().getSelf());
            String 이슈유형_아이디 = String.valueOf(지라이슈.getIssueType().getId());
            String 이슈유형_이름 = 지라이슈.getIssueType().getName();
            String 이슈유형_내용 = 지라이슈.getIssueType().getDescription();
            Boolean 이슈유형_서브테스크여부 = 지라이슈.getIssueType().isSubtask();

            이슈유형_데이터 이슈유형 = new 이슈유형_데이터();
            이슈유형.setSelf(이슈유형_주소);
            이슈유형.setId(이슈유형_아이디);
            이슈유형.setName(이슈유형_이름);
            이슈유형.setDescription(이슈유형_내용);
            이슈유형.setSubtask(이슈유형_서브테스크여부);

            지라이슈필드_데이터.setIssuetype(이슈유형);
        }

        // 생성자

        // 보고자
        if (지라이슈.getReporter() != null) {

            보고자.setAccountId(지라이슈.getReporter().getName());
            보고자.setEmailAddress(지라이슈.getReporter().getEmailAddress());
            보고자.setDisplayName(지라이슈.getReporter().getDisplayName());

            지라이슈필드_데이터.setReporter(보고자);
        }

        // 담당자
        if (지라이슈.getAssignee() != null) {

            담당자.setAccountId(지라이슈.getAssignee().getName());
            담당자.setEmailAddress(지라이슈.getAssignee().getEmailAddress());
            담당자.setDisplayName(지라이슈.getAssignee().getDisplayName());

            지라이슈필드_데이터.setAssignee(담당자);
        }

        // 라벨
        if (지라이슈.getLabels() != null) {
            Set<String> 라벨_목록 = 지라이슈.getLabels();
            List<String> 이슈라벨 = new ArrayList<>(라벨_목록);
            지라이슈필드_데이터.setLabels(이슈라벨);
        }

        // 우선 순위
        if (지라이슈.getPriority() != null) {

            String 이슈우선순위_주소 = String.valueOf(지라이슈.getPriority().getSelf());
            String 이슈우선순위_아이디 = String.valueOf(지라이슈.getPriority().getId());
            String 이슈우선순위_이름 = 지라이슈.getPriority().getName();

            이슈우선순위_데이터 이슈우선순위 = new 이슈우선순위_데이터();
            이슈우선순위.setSelf(이슈우선순위_주소);
            이슈우선순위.setId(이슈우선순위_아이디);
            이슈우선순위.setName(이슈우선순위_이름);
            // description

            지라이슈필드_데이터.setPriority(이슈우선순위);
        }

        // 상태 값
        if (지라이슈.getStatus() != null) {

            String 이슈상태_주소 = String.valueOf(지라이슈.getStatus().getSelf());
            String 이슈상태_아이디 = String.valueOf(지라이슈.getStatus().getId());
            String 이슈상태_이름 = 지라이슈.getStatus().getName();
            String 이슈상태_설명 =  지라이슈.getStatus().getDescription();

            이슈상태_데이터 이슈상태 = new 이슈상태_데이터();
            이슈상태.setSelf(이슈상태_주소);
            이슈상태.setId(이슈상태_아이디);
            이슈상태.setName(이슈상태_이름);
            이슈상태.setDescription(이슈상태_설명);

            지라이슈필드_데이터.setStatus(이슈상태);
        }

        // 해결책
        if (지라이슈.getResolution() != null) {

            String 이슈해결책_주소 = String.valueOf(지라이슈.getResolution().getSelf());
            String 이슈해결책_아이디 = String.valueOf(지라이슈.getResolution().getId());
            String 이슈해결책_이름 = 지라이슈.getResolution().getName();
            String 이슈해결책_설명 = 지라이슈.getResolution().getDescription();

            이슈해결책_데이터 이슈해결책 = new 이슈해결책_데이터();
            이슈해결책.setSelf(이슈해결책_주소);
            이슈해결책.setId(이슈해결책_아이디);
            이슈해결책.setName(이슈해결책_이름);
            이슈해결책.setDescription(이슈해결책_설명);

            지라이슈필드_데이터.setResolution(이슈해결책);
        }

        // resolutiondate
        for (IssueField 필드 : 지라이슈.getFields()) {
            if (필드 != null && !필드.getId().isEmpty() && 필드.getId().equals("resolutiondate")) {
                if (필드.getValue().toString() != null) {
                    지라이슈필드_데이터.setResolutiondate(필드.getValue().toString());
                }
                break;
            }
        }

        // created
        if (지라이슈.getCreationDate() != null) {
            String 이슈생성날짜 = String.valueOf(지라이슈.getCreationDate());
            지라이슈필드_데이터.setCreated(이슈생성날짜);
        }

        if (지라이슈.getUpdateDate() != null) {
            String 이슈수정날짜 = String.valueOf(지라이슈.getUpdateDate());
            지라이슈필드_데이터.setUpdated(이슈수정날짜);
        }

        // worklogs
        // BasicUser 타입에서 이메일 데이터를 받아올 수 없어서 고민 중...
        if (지라이슈.getWorklogs() != null) {

            List<지라이슈워크로그_데이터> 이슈워크로그_목록 = new ArrayList<>();

            Iterable<Worklog> 전체이슈워크로그 = 지라이슈.getWorklogs();

            for (Worklog 워크로그 : 전체이슈워크로그) {
                지라사용자_데이터 작성자 = new 지라사용자_데이터();

                String 이슈워크로그_주소 = 워크로그.getSelf().toString();
                BasicUser 이슈워크로그_작성자 = 워크로그.getAuthor();
                String 이슈워크로그_작성자아이디 = 이슈워크로그_작성자.getName();
                //String 이슈워크로그_작성자이메일 = 이슈워크로그_작성자.getSelf().toString();
                BasicUser 이슈워크로그_수정작성자 = 워크로그.getUpdateAuthor();
                String 이슈워크로그_수정작성자아이디 = 이슈워크로그_수정작성자.getName();
                //String 이슈워크로그_수정작성자이메일 = 이슈워크로그_수정작성자.getSelf().toString();
                String 이슈워크로그_생성날짜 = 워크로그.getCreationDate().toString();
                String 이슈워크로그_수정날짜 = 워크로그.getUpdateDate().toString();
                String 이슈워크로그_시작날짜 = 워크로그.getStartDate().toString();
                String 이슈워크로그_소요시간_포맷 = 시간_포맷(워크로그.getMinutesSpent());
                Integer 이슈워크로그_소요시간 = 워크로그.getMinutesSpent() * 60;
                String[] 이슈워크로그_아이디 = 이슈워크로그_주소.split("/");

                지라이슈워크로그_데이터 이슈워크로그 = new 지라이슈워크로그_데이터();
                이슈워크로그.setSelf(이슈워크로그_주소);

                작성자.setAccountId(이슈워크로그_작성자아이디);
                //작성자.setEmailAddress(이슈워크로그_작성자이메일);
                이슈워크로그.setAuthor(작성자);

                작성자.setAccountId(이슈워크로그_수정작성자아이디);
                //작성자.setEmailAddress(이슈워크로그_수정작성자이메일);
                이슈워크로그.setUpdateAuthor(작성자);

                이슈워크로그.setCreated(이슈워크로그_생성날짜);
                이슈워크로그.setUpdated(이슈워크로그_수정날짜);
                이슈워크로그.setStarted(이슈워크로그_시작날짜);
                이슈워크로그.setTimeSpent(이슈워크로그_소요시간_포맷);
                이슈워크로그.setTimeSpentSeconds(이슈워크로그_소요시간);
                이슈워크로그.setId(이슈워크로그_아이디[이슈워크로그_아이디.length - 1]);
                이슈워크로그.setIssueId(지라이슈_데이터.getId());

                이슈워크로그_목록.add(이슈워크로그);
            }
            지라이슈필드_데이터.setWorklogs(이슈워크로그_목록);
        }

        // timespent
        if (지라이슈.getTimeTracking().getTimeSpentMinutes() != null) {
            Integer 이슈소요시간 = 지라이슈.getTimeTracking().getTimeSpentMinutes() * 60;
            지라이슈필드_데이터.setTimespent(이슈소요시간);
        }

        if (지라이슈.getSummary() != null) {
            지라이슈필드_데이터.setSummary(지라이슈.getSummary());
        }

        지라이슈_데이터.setFields(지라이슈필드_데이터);

        return 지라이슈_데이터;
    }

    public String 시간_포맷(int 분) {

        // 1주 = 5일, 1일 = 8시간, 1시간 = 60분
        int 주 = 분 / (5 * 8 * 60);
        int 일 = (분 % (5 * 8 * 60)) / (8 * 60);
        int 시간 = (분 % (8 * 60)) / 60;
        int 남은시간 = 분 % 60;

        StringBuilder 포맷팅 = new StringBuilder();
        if (주 > 0) {
            포맷팅.append(주).append("w ");
        }
        if (일 > 0) {
            포맷팅.append(일).append("d ");
        }
        if (시간 > 0) {
            포맷팅.append(시간).append("h ");
        }
        if (남은시간 > 0 || (주 == 0 && 일 == 0 && 시간 == 0)) {
            포맷팅.append(남은시간).append("m ");
        }

        return 포맷팅.toString().trim();
    }

    @Override
    public 지라이슈_데이터 이슈_상세정보_가져오기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디) {

        JiraRestClient restClient = null;

        try {
            restClient = 지라유틸.온프레미스_통신기_생성(서버정보.getUri(),
                    서버정보.getUserId(),
                    서버정보.getPasswordOrToken());
        }
        catch (URISyntaxException | IOException e) {
            에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "온프레미스 지라(" + 서버정보.getUri() + ") ::  이슈_키_또는_아이디 :: "
                            + 이슈_키_또는_아이디 + ", 이슈_상세정보_가져오기 온프레미스_통신기_생성 중 오류");
            return null;
        }

        try {
            Issue 지라이슈 = restClient.getIssueClient().getIssue(이슈_키_또는_아이디).claim();
            return 지라이슈_데이터로_변환(지라이슈);
        }
        catch (Exception e) {
            에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "온프레미스 지라(" + 서버정보.getUri() + ") ::  이슈_키_또는_아이디 :: "+ 이슈_키_또는_아이디 + ", 이슈_상세정보_가져오기 중 오류");
            return null;
        }
    }

    @Override
    public List<지라이슈_데이터> 이슈링크_가져오기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디) {

        JiraRestClient restClient = null;

        try {
            restClient = 지라유틸.온프레미스_통신기_생성(서버정보.getUri(),
                    서버정보.getUserId(),
                    서버정보.getPasswordOrToken());

            Set<String> 이슈아이디_세트 = new HashSet<>();
            이슈아이디_세트.add(이슈_키_또는_아이디);

            List<지라이슈_데이터> 이슈링크_목록 = new ArrayList<>();

            이슈링크_가져오기_재귀(restClient, 서버정보.getUri(), 이슈_키_또는_아이디, 이슈아이디_세트, 이슈링크_목록);

            return 이슈링크_목록;
        }
        catch (URISyntaxException | IOException e) {
            에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "온프레미스 지라(" + 서버정보.getUri() + ") ::  이슈_키_또는_아이디 :: "+ 이슈_키_또는_아이디 + ", 이슈링크_가져오기 온프레미스_통신기_생성 중 오류");
            return null;
        }
    }

    public void 이슈링크_가져오기_재귀(JiraRestClient restClient, String 서버URI, String 이슈_키_또는_아이디, Set<String> 이슈아이디_세트, List<지라이슈_데이터> 이슈링크_목록) {

        String jql = "issue in linkedIssues("+이슈_키_또는_아이디+")";

        int startAt = 0;
        int 최대_검색수 = 지라API_정보.getParameter().getMaxResults();
        Set<String> fields = new HashSet<>(Arrays.asList("*all")); // 검색 필드

        // 이슈 건수가 1000이 넘을때 이슈 조회를 위한 처리
        SearchResult searchResult;

        try {
            do {
                searchResult = restClient.getSearchClient()
                        .searchJql(jql, 최대_검색수, startAt, fields)
                        .get();
                for (Issue issue : searchResult.getIssues()) {
                    if (!이슈아이디_세트.contains(issue.getKey())) {
                        이슈아이디_세트.add(issue.getKey());
                        이슈링크_목록.add(지라이슈_데이터로_변환(issue));
                        이슈링크_가져오기_재귀(restClient, 서버URI, issue.getKey(), 이슈아이디_세트, 이슈링크_목록);
                    }
                }

                startAt += 최대_검색수;

            } while (searchResult.getTotal() > startAt);

        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 스레드를 다시 interrupt

            에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "온프레미스 지라(" + 서버URI + ") ::  이슈_키_또는_아이디 :: "+ 이슈_키_또는_아이디 + ", 이슈링크_가져오기 중 오류");
        }
        catch (Exception e) {
            에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "온프레미스 지라(" + 서버URI + ") ::  이슈_키_또는_아이디 :: "+ 이슈_키_또는_아이디 + ", 이슈링크_가져오기 중 오류");
        }
    }

    @Override
    public List<지라이슈_데이터> 서브테스크_가져오기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디) {

        JiraRestClient restClient = null;
        try {
            restClient = 지라유틸.온프레미스_통신기_생성(서버정보.getUri(),
                    서버정보.getUserId(),
                    서버정보.getPasswordOrToken());
        }
        catch (URISyntaxException | IOException e) {
            에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "온프레미스 지라(" + 서버정보.getUri() + ") ::  이슈_키_또는_아이디 :: "+ 이슈_키_또는_아이디 + ", 서브테스크_가져오기 온프레미스_통신기_생성 중 오류");
            return null;
        }

        String jql = "parent="+ 이슈_키_또는_아이디;

        int startAt = 0;
        int 최대_검색수 = 지라API_정보.getParameter().getMaxResults();
        Set<String> fields = new HashSet<>(Arrays.asList("*all")); // 검색 필드

        // 이슈 건수가 1000이 넘을때 이슈 조회를 위한 처리
        List<지라이슈_데이터> 반환할_서브테스크_목록 = new ArrayList<>();
        SearchResult searchResult;
        try {
            do {
                searchResult = restClient.getSearchClient()
                        .searchJql(jql, 최대_검색수, startAt, fields)
                        .get();
                for (Issue issue : searchResult.getIssues()) {
                    지라이슈_데이터 지라이슈_데이터 = 지라이슈_데이터로_변환(issue, 이슈_키_또는_아이디);
                    반환할_서브테스크_목록.add(지라이슈_데이터);
                }

                startAt += 최대_검색수;
            } while (searchResult.getTotal() > startAt);

            return 반환할_서브테스크_목록;
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 스레드를 다시 interrupt

            에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "온프레미스 지라(" + 서버정보.getUri() + ") ::  이슈_키_또는_아이디 :: "+ 이슈_키_또는_아이디 + ", 서브테스크 가져오기 중 오류");
            return null;
        }
        catch (Exception e) {
            에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "온프레미스 지라(" + 서버정보.getUri() + ") ::  이슈_키_또는_아이디 :: "+ 이슈_키_또는_아이디 + ", 서브테스크 가져오기 중 오류");
            return null;
        }
    }

    @Override
    public 지라이슈_데이터 증분이슈_상세정보_가져오기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디) {

        JiraRestClient restClient = null;
        try {
            restClient = 지라유틸.온프레미스_통신기_생성(서버정보.getUri(),
                    서버정보.getUserId(),
                    서버정보.getPasswordOrToken());
        }
        catch (URISyntaxException | IOException e) {
            에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "온프레미스 지라(" + 서버정보.getUri() + ") ::  이슈_키_또는_아이디 :: "+ 이슈_키_또는_아이디 + ", 증분이슈_상세정보_가져오기 온프레미스_통신기_생성 중 오류");
            return null;
        }

        String issueJql = 지라API_정보.이슈키_대체하기(지라API_정보.getParameter().getJql().getIssue(), 이슈_키_또는_아이디);
        String updateJql = 지라API_정보.getParameter().getJql().getUpdated();
        String jql = issueJql + " and " + updateJql;
        Set<String> fields = new HashSet<>(Arrays.asList("*all")); // 검색 필드

        int startAt = 0;
        int 최대_검색수 = 지라API_정보.getParameter().getMaxResults();

        List<지라이슈_데이터> 반환할_이슈상세_목록 = new ArrayList<>();
        SearchResult searchResult;
        try {
            do {
                searchResult = restClient.getSearchClient()
                        .searchJql(jql, 최대_검색수, startAt, fields)
                        .get();
                for (Issue issue : searchResult.getIssues()) {
                    지라이슈_데이터 지라이슈_데이터 = 지라이슈_데이터로_변환(issue);
                    반환할_이슈상세_목록.add(지라이슈_데이터);
                }

                startAt += 최대_검색수;
            } while (searchResult.getTotal() > startAt);

            if (반환할_이슈상세_목록.size() == 1) {
                지라이슈_데이터 지라이슈_데이터 = 반환할_이슈상세_목록.get(0);
                return 지라이슈_데이터;
            }

            return null;
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 스레드를 다시 interrupt

            에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "온프레미스 지라(" + 서버정보.getUri() + ") ::  이슈_키_또는_아이디 :: "+ 이슈_키_또는_아이디 + ", 증분이슈 상세정보 가져오기 중 오류");
            return null;
        }
        catch (Exception e) {
            에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "온프레미스 지라(" + 서버정보.getUri() + ") ::  이슈_키_또는_아이디 :: "+ 이슈_키_또는_아이디 + ", 증분이슈 상세정보 가져오기 중 오류");
            return null;
        }
    }

    @Override
    public List<지라이슈_데이터> 증분이슈링크_가져오기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디) {

        JiraRestClient restClient = null;
        try {
            restClient = 지라유틸.온프레미스_통신기_생성(서버정보.getUri(),
                    서버정보.getUserId(),
                    서버정보.getPasswordOrToken());
        }
        catch (URISyntaxException | IOException e) {
            에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "온프레미스 지라(" + 서버정보.getUri() + ") ::  이슈_키_또는_아이디 :: "+ 이슈_키_또는_아이디 + ", 증분이슈링크 가져오기 온프레미스_통신기_생성 중 오류");
            return null;
        }

        String linkedIssueJql = 지라API_정보.이슈키_대체하기(지라API_정보.getParameter().getJql().getLinkedIssue(), 이슈_키_또는_아이디);
        String updateJql = 지라API_정보.getParameter().getJql().getUpdated();
        String jql = linkedIssueJql + " and " + updateJql;

        int startAt = 0;
        int 최대_검색수 = 지라API_정보.getParameter().getMaxResults();

        Set<String> fields = new HashSet<>(Arrays.asList("*all")); // 검색 필드

        // 이슈 건수가 1000이 넘을때 이슈 조회를 위한 처리
        List<지라이슈_데이터> 반환할_이슈링크_목록 = new ArrayList<>();
        SearchResult searchResult;

        try {
            do {
                searchResult = restClient.getSearchClient()
                        .searchJql(jql, 최대_검색수, startAt, fields)
                        .claim();
                for (Issue issue : searchResult.getIssues()) {
                    지라이슈_데이터 지라이슈_데이터 = 지라이슈_데이터로_변환(issue);
                    반환할_이슈링크_목록.add(지라이슈_데이터);
                }

                startAt += 최대_검색수;

            } while (searchResult.getTotal() > startAt);

            return 반환할_이슈링크_목록;
        }
        catch (Exception e) {
            에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "온프레미스 지라(" + 서버정보.getUri() + ") ::  이슈_키_또는_아이디 :: "+ 이슈_키_또는_아이디 + ", 증분이슈링크 가져오기 중 오류");
            return null;
        }
    }

    @Override
    public List<지라이슈_데이터> 증분서브테스크_가져오기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디) {

        JiraRestClient restClient = null;

        try {
            restClient = 지라유틸.온프레미스_통신기_생성(서버정보.getUri(),
                    서버정보.getUserId(),
                    서버정보.getPasswordOrToken());
        }
        catch (URISyntaxException | IOException e) {
            에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "온프레미스 지라(" + 서버정보.getUri() + ") ::  이슈_키_또는_아이디 :: "+ 이슈_키_또는_아이디 + ", 증분 서브테스크 가져오기 온프레미스_통신기_생성 중 오류");
            return null;
        }

        String subtaskJql = 지라API_정보.이슈키_대체하기(지라API_정보.getParameter().getJql().getSubtask(), 이슈_키_또는_아이디);
        String updateJql = 지라API_정보.getParameter().getJql().getUpdated();
        String jql = subtaskJql + " and " + updateJql;

        int startAt = 0;
        int 최대_검색수 = 지라API_정보.getParameter().getMaxResults();

        Set<String> fields = new HashSet<>(Arrays.asList("*all")); // 검색 필드

        // 이슈 건수가 1000이 넘을때 이슈 조회를 위한 처리
        List<지라이슈_데이터> 반환할_서브테스크_목록 = new ArrayList<>();
        SearchResult searchResult;
        try {
            do {
                searchResult = restClient.getSearchClient()
                        .searchJql(jql, 최대_검색수, startAt, fields)
                        .get();
                for (Issue issue : searchResult.getIssues()) {
                    지라이슈_데이터 지라이슈_데이터 = 지라이슈_데이터로_변환(issue, 이슈_키_또는_아이디);
                    반환할_서브테스크_목록.add(지라이슈_데이터);
                }

                startAt += 최대_검색수;
            } while (searchResult.getTotal() > startAt);

            return 반환할_서브테스크_목록;
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 스레드를 다시 interrupt

            에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "온프레미스 지라(" + 서버정보.getUri() + ") ::  이슈_키_또는_아이디 :: "+ 이슈_키_또는_아이디 + ", 증분 서브테스크 가져오기 중 오류");
            return null;
        }
        catch (Exception e) {
            에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "온프레미스 지라(" + 서버정보.getUri() + ") ::  이슈_키_또는_아이디 :: "+ 이슈_키_또는_아이디 + ", 증분 서브테스크 가져오기 중 오류");
            return null;
        }
    }
}
