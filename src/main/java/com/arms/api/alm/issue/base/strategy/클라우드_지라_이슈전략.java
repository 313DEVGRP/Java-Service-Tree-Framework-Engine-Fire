package com.arms.api.alm.issue.base.strategy;

import com.arms.api.alm.issue.base.model.*;
import com.arms.api.alm.utils.지라API_정보;
import com.arms.api.alm.utils.지라유틸;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import com.arms.api.alm.serverinfo.service.서버정보_서비스;
import com.arms.api.utils.errors.codes.에러코드;
import com.arms.api.utils.errors.에러로그_유틸;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Component
public class 클라우드_지라_이슈전략 implements 이슈전략 {
    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private final 서버정보_서비스 서버정보_서비스;
    private final ModelMapper modelMapper;
    private final 지라유틸 지라유틸;
    private final 지라API_정보 지라API_정보;

    @Autowired
    public 클라우드_지라_이슈전략(서버정보_서비스 서버정보_서비스, ModelMapper modelMapper, 지라유틸 지라유틸, 지라API_정보 지라API_정보) {
        this.서버정보_서비스 = 서버정보_서비스;
        this.modelMapper = modelMapper;
        this.지라유틸 = 지라유틸;
        this.지라API_정보 = 지라API_정보;
    }

    @Override
    public List<지라이슈_데이터> 이슈_목록_가져오기(Long 연결_아이디, String 프로젝트_키_또는_아이디) {

        로그.info("클라우드 이슈 전체 조회");

        if (프로젝트_키_또는_아이디==null || 프로젝트_키_또는_아이디.isEmpty()) {
            throw new IllegalArgumentException(에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        try {
            int 검색_시작_지점 = 0;
            int 최대_검색수 = 지라API_정보.getParameter().getMaxResults();
            boolean isLast = false;

            서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
            WebClient webClient = 지라유틸.클라우드_통신기_생성(서버정보.getUri(), 서버정보.getUserId(), 서버정보.getPasswordOrToken());

            List<지라이슈_데이터> 프로젝트_이슈_목록 = new ArrayList<>();

            while (!isLast) {
                String endpoint = "/rest/api/3/search?jql=project=" + 프로젝트_키_또는_아이디
                        + "&startAt=" + 검색_시작_지점 + "&maxResults=" + 최대_검색수
                        + "&" + 지라API_정보.getParameter().getFields();

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
            String 에러로그 = 에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
            "클라우드 지라("+ 연결_아이디 +") :: 프로젝트("+ 프로젝트_키_또는_아이디+ ") :: 전체이슈_목록_가져오기에 실패하였습니다.");
            throw new IllegalArgumentException(에러코드.이슈_조회_오류.getErrorMsg() + " :: " + 에러로그);
        }
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
            String 에러로그 = 에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "클라우드 지라("+ 연결_아이디 +") :: 이슈 키("+ 이슈_키_또는_아이디+ ") :: 이슈_수정하기에 실패하였습니다.");
            throw new IllegalArgumentException(에러코드.이슈수정_오류.getErrorMsg() + " :: " + 에러로그);
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
            String 에러로그 = 에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "클라우드 지라("+ 연결_아이디 +") :: 이슈 키("+ 이슈_키_또는_아이디+ ") :: 이슈_삭제 라벨 처리하기에 실패하였습니다.");
            throw new IllegalArgumentException(에러코드.이슈수정_오류.getErrorMsg() + " :: " + 에러로그);
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
            에러로그_유틸.예외로그출력(e, this.getClass().getName(), "사용자_정보_조회");
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

    @Override
    public 지라이슈_데이터 이슈_상세정보_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) {

        로그.info("클라우드 지라 이슈_상세정보_가져오기");
        로그.info("지라서버 아이디 : {}, 이슈 키 : {}", 연결_아이디, 이슈_키_또는_아이디);

        if (이슈_키_또는_아이디==null || 이슈_키_또는_아이디.isEmpty()) {
            throw new IllegalArgumentException(에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        try {
            String endpoint = "/rest/api/3/issue/" + 이슈_키_또는_아이디 + "?" + 지라API_정보.getParameter().getFields();

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
            에러로그_유틸.예외로그출력(e, this.getClass().getName(),
                    "클라우드 지라("+ 연결_아이디 +") :: 이슈 키("+ 이슈_키_또는_아이디+ ") :: 이슈_상세정보_가져오기에 실패하였습니다.");
            return null;
        }
    }

    public List<지라이슈_데이터> 이슈링크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) {

        로그.info("클라우드 이슈 링크 가져오기");

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
        WebClient webClient = 지라유틸.클라우드_통신기_생성(서버정보.getUri(), 서버정보.getUserId(), 서버정보.getPasswordOrToken());

        int 검색_시작_지점 = 0;
        int 최대_검색수 = 지라API_정보.getParameter().getMaxResults();
        boolean isLast = false;

        List<지라이슈_데이터> 이슈링크_목록 = new ArrayList<>(); // 이슈 저장

        try {
            while (!isLast) {
                String endpoint = "/rest/api/3/search?jql=issue in linkedIssues(" + 이슈_키_또는_아이디 + ")&" + 지라API_정보.getParameter().getFields()
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
            에러로그_유틸.예외로그출력(e, this.getClass().getName(),
                    "클라우드 지라("+ 연결_아이디 +") :: 이슈 키("+ 이슈_키_또는_아이디+ ") :: 링크드 이슈 조회에 실패하였습니다.");
            return null;
        }
    }

    public List<지라이슈_데이터> 서브테스크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) {

        로그.info("클라우드 서브테스크 가져오기");
        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
        WebClient webClient = 지라유틸.클라우드_통신기_생성(서버정보.getUri(), 서버정보.getUserId(), 서버정보.getPasswordOrToken());

        int 검색_시작_지점 = 0;
        int 최대_검색수 = 지라API_정보.getParameter().getMaxResults();
        boolean isLast = false;

        List<지라이슈_데이터> 서브테스크_목록 = new ArrayList<>(); // 이슈 저장

        try {
            while (!isLast) {
                String endpoint = "/rest/api/3/search?jql=parent=" + 이슈_키_또는_아이디 +
                        "&" + 지라API_정보.getParameter().getFields() +
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
            에러로그_유틸.예외로그출력(e, this.getClass().getName(),
                    "클라우드 지라("+ 연결_아이디 +") :: 이슈 키("+ 이슈_키_또는_아이디+ ") :: 서브테스크_가져오기에 실패하였습니다.");
            return null;
        }
    }

    public List<지라이슈워크로그_데이터> 이슈_워크로그_조회(WebClient webClient, String 이슈_키_또는_아이디) {

        int 검색_시작_지점 = 0;
        int 최대_검색수 = 지라API_정보.getParameter().getMaxResults();
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

    public 지라이슈_데이터 증분이슈_상세정보_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) {

        if (이슈_키_또는_아이디==null || 이슈_키_또는_아이디.isEmpty()) {
            throw new IllegalArgumentException(에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        로그.info("지라서버 아이디 : {}, 이슈 키 : {}", 연결_아이디, 이슈_키_또는_아이디);

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
        WebClient webClient = 지라유틸.클라우드_통신기_생성(서버정보.getUri(), 서버정보.getUserId(), 서버정보.getPasswordOrToken());

        int 검색_시작_지점 = 0;
        int 최대_검색수 = 지라API_정보.getParameter().getMaxResults();
        boolean isLast = false;

        List<지라이슈_데이터> 이슈상세_목록 = new ArrayList<>();

        try {
            while (!isLast) {
                String detailEndpoint = 지라API_정보.getEndpoint().getIssue().getIncrement().getDetail();
                String endpoint = 지라API_정보.이슈키_대체하기(detailEndpoint, 이슈_키_또는_아이디)
                        + "&startAt=" + 검색_시작_지점 + "&maxResults=" + 최대_검색수;

                지라이슈조회_데이터 이슈상세정보_조회결과
                        = 지라유틸.get(webClient, endpoint, 지라이슈조회_데이터.class).block();

                if (이슈상세정보_조회결과 == null) {
                    return null;
                }
                else if (이슈상세정보_조회결과.getIssues() == null || 이슈상세정보_조회결과.getIssues().size() == 0) {
                    return null;
                }

                이슈상세_목록.addAll(이슈상세정보_조회결과.getIssues());

                if (이슈상세정보_조회결과.getTotal() == 이슈상세_목록.size()) {
                    isLast = true;
                } else {
                    검색_시작_지점 += 최대_검색수;
                }
            }

            if (이슈상세_목록.size() == 1) {
                지라이슈_데이터 지라이슈_데이터 = 이슈상세_목록.get(0);
                return 지라이슈_데이터;
            }

            return null;
        }
        catch (Exception e) {
            에러로그_유틸.예외로그출력(e, this.getClass().getName(),
            "클라우드 지라("+ 연결_아이디 +") :: 이슈 키("+ 이슈_키_또는_아이디+ ") :: 증분이슈_상세정보_가져오기에 실패하였습니다.");
            return null;
        }
    }

    public List<지라이슈_데이터> 증분이슈링크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) {

        로그.info("클라우드 증분 이슈 링크 가져오기");
        로그.info("지라서버 아이디 : {}, 이슈 키 : {}", 연결_아이디, 이슈_키_또는_아이디);

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
        WebClient webClient = 지라유틸.클라우드_통신기_생성(서버정보.getUri(), 서버정보.getUserId(), 서버정보.getPasswordOrToken());

        int 검색_시작_지점 = 0;
        int 최대_검색수 = 지라API_정보.getParameter().getMaxResults();
        boolean isLast = false;

        List<지라이슈_데이터> 이슈링크_목록 = new ArrayList<>(); // 이슈 저장

        try {
            while (!isLast) {
                String linkedIssueEndpoint = 지라API_정보.getEndpoint().getIssue().getIncrement().getLinkedIssue();
                String endpoint = 지라API_정보.이슈키_대체하기(linkedIssueEndpoint, 이슈_키_또는_아이디)
                        + "&startAt=" + 검색_시작_지점 + "&maxResults=" + 최대_검색수;

                지라이슈조회_데이터 이슈링크_조회결과
                        = 지라유틸.get(webClient, endpoint, 지라이슈조회_데이터.class).block();

                // 증분 데이터가 없는 경우
                if (이슈링크_조회결과 == null) {
                    return null;
                }
                else if (이슈링크_조회결과.getIssues() == null || 이슈링크_조회결과.getIssues().size() == 0) {
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
            에러로그_유틸.예외로그출력(e, this.getClass().getName(),
                    "클라우드 지라("+ 연결_아이디 +") :: 이슈 키("+ 이슈_키_또는_아이디+ ") :: 증분_링크드이슈_가져오기에 실패하였습니다.");
            return null;
        }
    }

    public List<지라이슈_데이터> 증분서브테스크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) {

        로그.info("클라우드 증분 서브테스크 가져오기");
        로그.info("지라서버 아이디 : {}, 이슈 키 : {}", 연결_아이디, 이슈_키_또는_아이디);

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
        WebClient webClient = 지라유틸.클라우드_통신기_생성(서버정보.getUri(), 서버정보.getUserId(), 서버정보.getPasswordOrToken());

        int 검색_시작_지점 = 0;
        int 최대_검색수 = 지라API_정보.getParameter().getMaxResults();
        boolean isLast = false;

        List<지라이슈_데이터> 서브테스크_목록 = new ArrayList<>(); // 이슈 저장

        try {
            while (!isLast) {
                String subtaskEndpoint = 지라API_정보.getEndpoint().getIssue().getIncrement().getSubtask();
                String endpoint = 지라API_정보.이슈키_대체하기(subtaskEndpoint, 이슈_키_또는_아이디)
                        + "&startAt=" + 검색_시작_지점 + "&maxResults=" + 최대_검색수;

                지라이슈조회_데이터 서브테스크_조회결과
                        = 지라유틸.get(webClient, endpoint, 지라이슈조회_데이터.class).block();

                // 증분 데이터가 없는 경우
                if (서브테스크_조회결과 == null) {
                    return null;
                }
                else if (서브테스크_조회결과.getIssues() == null || 서브테스크_조회결과.getIssues().size() == 0) {
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
            에러로그_유틸.예외로그출력(e, this.getClass().getName(),
                    "클라우드 지라("+ 연결_아이디 +") :: 이슈 키("+ 이슈_키_또는_아이디+ ") :: 증분_서브테스크_가져오기에 실패하였습니다.");
            return null;
        }
    }

    @Override
    public 지라이슈_데이터 이슈_생성하기(Long 연결_아이디, 지라이슈생성_데이터 지라이슈생성_데이터) {

        로그.info("클라우드 지라 이슈 생성 :: {} :: {}", 연결_아이디, 지라이슈생성_데이터.toString());

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
        WebClient webClient = 지라유틸.클라우드_통신기_생성(서버정보.getUri(), 서버정보.getUserId(), 서버정보.getPasswordOrToken());

        지라이슈생성필드_데이터 이슈생성필드_데이터 = 지라이슈생성_데이터.getFields();

        if (이슈생성필드_데이터 == null) {
            throw new IllegalArgumentException(에러코드.요청본문_오류체크.getErrorMsg());
        }

        String 프로젝트_아이디 = "";
        String 이슈유형_아이디 = "";

        if (지라이슈생성_데이터.getFields().getProject() != null &&
                지라이슈생성_데이터.getFields().getProject().getId() != null &&
                !지라이슈생성_데이터.getFields().getProject().getId().isEmpty()) {
            프로젝트_아이디 = 지라이슈생성_데이터.getFields().getProject().getId();
        }

        if (지라이슈생성_데이터.getFields().getIssuetype() != null &&
                지라이슈생성_데이터.getFields().getIssuetype().getId() != null
                && !지라이슈생성_데이터.getFields().getIssuetype().getId().isEmpty()) {
            이슈유형_아이디 = 지라이슈생성_데이터.getFields().getIssuetype().getId();
        }

        if (프로젝트_아이디.isEmpty() || 이슈유형_아이디.isEmpty()) {
            throw new IllegalArgumentException("클라우드 지라("+ 연결_아이디 +") 이슈 생성 필드 확인에 필요한 프로젝트 아이디, 이슈유형 아이디가 존재 하지 않습니다.");
        }

        /* ***
         * 프로젝트 와 이슈 유형에 따라 이슈 생성 시 들어가는 fields의 내용을 확인하는 부분(현재 priority만 적용)
         *** */
        클라우드_지라이슈생성_데이터 입력_데이터 = new 클라우드_지라이슈생성_데이터();
        클라우드_지라이슈필드_데이터 클라우드_필드_데이터;

        Map<String, 클라우드_이슈생성필드_메타데이터.필드_메타데이터> 필드_메타데이터_목록
                = 지라유틸.필드_메타데이터_확인하기(webClient, 프로젝트_아이디, 이슈유형_아이디);
        클라우드_필드_데이터 = 필드검증_및_추가하기(webClient, 이슈생성필드_데이터, 필드_메타데이터_목록);

        입력_데이터.setFields(클라우드_필드_데이터);

        String endpoint = "/rest/api/3/issue";
        지라이슈_데이터 반환할_지라이슈_데이터;
        try {
            반환할_지라이슈_데이터 = 지라유틸.post(webClient, endpoint, 입력_데이터, 지라이슈_데이터.class).block();
            로그.info("클라우드 지라 프로젝트 : {}, 이슈유형 : {}, 생성 필드 : {}, 이슈 생성하기"
                    , 프로젝트_아이디, 이슈유형_아이디, 입력_데이터.toString());
        }
        catch (Exception e) {
            로그.error("클라우드 지라 프로젝트 : {}, 이슈유형 : {}, 생성 필드 : {}, 이슈 생성하기 중 오류"
                    , 프로젝트_아이디, 이슈유형_아이디, 입력_데이터.toString());
            String 에러로그 = 에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                "이슈 생성하기 클라우드 지라 프로젝트 :: "+프로젝트_아이디+ " :: 이슈유형 :: "+
                        이슈유형_아이디+ " :: 생성 필드 :: "+ 입력_데이터.toString() + ", 이슈 생성하기 중 오류");
            throw new IllegalArgumentException("이슈 생성 중 오류 :: " + 에러로그);
        }

        if (반환할_지라이슈_데이터 == null) {
            로그.error("이슈 생성에 실패하였습니다.");
            return null;
        }

        return 반환할_지라이슈_데이터;
    }

    public 클라우드_지라이슈필드_데이터 필드검증_및_추가하기(WebClient webClient,
                                       지라이슈생성필드_데이터 지라이슈생성필드_데이터,
                                       Map<String, 클라우드_이슈생성필드_메타데이터.필드_메타데이터> 필드_메타데이터_목록) {

        if (필드_메타데이터_목록 == null) {
            로그.error("필드 메타데이터 목록이 null입니다.");
            throw new IllegalArgumentException("필수필드 확인 및 추가 중 오류 :: 필드 메타데이터 목록이 null입니다.");
        }

        클라우드_지라이슈필드_데이터 클라우드_지라이슈필드_데이터 = new 클라우드_지라이슈필드_데이터();

        if (필드_메타데이터_목록.containsKey(지라API_정보.getFields().getProject())
                && 지라이슈생성필드_데이터.getProject() != null) {

            클라우드_지라이슈필드_데이터.setProject(지라이슈생성필드_데이터.getProject());
            필드_메타데이터_목록.remove(지라API_정보.getFields().getProject());
        }

        if (필드_메타데이터_목록.containsKey(지라API_정보.getFields().getIssuetype())
                && 지라이슈생성필드_데이터.getIssuetype() != null) {

            클라우드_지라이슈필드_데이터.setIssuetype(지라이슈생성필드_데이터.getIssuetype());
            필드_메타데이터_목록.remove(지라API_정보.getFields().getIssuetype());
        }

        if (필드_메타데이터_목록.containsKey(지라API_정보.getFields().getSummary())
                && 지라이슈생성필드_데이터.getSummary() != null) {

            클라우드_지라이슈필드_데이터.setSummary(지라이슈생성필드_데이터.getSummary());
            필드_메타데이터_목록.remove(지라API_정보.getFields().getSummary());
        }

        if (필드_메타데이터_목록.containsKey(지라API_정보.getFields().getDescription())
                && 지라이슈생성필드_데이터.getDescription() != null) {

            클라우드_지라이슈필드_데이터.setDescription(내용_변환(지라이슈생성필드_데이터.getDescription()));
            필드_메타데이터_목록.remove(지라API_정보.getFields().getDescription());
        }

        if (필드_메타데이터_목록.containsKey(지라API_정보.getFields().getReporter())) {
            /***
             * reporter 필드는 필수 여부와 상관없이 추가하지 않아도 API를 요청한 사용자로 자동으로 추가된다.
             ***/
            필드_메타데이터_목록.remove(지라API_정보.getFields().getReporter());
        }

        if (필드_메타데이터_목록.containsKey(지라API_정보.getFields().getPriority())
                && 지라이슈생성필드_데이터.getPriority() != null) {

            클라우드_지라이슈필드_데이터.setPriority(지라이슈생성필드_데이터.getPriority());
            필드_메타데이터_목록.remove(지라API_정보.getFields().getPriority());
        }

        /***
         * A-RMS에서 지원하는 필드 메타데이터 목록을 모두 remove 시킨 후 필수로 지정된 필드가 있을 경우 오류 반환
         ***/
        필드_메타데이터_목록.forEach((key, 필드_메타) -> {
            if (필드_메타.isRequired()) {
                로그.error("필드 메타데이터 목록 중 오류 :: {} :: {} 필드가 필수로 지정되어있습니다. A-RMS에서 지원하지 않는 필드입니다.",
                                지라이슈생성필드_데이터.toString(), key);
                throw new IllegalArgumentException("필수 필드 확인 및 추가 중 오류 :: " + key +
                        " 필드가 필수로 지정되어있습니다. A-RMS에서 지원하지 않는 필드입니다. :: \n" + 지라이슈생성필드_데이터.toString());
            }
        });

        return 클라우드_지라이슈필드_데이터;
    }
}
