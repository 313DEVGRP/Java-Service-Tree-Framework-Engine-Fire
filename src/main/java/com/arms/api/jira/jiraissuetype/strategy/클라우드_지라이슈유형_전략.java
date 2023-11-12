package com.arms.api.jira.jiraissuetype.strategy;

import com.arms.utils.지라유틸;
import com.arms.errors.codes.에러코드;
import com.arms.api.jira.jiraissuetype.model.지라이슈유형_데이터;
import com.arms.api.serverinfo.model.서버정보_데이터;
import com.arms.api.serverinfo.service.서버정보_서비스;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;
import java.util.List;

@Component
public class 클라우드_지라이슈유형_전략 implements 지라이슈유형_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 서버정보_서비스 서버정보_서비스;

    @Override
    public List<지라이슈유형_데이터> 이슈유형_목록_가져오기(Long 연결_아이디) {
        로그.info("클라우드 지라 이슈유형_목록_가져오기");
        try {
            String endpoint = "/rest/api/3/issuetype";

            서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
            WebClient webClient = 지라유틸.클라우드_통신기_생성(서버정보.getUri(), 서버정보.getUserId(), 서버정보.getPasswordOrToken());

            List<지라이슈유형_데이터> 반환할_이슈_유형_목록
                                        = 지라유틸.get(webClient, endpoint,
                                        new ParameterizedTypeReference<List<지라이슈유형_데이터>>() {}).block();

            로그.info(반환할_이슈_유형_목록.toString());

            return 반환할_이슈_유형_목록;

        } catch (Exception e) {
            로그.error("클라우드 지라 이슈 유형 목록 가져오기 가져오기에 실패하였습니다.");
            로그.error(e.getClass().getName() + " : "+ e.getMessage());

            if (e instanceof WebClientResponseException) {
                WebClientResponseException wcException = (WebClientResponseException) e;
                HttpStatus status = wcException.getStatusCode();
                String body = wcException.getResponseBodyAsString();

                로그.error(status + " : " + body);
            }

            return Collections.emptyList();
        }
    }

    @Override
    public List<지라이슈유형_데이터> 프로젝트별_이슈유형_목록_가져오기(Long 연결_아이디, String 프로젝트_아이디) {

        로그.info("클라우드 지라 프로젝트 아이디("+ 프로젝트_아이디 +")별_이슈유형_목록_가져오기");

        if (프로젝트_아이디 == null || 프로젝트_아이디.isEmpty()) {
            throw new IllegalArgumentException(에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        try {
            String endpoint = "/rest/api/3/issuetype/project?projectId=" + 프로젝트_아이디;

            서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
            WebClient webClient = 지라유틸.클라우드_통신기_생성(서버정보.getUri(), 서버정보.getUserId(), 서버정보.getPasswordOrToken());

            List<지라이슈유형_데이터> 반환할_이슈_유형_목록
                    = 지라유틸.get(webClient, endpoint,
                            new ParameterizedTypeReference<List<지라이슈유형_데이터>>() {}).block();

            로그.info(반환할_이슈_유형_목록.toString());

            return 반환할_이슈_유형_목록;

        } catch (Exception e) {
            로그.error("클라우드 지라 프로젝트 아이디("+ 프로젝트_아이디 +")별_이슈유형_목록_가져오기에 실패하였습니다.");
            로그.error(e.getClass().getName() + " : "+ e.getMessage());

            if (e instanceof WebClientResponseException) {
                WebClientResponseException wcException = (WebClientResponseException) e;
                HttpStatus status = wcException.getStatusCode();
                String body = wcException.getResponseBodyAsString();

                로그.error(status + " : " + body);
            }

            return Collections.emptyList();
        }
    }
}
