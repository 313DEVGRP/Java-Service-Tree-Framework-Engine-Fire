package com.arms.api.alm.issue.status.strategy;

import com.arms.api.alm.issue.status.model.이슈상태_데이터;
import com.arms.api.alm.issue.status.model.클라우드_지라_이슈유형별_상태_데이터;
import com.arms.api.alm.issue.status.model.클라우드_지라이슈상태_데이터;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import com.arms.api.alm.utils.지라API_정보;
import com.arms.api.alm.utils.지라유틸;
import com.arms.api.util.errors.codes.에러코드;
import com.arms.api.util.errors.에러로그_유틸;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class 클라우드_지라_이슈상태_전략 implements 이슈상태_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private 지라유틸 지라유틸;
    private 지라API_정보 지라API_정보;

    @Autowired
    public 클라우드_지라_이슈상태_전략(지라유틸 지라유틸,
                           지라API_정보 지라API_정보) {
        this.지라유틸 = 지라유틸;
        this.지라API_정보 = 지라API_정보;
    }

    @Override
    public List<이슈상태_데이터> 이슈상태_목록_가져오기(서버정보_데이터 서버정보) throws Exception {

        try {
            WebClient webClient = 지라유틸.클라우드_통신기_생성(서버정보.getUri(), 서버정보.getUserId(), 서버정보.getPasswordOrToken());

            int startAt = 0;
            int 최대_검색수 = 지라API_정보.getParameter().getMaxResults();
            boolean checkLast = false;

            List<이슈상태_데이터> 반환할_이슈상태_데이터_목록 = new ArrayList<이슈상태_데이터>();

            while(!checkLast) {
                String endpoint = "/rest/api/3/statuses/search?maxResults="+ 최대_검색수 + "&startAt=" + startAt;
                클라우드_지라이슈상태_데이터 지라이슈상태_조회_결과 = 지라유틸.get(webClient, endpoint, 클라우드_지라이슈상태_데이터.class).block();

                if (지라이슈상태_조회_결과 == null) {
                    로그.error("클라우드 지라("+ 서버정보.getConnectId() +") :: 이슈상태_목록_가져오기에 실패하였습니다.");
                    return Collections.emptyList();
                }
                else if (지라이슈상태_조회_결과.getValues() == null || 지라이슈상태_조회_결과.getValues().size() == 0) {
                    로그.info("클라우드 지라("+ 서버정보.getConnectId() +") :: 이슈 상태 목록이 없습니다.");
                    return Collections.emptyList();
                }

                반환할_이슈상태_데이터_목록.addAll(지라이슈상태_조회_결과.getValues());

                for (이슈상태_데이터 이슈_상태 : 반환할_이슈상태_데이터_목록) {
                    String self = 서버정보.getUri() + "/rest/api/3/statuses?id=" + 이슈_상태.getId();
                    이슈_상태.setSelf(self);
                }

                if (지라이슈상태_조회_결과.getTotal() == 반환할_이슈상태_데이터_목록.size()) {
                    checkLast = true;
                }
                else {
                    startAt += 최대_검색수;
                }
            }

            return 반환할_이슈상태_데이터_목록;

        }
        catch (Exception e) {
            String 에러로그 = 에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "클라우드 지라("+ 서버정보.getConnectId() +") :: 이슈상태_목록_가져오기에 실패하였습니다.");
            throw new Exception(에러로그);
        }
    }

    @Override
    public List<이슈상태_데이터> 프로젝트별_이슈상태_목록_가져오기(서버정보_데이터 서버정보, String 프로젝트_아이디) throws Exception{

        if (프로젝트_아이디 == null || 프로젝트_아이디.isEmpty()) {
            throw new IllegalArgumentException(에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        try {
            WebClient webClient = 지라유틸.클라우드_통신기_생성(서버정보.getUri(), 서버정보.getUserId(), 서버정보.getPasswordOrToken());

            String endpoint = "/rest/api/3/project/" + 프로젝트_아이디 + "/statuses";
            List<클라우드_지라_이슈유형별_상태_데이터> 지라이슈상태_조회_결과 = 지라유틸.get(webClient, endpoint,
                                        new ParameterizedTypeReference<List<클라우드_지라_이슈유형별_상태_데이터>>() {}).block();

            if (지라이슈상태_조회_결과 == null || 지라이슈상태_조회_결과.size() == 0) {
                String 에러로그 = "클라우드 지라("+ 서버정보.getConnectId() +") :: 프로젝트("+ 프로젝트_아이디+ ") :: 프로젝트별_이슈상태_목록_가져오기에 실패하였습니다.";
                로그.error(에러로그);
                throw new Exception(에러로그);
            }

            Map<String, 이슈상태_데이터> 이슈상태_SELF_맵 = new HashMap<>();
            ObjectMapper objectMapper = new ObjectMapper();
            for (클라우드_지라_이슈유형별_상태_데이터 이슈유형 : 지라이슈상태_조회_결과) {
                if (이슈유형 == null || 이슈유형.getStatuses() == null) {
                    continue;
                }

                for (이슈상태_데이터 이슈상태 : 이슈유형.getStatuses()) {
                    if (이슈상태 == null || 이슈상태.getSelf() == null) {
                        continue;
                    }

                    이슈상태_데이터 기존데이터 = 이슈상태_SELF_맵.get(이슈상태.getSelf());
                    if (기존데이터 != null) {
                        String 이슈유형아이디_JSON = 기존데이터.getIssueTypeId();
                        if (이슈유형아이디_JSON != null) {
                            try {
                                Set<String> set = objectMapper.readValue(이슈유형아이디_JSON, new TypeReference<Set<String>>() {});
                                if (!set.contains(이슈유형.getId())) {
                                    set.add(이슈유형.getId());
                                    String 아이디_내림차순_문자열 = objectMapper.writeValueAsString(set);
                                    기존데이터.setIssueTypeId(아이디_내림차순_문자열);
                                    이슈상태_SELF_맵.put(이슈상태.getSelf(), 기존데이터);
                                }
                            }
                            catch (IOException e) {
                                로그.error(e.getMessage());
                            }
                        }
                    }
                    else {
                        try {
                            List<String> list = Collections.singletonList(이슈유형.getId());
                            이슈상태.setIssueTypeId(objectMapper.writeValueAsString(list));
                            이슈상태_SELF_맵.put(이슈상태.getSelf(), 이슈상태);
                        }
                        catch (IOException e) {
                            String 에러로그 = 에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                                                                            "프로젝트별_이슈상태_목록_가져오기");
                            로그.error(에러로그);
                        }
                    }
                }
            }

            return new ArrayList<>(이슈상태_SELF_맵.values());
        }
        catch (Exception e) {
            String 에러로그 = 에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "클라우드 지라("+ 서버정보.getConnectId() +") :: 프로젝트("+ 프로젝트_아이디+ ") :: 프로젝트별_이슈상태_목록_가져오기에 실패하였습니다.");
            throw new Exception(에러로그);
        }
    }

    private static 이슈상태_데이터 업데이트_이슈상태_데이터(이슈상태_데이터 기존데이터, 이슈상태_데이터 새로운데이터) {
        String 기존이슈상태아이디 = 기존데이터.getIssueTypeId();

        Set<String> set = new HashSet<>();
        if (기존이슈상태아이디 != null && !기존이슈상태아이디.isEmpty()) {
            set.addAll(Arrays.asList(기존이슈상태아이디.split("[\\[\\],\"]")));
        }

        set.add(새로운데이터.getIssueTypeId());

        String 아이디_내림차순_문자열 = set.stream()
                .sorted()
                .collect(Collectors.joining("\",\"", "[\"", "\"]"));

        기존데이터.setIssueTypeId(아이디_내림차순_문자열);

        return 기존데이터;
    }

    /*public List<이슈상태_데이터> 프로젝트별_이슈상태_목록_가져오기2(서버정보_데이터 서버정보, String 프로젝트_아이디) throws Exception{

        if (프로젝트_아이디 == null || 프로젝트_아이디.isEmpty()) {
            throw new IllegalArgumentException(에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        try {
            WebClient webClient = 지라유틸.클라우드_통신기_생성(서버정보.getUri(), 서버정보.getUserId(), 서버정보.getPasswordOrToken());

            int startAt = 0;
            int 최대_검색수 = 지라API_정보.getParameter().getMaxResults();
            boolean checkLast = false;

            List<이슈상태_데이터> 반환할_이슈상태_데이터_목록 = new ArrayList<이슈상태_데이터>();

            while(!checkLast) {
                // String endpoint = "/rest/api/3/statuses/search?maxResults="+ 최대_검색수 + "&startAt=" + startAt + "&projectId="+프로젝트_아이디;
                String endpoint = "/rest/api/3/" + 프로젝트_아이디 + "/statuses";
                클라우드_지라이슈상태_데이터 지라이슈상태_조회_결과 = 지라유틸.get(webClient, endpoint, 클라우드_지라이슈상태_데이터.class).block();

                if (지라이슈상태_조회_결과 == null) {
                    로그.error("클라우드 지라("+ 서버정보.getConnectId() +") :: 프로젝트("+ 프로젝트_아이디+ ") :: 프로젝트별_이슈상태_목록_가져오기에 실패하였습니다.");
                    return Collections.emptyList();
                }
                else if (지라이슈상태_조회_결과.getValues() == null || 지라이슈상태_조회_결과.getValues().size() == 0) {
                    로그.info("클라우드 지라("+ 서버정보.getConnectId() +") :: 프로젝트("+ 프로젝트_아이디+ ") :: 이슈상태 목록이 없습니다.");
                    return Collections.emptyList();
                }

                반환할_이슈상태_데이터_목록.addAll(지라이슈상태_조회_결과.getValues());

                for (이슈상태_데이터 이슈_상태 : 반환할_이슈상태_데이터_목록) {
                    String self = 서버정보.getUri() + "/rest/api/3/statuses?id=" + 이슈_상태.getId();
                    이슈_상태.setSelf(self);
                }

                if (지라이슈상태_조회_결과.getTotal() == 반환할_이슈상태_데이터_목록.size()) {
                    checkLast = true;
                }
                else {
                    startAt += 최대_검색수;
                }
            }

            return 반환할_이슈상태_데이터_목록;

        }
        catch (Exception e) {
            에러로그_유틸.예외로그출력(e, this.getClass().getName(),
                    "클라우드 지라("+ 서버정보.getConnectId() +") :: 프로젝트("+ 프로젝트_아이디+ ") :: 프로젝트별_이슈상태_목록_가져오기에 실패하였습니다.");
            return Collections.emptyList();
        }
    }*/
}
