package com.arms.jira.jiraissue.service;

import com.arms.api.jira.jiraissue.model.지라사용자_데이터;
import com.arms.api.jira.jiraissue.model.지라이슈_데이터;
import com.arms.api.jira.jiraissue.model.지라이슈워크로그_데이터;
import com.arms.api.jira.jiraissue.model.지라이슈전체워크로그_데이터;
import com.arms.api.jira.jiraissue.model.지라이슈조회_데이터;
import com.arms.api.jira.jiraissue.model.지라이슈필드_데이터;
import com.arms.api.jira.jiraissue.model.지라프로젝트_데이터;
import com.arms.api.jira.jiraissueresolution.model.지라이슈해결책_데이터;
import com.arms.api.jira.jiraissuestatus.model.지라이슈상태_데이터;
import com.arms.api.jira.jiraissuestatus.model.클라우드_지라이슈상태_데이터;
import com.arms.api.jira.jiraissuetype.model.지라이슈유형_데이터;
import com.arms.api.jira.jirapriority.model.지라이슈우선순위_데이터;
import com.arms.utils.지라유틸;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Worklog;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class 통합이슈조회테스트 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    com.arms.api.jira.jiraissue.service.지라이슈_전략_호출 지라이슈_전략_호출;

    String fieldsParam = "project,issuetype,creator,reporter,assignee,labels,priority,status,resolution,resolutiondate,created,worklogs,timespent,fixVersions";

    int 페이지_사이즈 = 10;

    // 온프레미스
    JiraRestClient restClient;

    private String o_url = "http://www.313.co.kr/jira";
    private String o_id = "admin";
    private String o_pass = "flexjava";
    public String o_projectKey = "JSTFFW";
    public String o_issueKey = "JSTFFW-124";
    public Long o_serverId = 313L;

    // 클라우드
    WebClient webClient;

    public String c_url = "https://ssoohyun.atlassian.net";
    public String c_id = "chltngus129@gmail.com";
    public String c_pass = "";
    public String c_projectKey = "T3";
    public String c_issueKey = "T3-5";
    public Long c_serverId = 1L;

    @BeforeEach
    void setUp () throws URISyntaxException {
        final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        restClient = factory.createWithBasicHttpAuthentication(new URI(o_url), o_id, o_pass);

        webClient = WebClient.builder()
                .baseUrl(c_url)
                .defaultHeader("Authorization", "Basic " + getBase64Credentials(c_id, c_pass))
                .build();
    }

    private String getBase64Credentials(String jiraID, String jiraPass) {
        String credentials = jiraID + ":" + jiraPass;
        return new String(Base64.getEncoder().encode(credentials.getBytes()));
    }

    @Test
    public void 클라우드_이슈조회_테스트() throws JsonProcessingException {

        지라이슈_데이터 지라이슈_데이터 = 클라우드_이슈조회();

        assertNotNull(지라이슈_데이터);
    }

    @Test
    public void 클라우드_전체이슈조회_테스트() throws JsonProcessingException {

        List<지라이슈_데이터> 지라이슈_목록 = 클라우드_전체이슈조회();

        assertNotNull(지라이슈_목록);
    }

    public 지라이슈_데이터 클라우드_이슈조회() throws JsonProcessingException {

        //String endpoint = "/rest/api/3/engine?jql=issue=" + c_issueKey + "&fields=" + fieldsParam;
        String endpoint = "/rest/api/3/issue/" + c_issueKey + "?fields=" + fieldsParam;
        
        로그.info(endpoint);

        지라이슈_데이터 지라이슈_데이터 = webClient.get()
                .uri(endpoint)
                .retrieve()
                .bodyToMono(지라이슈_데이터.class)
                .block();

        지라이슈_데이터.getFields().setWorklogs(클라우드_이슈워크로그조회(c_issueKey));
        
        String json = objectMapper.writeValueAsString(지라이슈_데이터);

        로그.info(json);

        return 지라이슈_데이터;
    }

    public List<지라이슈워크로그_데이터> 클라우드_이슈워크로그조회(String issueKey) {

        int 검색_시작_지점 = 0;
        int 검색_최대_개수 = 50;
        boolean isLast = false;

        List<지라이슈워크로그_데이터> 지라이슈워크로그_목록 = new ArrayList<>();

        while (!isLast) {
            String endpoint = "/rest/api/3/issue/" + issueKey + "/worklog?startAt=" + 검색_시작_지점 + "&maxResults=" + 검색_최대_개수;

            지라이슈전체워크로그_데이터 지라이슈전체워크로그_데이터 = 지라유틸.get(webClient, endpoint, 지라이슈전체워크로그_데이터.class).block();

            지라이슈워크로그_목록.addAll(지라이슈전체워크로그_데이터.getWorklogs());

            if (지라이슈전체워크로그_데이터.getTotal() == 지라이슈워크로그_목록.size()) {
                isLast = true;
            } else {
                검색_시작_지점 += 검색_최대_개수;
            }
        }

        로그.info(지라이슈워크로그_목록.toString());

        return 지라이슈워크로그_목록;

    }

    public List<지라이슈_데이터> 클라우드_전체이슈조회() throws JsonProcessingException {

        int 검색_시작_지점 = 0;
        int 검색_최대_개수 = 50;
        boolean isLast = false;

        List<지라이슈_데이터> 지라이슈_목록 = new ArrayList<>();

        while (!isLast) {
            String endpoint = "/rest/api/3/search?jql=project=" + c_projectKey + "&fields=" + fieldsParam
                            + "&startAt=" + 검색_시작_지점 + "&maxResults=" + 검색_최대_개수;

            지라이슈조회_데이터 지라이슈조회_데이터 = 지라유틸.get(webClient, endpoint, 지라이슈조회_데이터.class).block();

            지라이슈_목록.addAll(지라이슈조회_데이터.getIssues());
            로그.info("이슈 개수: " + 지라이슈_목록.size());

            if (지라이슈조회_데이터.getTotal() == 지라이슈_목록.size()) {
                isLast = true;
            } else {
                검색_시작_지점 += 검색_최대_개수;
            }
        }

        for ( 지라이슈_데이터 이슈 : 지라이슈_목록 ) {
            이슈.getFields().setWorklogs(클라우드_이슈워크로그조회(이슈.getKey()));
        }

        String json = objectMapper.writeValueAsString(지라이슈_목록);

        로그.info(json);

        return 지라이슈_목록;
    }

    @Test
    public void 온프레미스_이슈조회_테스트() throws JsonProcessingException {

        지라이슈_데이터 지라이슈_데이터 = 온프레미스_이슈조회();

        assertNotNull(지라이슈_데이터);
    }
    
    @Test
    public void 온프레미스_전체이슈조회_테스트() throws ExecutionException, InterruptedException, JsonProcessingException {
        
        List<지라이슈_데이터> 지라이슈_목록 = 온프레미스_전체이슈조회();
        
        assertNotNull(지라이슈_목록);
    }

    public 지라이슈_데이터 온프레미스_이슈조회() throws JsonProcessingException {

        Issue 이슈 = restClient.getIssueClient().getIssue(o_issueKey).claim();

        지라이슈_데이터 지라이슈_데이터 = 이슈_데이터_변환(이슈);
        
        return 지라이슈_데이터;
    }

    public List<지라이슈_데이터> 온프레미스_전체이슈조회() throws ExecutionException, InterruptedException, JsonProcessingException {
        
        String jql = "project = " + o_projectKey;

        int 검색_시작_지점 = 0;
        int 검색_최대_개수 = 50;

        Set<String> fields = new HashSet<>(Arrays.asList("*all")); // 검색 필드

        // 이슈 건수가 1000이 넘을 때 이슈 조회를 위한 처리
        List<지라이슈_데이터> 지라이슈_목록 = new ArrayList<>();
        SearchResult 전체이슈조회;

        do {
            전체이슈조회 = restClient.getSearchClient()
                    .searchJql(jql, 검색_최대_개수, 검색_시작_지점, fields)
                    .get();
            for (Issue 이슈 : 전체이슈조회.getIssues()) {
                지라이슈_데이터 이슈데이터 = 이슈_데이터_변환(이슈);
                지라이슈_목록.add(이슈데이터);
            }
            검색_시작_지점 += 검색_최대_개수;
        } while (전체이슈조회.getTotal() > 검색_시작_지점);

        return 지라이슈_목록;
    }

    private 지라이슈_데이터 이슈_데이터_변환(Issue 이슈) throws JsonProcessingException {

        지라이슈_데이터 지라이슈_데이터 = new 지라이슈_데이터();
        지라이슈필드_데이터 지라이슈필드_데이터 = new 지라이슈필드_데이터();

        // 초기화
        지라프로젝트_데이터 프로젝트 = 지라프로젝트_데이터.builder().build();
        지라사용자_데이터 보고자 = 지라사용자_데이터.builder().build();
        지라사용자_데이터 담당자 = 지라사용자_데이터.builder().build();

        // 프로젝트
        if (이슈.getProject() != null) {

            프로젝트.setSelf(이슈.getProject().getSelf().toString());
            프로젝트.setId(이슈.getProject().getId().toString());
            프로젝트.setKey(이슈.getProject().getKey());
            프로젝트.setName(이슈.getProject().getName());

            지라이슈필드_데이터.setProject(프로젝트);
        }

        // 이슈 유형
        if (이슈.getIssueType() != null) {

            String 이슈유형_주소 = String.valueOf(이슈.getIssueType().getSelf());
            String 이슈유형_아이디 = String.valueOf(이슈.getIssueType().getId());
            String 이슈유형_이름 = 이슈.getIssueType().getName();
            String 이슈유형_내용 = 이슈.getIssueType().getDescription();

            지라이슈유형_데이터 이슈유형 = new 지라이슈유형_데이터();
            이슈유형.setSelf(이슈유형_주소);
            이슈유형.setId(이슈유형_아이디);
            이슈유형.setName(이슈유형_이름);
            이슈유형.setDescription(이슈유형_내용);
            // subtask, untranslatedName, hierarchyLevel

            지라이슈필드_데이터.setIssuetype(이슈유형);
        }

        // 생성자

        // 보고자
        if (이슈.getReporter() != null) {

            보고자.setAccountId(이슈.getReporter().getName());
            보고자.setEmailAddress(이슈.getReporter().getEmailAddress());

            지라이슈필드_데이터.setReporter(보고자);
        }

        // 담당자
        if (이슈.getAssignee() != null) {

            담당자.setAccountId(이슈.getAssignee().getName());
            담당자.setEmailAddress(이슈.getAssignee().getEmailAddress());

            지라이슈필드_데이터.setAssignee(담당자);
        }

        // 라벨
        if (이슈.getLabels() != null) {
            Set<String> 라벨_목록 = 이슈.getLabels();
            List<String> 이슈라벨 = new ArrayList<>(라벨_목록);
            지라이슈필드_데이터.setLabels(이슈라벨);
        }

        // 우선 순위
        if (이슈.getPriority() != null) {

            String 이슈우선순위_주소 = String.valueOf(이슈.getPriority().getSelf());
            String 이슈우선순위_아이디 = String.valueOf(이슈.getPriority().getId());
            String 이슈우선순위_이름 = 이슈.getPriority().getName();

            지라이슈우선순위_데이터 이슈우선순위 = new 지라이슈우선순위_데이터();
            이슈우선순위.setSelf(이슈우선순위_주소);
            이슈우선순위.setId(이슈우선순위_아이디);
            이슈우선순위.setName(이슈우선순위_이름);
            // description, isDefault

            지라이슈필드_데이터.setPriority(이슈우선순위);
        }

        // 상태 값
        if (이슈.getStatus() != null) {

            String 이슈상태_주소 = String.valueOf(이슈.getStatus().getSelf());
            String 이슈상태_아이디 = String.valueOf(이슈.getStatus().getId());
            String 이슈상태_이름 = 이슈.getStatus().getName();
            String 이슈상태_설명 =  이슈.getStatus().getDescription();

            지라이슈상태_데이터 이슈상태 = new 지라이슈상태_데이터();
            이슈상태.setSelf(이슈상태_주소);
            이슈상태.setId(이슈상태_아이디);
            이슈상태.setName(이슈상태_이름);
            이슈상태.setDescription(이슈상태_설명);

            지라이슈필드_데이터.setStatus(이슈상태);
        }

        // 해결책
        if (이슈.getResolution() != null) {

            String 이슈해결책_주소 = String.valueOf(이슈.getResolution().getSelf());
            String 이슈해결책_아이디 = String.valueOf(이슈.getResolution().getId());
            String 이슈해결책_이름 = 이슈.getResolution().getName();
            String 이슈해결책_설명 = 이슈.getResolution().getDescription();

            지라이슈해결책_데이터 이슈해결책 = new 지라이슈해결책_데이터();
            이슈해결책.setSelf(이슈해결책_주소);
            이슈해결책.setId(이슈해결책_아이디);
            이슈해결책.setName(이슈해결책_이름);
            이슈해결책.setDescription(이슈해결책_설명);
            // isDefault

            지라이슈필드_데이터.setResolution(이슈해결책);
        }

        // resolutiondate

        // created
        if (이슈.getCreationDate() != null) {
            String 이슈생성날짜 = String.valueOf(이슈.getCreationDate());
            지라이슈필드_데이터.setCreated(이슈생성날짜);
        }

        // worklogs
        if (이슈.getWorklogs() != null) {

            List<지라이슈워크로그_데이터> 이슈워크로그_목록 = new ArrayList<>();

            Iterable<Worklog> 전체이슈워크로그 = 이슈.getWorklogs();

            for (Worklog 워크로그 : 전체이슈워크로그) {
                지라사용자_데이터 작성자 = new 지라사용자_데이터();

                String 이슈워크로그_주소 = 워크로그.getSelf().toString();
                BasicUser 이슈워크로그_작성자 = 워크로그.getAuthor();
                String 이슈워크로그_작성자아이디 = 이슈워크로그_작성자.getName();
                //String 이슈워크로그_작성자이메일 = 이슈워크로그_작성자.getDisplayName().toString();
//                User 이슈워크로그_수정작성자 = (User) 워크로그.getUpdateAuthor();
//                String 이슈워크로그_수정작성자아이디 = 이슈워크로그_수정작성자.getName();
//                String 이슈워크로그_수정작성자이메일 = 이슈워크로그_수정작성자.getEmailAddress();
                BasicUser 이슈워크로그_수정작성자 = 워크로그.getUpdateAuthor();
                String 이슈워크로그_수정작성자아이디 = 이슈워크로그_수정작성자.getName();
                //String 이슈워크로그_수정작성자이메일 = 이슈워크로그_수정작성자.getDisplayName().toString();
                String 이슈워크로그_생성날짜 = 워크로그.getCreationDate().toString();
                String 이슈워크로그_수정날짜 = 워크로그.getUpdateDate().toString();
                String 이슈워크로그_시작날짜 = 워크로그.getStartDate().toString();
                Integer 이슈워크로그_소요시간 = 워크로그.getMinutesSpent() * 60;

                지라이슈워크로그_데이터 이슈워크로그 = new 지라이슈워크로그_데이터();
                이슈워크로그.setSelf(이슈워크로그_주소);

                // BasicUser 타입에서는 이메일을 받아오지 않아서 어떻게 처리할지 고민중...
                작성자.setAccountId(이슈워크로그_작성자아이디);
                //작성자.setEmailAddress(이슈워크로그_작성자이메일);
                이슈워크로그.setAuthor(작성자);

                작성자.setAccountId(이슈워크로그_수정작성자아이디);
                //작성자.setEmailAddress(이슈워크로그_수정작성자이메일);
                이슈워크로그.setUpdateAuthor(작성자);

                이슈워크로그.setCreated(이슈워크로그_생성날짜);
                이슈워크로그.setUpdated(이슈워크로그_수정날짜);
                이슈워크로그.setStarted(이슈워크로그_시작날짜);
                이슈워크로그.setTimeSpentSeconds(이슈워크로그_소요시간);
                // timespent, id, issueId

                이슈워크로그_목록.add(이슈워크로그);
            }
            지라이슈필드_데이터.setWorklogs(이슈워크로그_목록);
        }

        // timespent
        if (이슈.getTimeTracking().getTimeSpentMinutes() != null) {
            Integer 이슈소요시간 = 이슈.getTimeTracking().getTimeSpentMinutes() * 60;
            지라이슈필드_데이터.setTimespent(이슈소요시간);
        }

        지라이슈_데이터.setFields(지라이슈필드_데이터);

        String json = objectMapper.writeValueAsString(지라이슈_데이터);

        로그.info(json);

        return 지라이슈_데이터;
    }

    @Test
    public void 클라우드_요구사항이슈_이슈링크_서브테스크_조회() throws Exception {

        List<지라이슈_데이터> 지라이슈_목록 = 연관된_지라이슈_목록(c_serverId, c_issueKey);

        int 전체_페이지_수 = 페이지_수(지라이슈_목록, 페이지_사이즈);

        if (전체_페이지_수 == 0) {
            로그.info("조회할 데이터가 없습니다.");
        }

        for (int 페이지_번호 = 1; 페이지_번호 <= 전체_페이지_수; 페이지_번호++) {
            로그.info(페이지_번호 + "번째 페이지: " + 페이징_지라이슈_목록(지라이슈_목록, 페이지_사이즈, 페이지_번호));
        }
    }

    @Test
    public void 온프레미스_요구사항이슈_이슈링크_서브테스크_조회() throws Exception {

        List<지라이슈_데이터> 지라이슈_목록 = 연관된_지라이슈_목록(o_serverId, o_issueKey);

        int 전체_페이지_수 = 페이지_수(지라이슈_목록, 페이지_사이즈);

        if (전체_페이지_수 == 0) {
            로그.info("조회할 데이터가 없습니다.");
        }

        for (int 페이지_번호 = 1; 페이지_번호 <= 전체_페이지_수; 페이지_번호++) {
            로그.info(페이지_번호 + "번째 페이지: " + 페이징_지라이슈_목록(지라이슈_목록, 페이지_사이즈, 페이지_번호));
        }
    }

    public int 페이지_수(List<지라이슈_데이터> 받아온_지라이슈_목록, int 페이지_사이즈) {

        if (받아온_지라이슈_목록.isEmpty()) {
            return 0;
        }

        return (int) Math.ceil((double) 받아온_지라이슈_목록.size()/페이지_사이즈);
    }

    public List<지라이슈_데이터> 페이징_지라이슈_목록(List<지라이슈_데이터> 받아온_지라이슈_목록, int 페이지_사이즈, int 페이지_번호) {

        int 조회_시작_지점 = (페이지_번호-1) * 페이지_사이즈;
        int 지라이슈_목록_개수 = 받아온_지라이슈_목록.size();

        if (조회_시작_지점 >= 지라이슈_목록_개수 || 조회_시작_지점 < 0) {
            로그.info("잘못된 페이지 번호입니다.");
        }

        return 받아온_지라이슈_목록.subList(조회_시작_지점, Math.min(조회_시작_지점+페이지_사이즈, 지라이슈_목록_개수));
    }

    public List<지라이슈_데이터> 연관된_지라이슈_목록(Long 지라서버_아이디, String 이슈_키) throws Exception {

        List<지라이슈_데이터> 연관된_지라이슈_목록 = new ArrayList<>();

        지라이슈_데이터 이슈 = 지라이슈_전략_호출.이슈_상세정보_가져오기(지라서버_아이디, 이슈_키);
        List<지라이슈_데이터> 이슈링크_목록 = 지라이슈_전략_호출.이슈링크_가져오기(지라서버_아이디, 이슈_키);
        List<지라이슈_데이터> 서브테스크_목록 = 지라이슈_전략_호출.서브테스크_가져오기(지라서버_아이디, 이슈_키);

        연관된_지라이슈_목록.add(이슈);
        연관된_지라이슈_목록.addAll(이슈링크_목록);
        연관된_지라이슈_목록.addAll(서브테스크_목록);

        return 연관된_지라이슈_목록;
    }

    @Test
    public void 클라우드_지라이슈상태_테스트() throws JsonProcessingException {

        List<지라이슈상태_데이터> 결과 = 지라이슈상태테스트("10004");

        if (결과.size() != 0) {
            System.out.println(결과.toString());
        }
        else {
            System.out.println(결과.size() + "ㅋㅋㅋ");
        }

        assertNotNull(결과);
    }

    public List<지라이슈상태_데이터> 지라이슈상태테스트(String 프로젝트_아이디) {
        int 최대_검색수 = 50;
        int startAt = 0;

        WebClient chanhoWeb = WebClient.builder()
                .baseUrl("https://testchkim.atlassian.net")
                .defaultHeader("Authorization", "Basic " + getBase64Credentials("gkfn185@gmail.com", "ATATT3xFfGF0WHBw5R_YdXGY-QBpFTJl5S6am7hqo7yWx1P0kOUdarkWcDaWDH-aYuJNQFL7LjUAA8MzArZyaXQQDeOGdKVjLyiFcuIBDR_FdKSD1UCFm3cXnJD7rHPPsxYe73OIMO5nuoP-mJDMy35zYoQiQGaW3DdW4Z64UoT2PeieuDz0UFE=1E941C6B"))
                .build();

        String endpoint = "/rest/api/3/statuses/search?maxResults=" + 최대_검색수 + "&startAt=" + startAt + "&projectId=" + 프로젝트_아이디;

        클라우드_지라이슈상태_데이터 지라이슈상태_조회_결과;

        try {
            지라이슈상태_조회_결과 = 지라유틸.get(chanhoWeb, endpoint, 클라우드_지라이슈상태_데이터.class).block();
        }
        catch (Exception e) {
            if (e instanceof WebClientResponseException) {
                WebClientResponseException wcException = (WebClientResponseException) e;
                HttpStatus status = wcException.getStatusCode();
                String body = wcException.getResponseBodyAsString();

                로그.error(status + " : " + body);
            }

            로그.error("클라우드 이슈 상태 목록 조회에 실패하였습니다" + e.getMessage());

            return Collections.emptyList();
            // throw new IllegalArgumentException(에러코드.이슈상태_조회_오류.getErrorMsg());
        }

        return 지라이슈상태_조회_결과.getValues();
    }
}
