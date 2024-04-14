package com.arms.api.alm.issue.status.strategy;

import com.arms.api.alm.issue.status.model.이슈상태_데이터;
import com.arms.api.alm.issue.status.model.클라우드_지라이슈상태_데이터;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import com.arms.api.alm.utils.지라API_정보;
import com.arms.api.utils.errors.codes.에러코드;
import com.arms.api.alm.serverinfo.service.서버정보_서비스;
import com.arms.api.alm.utils.지라유틸;


import com.arms.api.utils.errors.에러로그_유틸;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    public List<이슈상태_데이터> 이슈상태_목록_가져오기(서버정보_데이터 서버정보) {

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
            에러로그_유틸.예외로그출력(e, this.getClass().getName(),
                    "클라우드 지라("+ 서버정보.getConnectId() +") :: 이슈상태_목록_가져오기에 실패하였습니다.");
            return Collections.emptyList();
        }
    }


    @Override
    public List<이슈상태_데이터> 프로젝트별_이슈상태_목록_가져오기(서버정보_데이터 서버정보, String 프로젝트_아이디) throws Exception{

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
                String endpoint = "/rest/api/3/statuses/search?maxResults="+ 최대_검색수 + "&startAt=" + startAt + "&projectId="+프로젝트_아이디;
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
    }

}
