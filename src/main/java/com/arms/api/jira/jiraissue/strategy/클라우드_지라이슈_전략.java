package com.arms.api.jira.jiraissue.strategy;

import com.arms.api.jira.jiraissue.model.지라사용자_데이터;
import com.arms.api.jira.jiraissue.model.지라이슈워크로그_데이터;
import com.arms.api.jira.jiraissue.model.클라우드_지라이슈필드_데이터;
import com.arms.api.jira.jiraissue.model.지라이슈_데이터;
import com.arms.api.jira.jiraissue.model.지라이슈생성_데이터;
import com.arms.api.jira.jiraissue.model.지라이슈생성필드_데이터;
import com.arms.api.jira.jiraissue.model.지라이슈전체워크로그_데이터;
import com.arms.api.jira.jiraissue.model.지라이슈조회_데이터;
import com.arms.api.jira.jiraissue.model.클라우드_지라이슈생성_데이터;
import com.arms.api.serverinfo.model.서버정보_데이터;
import com.arms.errors.codes.에러코드;
import com.arms.api.serverinfo.service.서버정보_서비스;
import com.arms.utils.지라유틸;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.*;

@Component
public class 클라우드_지라이슈_전략 implements 지라이슈_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 서버정보_서비스 서버정보_서비스;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private 지라유틸 지라유틸;

    @Override
    public List<지라이슈_데이터> 이슈_목록_가져오기(Long 연결_아이디, String 프로젝트_키_또는_아이디) {

        로그.info("클라우드 이슈 전체 조회");

        if (프로젝트_키_또는_아이디==null || 프로젝트_키_또는_아이디.isEmpty()) {
            throw new IllegalArgumentException(에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        try {
            int 검색_시작_지점 = 0;
            int 최대_검색수 = 지라유틸.최대_검색수_가져오기();
            boolean isLast = false;

            서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
            WebClient webClient = 지라유틸.클라우드_통신기_생성(서버정보.getUri(), 서버정보.getUserId(), 서버정보.getPasswordOrToken());

            List<지라이슈_데이터> 프로젝트_이슈_목록 = new ArrayList<>();

            while (!isLast) {
                String endpoint = "/rest/api/3/search?jql=project=" + 프로젝트_키_또는_아이디
                                + "&startAt=" + 검색_시작_지점 + "&maxResults=" + 최대_검색수
                                + "&" + 지라유틸.조회할_필드_목록_가져오기();

                지라이슈조회_데이터 프로젝트_이슈_검색결과 = 지라유틸.get(webClient, endpoint, 지라이슈조회_데이터.class).block();

                if (프로젝트_이슈_검색결과 == null) {
                    로그.error("클라우드 지라 프로젝트에 이슈 목록이 Null입니다.");
                    throw new IllegalArgumentException(에러코드.이슈_조회_오류.getErrorMsg());
                }
                else if (프로젝트_이슈_검색결과.getIssues() == null || 프로젝트_이슈_검색결과.getIssues().size() == 0) {
                    로그.error("클라우드 지라 프로젝트에 이슈 목록이 없습니다.");
                    throw new IllegalArgumentException(에러코드.이슈_조회_오류.getErrorMsg());
                }

                프로젝트_이슈_목록.addAll(프로젝트_이슈_검색결과.getIssues());

                if (프로젝트_이슈_검색결과.getTotal() == 프로젝트_이슈_목록.size()) {
                    isLast = true;
                } else {
                    검색_시작_지점 += 최대_검색수;
                }
            }

            for (지라이슈_데이터 지라이슈 : 프로젝트_이슈_목록) {
                지라이슈.getFields().setWorklogs(이슈_워크로그_조회(webClient, 지라이슈.getKey()));
            }

            return 프로젝트_이슈_목록;
        } catch (Exception e) {
            로그.error("클라우드 이슈 전체 조회시 오류가 발생하였습니다."+e.getMessage());
            throw new IllegalArgumentException(에러코드.이슈_조회_오류.getErrorMsg());

        }
    }

    @Override
    public 지라이슈_데이터 이슈_상세정보_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) {

        로그.info("클라우드 지라 이슈 조회하기");

        if (이슈_키_또는_아이디==null || 이슈_키_또는_아이디.isEmpty()) {
            throw new IllegalArgumentException(에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        try {
            String endpoint = "/rest/api/3/issue/" + 이슈_키_또는_아이디 + "?" + 지라유틸.조회할_필드_목록_가져오기();

            서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
            WebClient webClient = 지라유틸.클라우드_통신기_생성(서버정보.getUri(), 서버정보.getUserId(), 서버정보.getPasswordOrToken());

            지라이슈_데이터 지라이슈_데이터 = 지라유틸.get(webClient, endpoint, 지라이슈_데이터.class).block();

            if(지라이슈_데이터 != null && 지라이슈_데이터.getFields() != null) {
                지라이슈_데이터.getFields().setWorklogs(이슈_워크로그_조회(webClient, 이슈_키_또는_아이디));
            } else {
                로그.error("이슈 정보 가져오기에 실패하였습니다. 조회 대상 정보 확인이 필요합니다.");
                return null;
            }

            로그.info(지라이슈_데이터.toString());

            return 지라이슈_데이터;
        } catch (Exception e) {
            로그.error("이슈 정보 가져오기에 실패하였습니다. 조회 대상 정보 확인이 필요합니다.");
            return null;
        }
    }

    /* ***
     * 수정사항: null 체크하여 에러 처리 필요
     *** */
    @Override
    public 지라이슈_데이터 이슈_생성하기(Long 연결_아이디, 지라이슈생성_데이터 지라이슈생성_데이터) throws JsonProcessingException {

        로그.info("클라우드 지라 이슈 생성하기");

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
        WebClient webClient = 지라유틸.클라우드_통신기_생성(서버정보.getUri(), 서버정보.getUserId(), 서버정보.getPasswordOrToken());


        지라이슈생성필드_데이터 필드_데이터 = 지라이슈생성_데이터.getFields();

        if (필드_데이터 == null) {
            throw new IllegalArgumentException(에러코드.요청본문_오류체크.getErrorMsg());
        }

        클라우드_지라이슈생성_데이터 입력_데이터 = new 클라우드_지라이슈생성_데이터();
        클라우드_지라이슈필드_데이터 클라우드_필드_데이터 = new 클라우드_지라이슈필드_데이터();

        if (필드_데이터.getProject() != null) {
            클라우드_필드_데이터.setProject(필드_데이터.getProject());
        }
        if (필드_데이터.getIssuetype() != null) {
            클라우드_필드_데이터.setIssuetype(필드_데이터.getIssuetype());
        }
        if (필드_데이터.getSummary() != null) {
            클라우드_필드_데이터.setSummary(필드_데이터.getSummary());
        }

        if (필드_데이터.getDescription() != null) {
            클라우드_필드_데이터.setDescription(내용_변환((String) 필드_데이터.getDescription()));
        }

        지라사용자_데이터 사용자 = 사용자_정보_조회(webClient);

        if (사용자 == null) {
            로그.info("이슈 생성 필드 확인에 필요한 사용자 데이터가 Null입니다.");
        }
        else {
            클라우드_필드_데이터.setReporter(사용자);
        }

        /* ***
        * 프로젝트 와 이슈 유형에 따라 이슈 생성 시 들어가는 fields의 내용을 확인하는 부분(현재 priority만 적용)
        *** */
        String 프로젝트_아이디 = "";
        String 이슈유형_아이디 = "";

        if (지라이슈생성_데이터.getFields().getProject().getId() != null
                    && !지라이슈생성_데이터.getFields().getProject().getId().isEmpty()) {
            프로젝트_아이디 = 지라이슈생성_데이터.getFields().getProject().getId();
        }

        if (지라이슈생성_데이터.getFields().getIssuetype().getId() != null
                    && !지라이슈생성_데이터.getFields().getIssuetype().getId().isEmpty()) {
            이슈유형_아이디 = 지라이슈생성_데이터.getFields().getIssuetype().getId();
        }

        if (프로젝트_아이디.isEmpty() || 이슈유형_아이디.isEmpty()) {
            throw new IllegalArgumentException("이슈 생성 필드 확인에 필요한 프로젝트 아이디, 이슈유형 아이디가 존재 하지 않습니다.");
        }

        String 이슈생성_필드확인_지점 = "/rest/api/3/issue/createmeta?expand=projects.issuetypes.fields&projectIds="+ 프로젝트_아이디 +"&issuetypeIds=" +이슈유형_아이디;

        Map<String, Object> 반환할_이슈생성_필드 = 지라유틸.get(webClient, 이슈생성_필드확인_지점, Map.class).block();
        List<Map<String, Object>> 프로젝트_목록 = null;

        if(반환할_이슈생성_필드 != null && 반환할_이슈생성_필드.containsKey("projects") && 반환할_이슈생성_필드.get("projects") != null) {
            프로젝트_목록 = (List<Map<String, Object>>) 반환할_이슈생성_필드.get("projects");
        } else {
            throw new IllegalArgumentException("이슈 생성 필드 확인 중 프로젝트 목록에 문제가있습니다.");
        }

        if (프로젝트_목록.size() != 1) {
            throw new IllegalArgumentException("이슈 생성 필드 확인 중 프로젝트 목록에 문제가있습니다.");
        }

        List<Map<String, Object>> 이슈유형 = null;

        if(프로젝트_목록 != null && !프로젝트_목록.isEmpty() && 프로젝트_목록.get(0) != null) {
            Map<String, Object> 프로젝트 = 프로젝트_목록.get(0);
            if(프로젝트.containsKey("issuetypes") && 프로젝트.get("issuetypes") != null) {
                이슈유형 = (List<Map<String, Object>>) 프로젝트.get("issuetypes");
            } else {
                throw new IllegalArgumentException("이슈 생성 필드 확인 중 이슈유형 목록에 문제가있습니다.");
            }
        } else {
            throw new IllegalArgumentException("이슈 생성 필드 확인 중 이슈유형 목록에 문제가있습니다.");
        }

        Map<String, Object> 필드 = null;

        if(이슈유형 != null && !이슈유형.isEmpty() && 이슈유형.get(0) != null) {
            Map<String, Object> firstIssueType = 이슈유형.get(0);
            if(firstIssueType.containsKey("fields") && firstIssueType.get("fields") != null) {
                필드 = (Map<String, Object>) firstIssueType.get("fields");
            } else {
                throw new IllegalArgumentException("이슈 생성 필드 확인 중 fields 목록에 문제가있습니다.");
            }
        } else {
            throw new IllegalArgumentException("이슈 생성 필드 확인 중 fields 목록에 문제가있습니다.");
        }

        boolean 우선순위_유무 = 필드.containsKey("priority");

        if (우선순위_유무 && 필드_데이터.getPriority() != null) {
            클라우드_필드_데이터.setPriority(필드_데이터.getPriority());
        }

        입력_데이터.setFields(클라우드_필드_데이터);
        ObjectMapper objectMapper = new ObjectMapper();
        로그.info(objectMapper.writeValueAsString(입력_데이터));

        String endpoint = "/rest/api/3/issue";
        지라이슈_데이터 반환할_지라이슈_데이터 = null;
        try {
            반환할_지라이슈_데이터 = 지라유틸.post(webClient, endpoint, 입력_데이터, 지라이슈_데이터.class).block();
        } catch (Exception e) {
            if (e instanceof WebClientResponseException) {
                WebClientResponseException wcException = (WebClientResponseException) e;
                HttpStatus status = wcException.getStatusCode();
                String body = wcException.getResponseBodyAsString();

                로그.error(status + " : " + body);
            }
        }

        if (반환할_지라이슈_데이터 == null) {
            로그.error("이슈 생성에 실패하였습니다.");
            return null;
        }

        // DB 저장 ELK로 변경
//        지라_이슈_엔티티 지라_이슈_엔티티 = modelMapper.map(반환할_지라이슈_데이터, 지라_이슈_엔티티.class);
//        지라_이슈_엔티티.setConnectId(연결_아이디);
//        지라_이슈_저장소.save(지라_이슈_엔티티);

        return 반환할_지라이슈_데이터;
    }

    @Override
    public Map<String, Object> 이슈_수정하기(Long 연결_아이디, String 이슈_키_또는_아이디, 지라이슈생성_데이터 지라이슈생성_데이터) {

        로그.info("클라우드 지라 이슈 수정하기");

        if (이슈_키_또는_아이디==null || 이슈_키_또는_아이디.isEmpty()) {
            throw new IllegalArgumentException(에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        try {
            서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
            WebClient webClient = 지라유틸.클라우드_통신기_생성(서버정보.getUri(), 서버정보.getUserId(), 서버정보.getPasswordOrToken());

            String endpoint = "/rest/api/3/issue/" + 이슈_키_또는_아이디;
            Map<String, Object> 결과 = new HashMap<>();

            지라이슈생성필드_데이터 필드_데이터 = 지라이슈생성_데이터.getFields();

            if (필드_데이터.getReporter() != null || 필드_데이터.getAssignee() != null
                    || 필드_데이터.getStatus() != null || 필드_데이터.getResolution() != null) {

                로그.info("입력 값에 수정할 수 없는 필드가 있습니다.");

                결과.put("이슈 수정", "실패");
                결과.put("에러 메시지", "수정할 수 없는 필드가 포함");

                return 결과;
            }

            클라우드_지라이슈생성_데이터 수정_데이터 = new 클라우드_지라이슈생성_데이터();
            클라우드_지라이슈필드_데이터 클라우드_필드_데이터 = new 클라우드_지라이슈필드_데이터();

            if (필드_데이터.getSummary() != null) {
                클라우드_필드_데이터.setSummary(필드_데이터.getSummary());
            }

            if (필드_데이터.getDescription() != null) {
                클라우드_필드_데이터.setDescription(내용_변환(필드_데이터.getDescription()));
            }

            if (필드_데이터.getLabels() != null) {
                클라우드_필드_데이터.setLabels(필드_데이터.getLabels());
            }

            수정_데이터.setFields(클라우드_필드_데이터);
            Optional<Boolean> 응답_결과 = 지라유틸.executePut(webClient, endpoint, 수정_데이터);

            if (응답_결과.isPresent()) {
                if (응답_결과.get()) {

                    결과.put("success", true);
                    결과.put("message", "이슈 수정 성공");

                    return 결과;
                }
            }
            결과.put("success", false);
            결과.put("message", "이슈 수정 실패");

            return 결과;
        } catch (Exception e) {
            로그.error("이슈 수정시 오류가 발생하였습니다. "+e.getMessage());
            throw new IllegalArgumentException(에러코드.이슈수정_오류.getErrorMsg());
        }

    }

    @Override
    public Map<String, Object> 이슈_삭제_라벨_처리하기(Long 연결_아이디, String 이슈_키_또는_아이디) {

        로그.info("지라 이슈 삭제 라벨 처리하기");
        try {
            Map<String, Object> 반환할_결과맵 = new HashMap<String, Object>();

            String 삭제_라벨링 = "이슈_삭제_라벨_처리";

            지라이슈생성필드_데이터 필드_데이터 = new 지라이슈생성필드_데이터();
            필드_데이터.setLabels(List.of(삭제_라벨링));

            지라이슈생성_데이터 지라이슈생성_데이터 = new 지라이슈생성_데이터();
            지라이슈생성_데이터.setFields(필드_데이터);

            Map<String, Object> 라벨_처리_결과맵 = 이슈_수정하기(연결_아이디, 이슈_키_또는_아이디, 지라이슈생성_데이터);

            if (!((Boolean) 라벨_처리_결과맵.get("success"))) {
                반환할_결과맵.put("success", false);
                반환할_결과맵.put("message", "이슈 라벨 닫기 처리 실패");
            }
            else {
                반환할_결과맵.put("success", true);
                반환할_결과맵.put("message", "이슈 라벨 닫기 처리 성공");
            }

            return 반환할_결과맵;
        } catch (Exception e) {
            throw new IllegalArgumentException(에러코드.이슈수정_오류.getErrorMsg());
        }
    }

    public 클라우드_지라이슈필드_데이터.내용 내용_변환(String 입력_데이터) {

        클라우드_지라이슈필드_데이터.콘텐츠_아이템 콘텐츠_아이템 = 클라우드_지라이슈필드_데이터.콘텐츠_아이템.builder()
                .text(입력_데이터)
                .type("text")
                .build();

        List<클라우드_지라이슈필드_데이터.콘텐츠_아이템> 콘텐츠_아이템_리스트 = new ArrayList<>();
        콘텐츠_아이템_리스트.add(콘텐츠_아이템);

        클라우드_지라이슈필드_데이터.콘텐츠 콘텐츠 = 클라우드_지라이슈필드_데이터.콘텐츠.builder()
                .content(콘텐츠_아이템_리스트)
                .type("paragraph")
                .build();

        List<클라우드_지라이슈필드_데이터.콘텐츠> 콘텐츠_리스트 = new ArrayList<>();
        콘텐츠_리스트.add(콘텐츠);

        클라우드_지라이슈필드_데이터.내용 내용 = 클라우드_지라이슈필드_데이터.내용.builder()
                .content(콘텐츠_리스트)
                .type("doc")
                .version(1)
                .build();

        return 내용;
    }

    public 지라사용자_데이터 사용자_정보_조회(WebClient webClient) {

        String endpoint = "/rest/api/3/myself";

        지라사용자_데이터 사용자_정보 = null;
        try {
            사용자_정보 = 지라유틸.get(webClient, endpoint, 지라사용자_데이터.class).block();
        } catch (Exception e) {
            if (e instanceof WebClientResponseException) {
                WebClientResponseException wcException = (WebClientResponseException) e;
                HttpStatus status = wcException.getStatusCode();
                String body = wcException.getResponseBodyAsString();

                로그.error(status + " : " + body);
            }
        }

        지라사용자_데이터 사용자 = null;
        if (사용자_정보 != null) {
            사용자 = 사용자_정보_설정(사용자_정보);
        }

        return 사용자;
    }

    public 지라사용자_데이터 사용자_정보_설정(지라사용자_데이터 사용자_정보) {

        return 지라사용자_데이터.builder()
                .accountId(사용자_정보.getAccountId())
                .emailAddress(사용자_정보.getEmailAddress())
                .displayName(사용자_정보.getDisplayName())
                .build();
    }

    public List<지라이슈_데이터> 이슈링크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) {

        로그.info("클라우드 이슈 링크 가져오기");

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
        WebClient webClient = 지라유틸.클라우드_통신기_생성(서버정보.getUri(), 서버정보.getUserId(), 서버정보.getPasswordOrToken());

        int 검색_시작_지점 = 0;
        int 최대_검색수 = 50;
        boolean isLast = false;

        List<지라이슈_데이터> 이슈링크_목록 = new ArrayList<>(); // 이슈 저장

        try {
            while (!isLast) {
                String endpoint = "/rest/api/3/search?jql=issue in linkedIssues(" + 이슈_키_또는_아이디 + ")&" + 지라유틸.조회할_필드_목록_가져오기()
                        + "&startAt=" + 검색_시작_지점 + "&maxResults=" + 최대_검색수;

                지라이슈조회_데이터 이슈링크_조회결과
                        = 지라유틸.get(webClient, endpoint, 지라이슈조회_데이터.class).block();

                if (이슈링크_조회결과 == null) {
                    로그.info("클라우드 지라 연결된 이슈 목록이 Null입니다.");
                    return null;
                }
                else if (이슈링크_조회결과.getIssues() == null || 이슈링크_조회결과.getIssues().size() == 0) {
                    로그.info("클라우드 지라 연결된 이슈 목록이 없습니다.");
                    return null;
                }

                이슈링크_목록.addAll(이슈링크_조회결과.getIssues());

                if (이슈링크_조회결과.getTotal() == 이슈링크_목록.size()) {
                    isLast = true;
                } else {
                    검색_시작_지점 += 최대_검색수;
                }
            }

            return 이슈링크_목록;
        }
        catch (Exception e) {
            로그.error("클라우드에서 조회하려는 이슈 키의 연결된 이슈 정보 가져오기에 실패하였습니다. 조회 대상 정보 확인이 필요합니다.");
            return null;
        }
    }

    public List<지라이슈_데이터> 서브테스크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) {

        로그.info("클라우드 서브테스크 가져오기");
        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
        WebClient webClient = 지라유틸.클라우드_통신기_생성(서버정보.getUri(), 서버정보.getUserId(), 서버정보.getPasswordOrToken());

        int 검색_시작_지점 = 0;
        int 최대_검색수 = 50;
        boolean isLast = false;

        List<지라이슈_데이터> 서브테스크_목록 = new ArrayList<>(); // 이슈 저장

        try {
            while (!isLast) {
                String endpoint = "/rest/api/3/search?jql=parent=" + 이슈_키_또는_아이디 +
                        "&" + 지라유틸.조회할_필드_목록_가져오기() +
                        "&startAt=" + 검색_시작_지점 + "&maxResults=" + 최대_검색수;

                지라이슈조회_데이터 서브테스크_조회결과
                        = 지라유틸.get(webClient, endpoint, 지라이슈조회_데이터.class).block();

                if (서브테스크_조회결과 == null) {
                    로그.info("클라우드 지라 서브테스크 이슈 목록이 Null입니다.");
                    return null;
                }
                else if (서브테스크_조회결과.getIssues() == null || 서브테스크_조회결과.getIssues().size() == 0) {
                    로그.info("클라우드 지라 서브테스크 이슈 목록이 없습니다.");
                    return null;
                }

                서브테스크_목록.addAll(서브테스크_조회결과.getIssues());

                if (서브테스크_조회결과.getTotal() == 서브테스크_목록.size()) {
                    isLast = true;
                } else {
                    검색_시작_지점 += 최대_검색수;
                }
            }

            return 서브테스크_목록;
        }
        catch (Exception e) {
            로그.error("클라우드에서 조회하려는 이슈 키의 서브테스크 정보 가져오기에 실패하였습니다. 조회 대상 정보 확인이 필요합니다.");
            return null;
        }
    }

    public List<지라이슈워크로그_데이터> 이슈_워크로그_조회(WebClient webClient, String 이슈_키_또는_아이디) {

        int 검색_시작_지점 = 0;
        int 최대_검색수 = 지라유틸.최대_검색수_가져오기();
        boolean isLast = false;

        List<지라이슈워크로그_데이터> 지라이슈워크로그_목록 = new ArrayList<>();

        while (!isLast) {
            String endpoint = "/rest/api/3/issue/" + 이슈_키_또는_아이디 + "/worklog?startAt=" + 검색_시작_지점 + "&maxResults=" + 최대_검색수;

            지라이슈전체워크로그_데이터 지라이슈워크로그_조회_결과 = 지라유틸.get(webClient, endpoint, 지라이슈전체워크로그_데이터.class).block();

            if (지라이슈워크로그_조회_결과 == null) {
                return Collections.emptyList();
            }
            else if (지라이슈워크로그_조회_결과.getWorklogs() == null || 지라이슈워크로그_조회_결과.getWorklogs().size() == 0) {
                return Collections.emptyList();
            }

            지라이슈워크로그_목록.addAll(지라이슈워크로그_조회_결과.getWorklogs());

            if (지라이슈워크로그_조회_결과.getTotal() == 지라이슈워크로그_목록.size()) {
                isLast = true;
            } else {
                검색_시작_지점 += 최대_검색수;
            }
        }

        return 지라이슈워크로그_목록;
    }

}
