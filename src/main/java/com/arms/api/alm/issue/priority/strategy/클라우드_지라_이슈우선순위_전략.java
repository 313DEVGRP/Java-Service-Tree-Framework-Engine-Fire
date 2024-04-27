package com.arms.api.alm.issue.priority.strategy;

import com.arms.api.alm.issue.priority.model.이슈우선순위_데이터;
import com.arms.api.alm.issue.priority.model.클라우드_지라이슈우선순위_데이터;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import com.arms.api.alm.utils.지라API_정보;
import com.arms.api.util.errors.codes.에러코드;
import com.arms.api.alm.serverinfo.service.서버정보_서비스;
import com.arms.api.alm.utils.지라유틸;

import com.arms.api.util.errors.에러로그_유틸;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class 클라우드_지라_이슈우선순위_전략 implements 이슈우선순위_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 서버정보_서비스 서버정보_서비스;

    @Autowired
    private 지라유틸 지라유틸;

    @Autowired
    private 지라API_정보 지라API_정보;

    @Override
    public List<이슈우선순위_데이터> 우선순위_목록_가져오기(Long 연결_아이디) throws Exception {

        로그.info("클라우드 지라 이슈 우선순위 전체 목록 가져오기");

        try {
            서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
            WebClient webClient = 지라유틸.클라우드_통신기_생성(서버정보.getUri(), 서버정보.getUserId(), 서버정보.getPasswordOrToken());

            int 최대_검색수 = 지라API_정보.getParameter().getMaxResults();
            int startAt = 0;
            boolean isLast = false;

            List<이슈우선순위_데이터> 반환할_이슈우선순위_데이터_목록 = new ArrayList<>();

            while(!isLast) {

                String endpoint = "/rest/api/3/priority/search?maxResults="+ 최대_검색수 + "&startAt=" + startAt;
                클라우드_지라이슈우선순위_데이터 클라우드_지라이슈우선순위_데이터 = 지라유틸.get(webClient, endpoint, 클라우드_지라이슈우선순위_데이터.class).block();

                if (클라우드_지라이슈우선순위_데이터 == null) {
                    로그.error("클라우드 지라 이슈 우선순위 전체 목록이 Null 입니다.");
                    throw new IllegalArgumentException(에러코드.이슈우선순위_조회_오류.getErrorMsg());
                }
                else if (클라우드_지라이슈우선순위_데이터.getValues() == null || 클라우드_지라이슈우선순위_데이터.getValues().size() == 0) {
                    로그.error("클라우드 지라 이슈 우선순위 전체 목록이 없습니다.");
                    throw new IllegalArgumentException(에러코드.이슈우선순위_조회_오류.getErrorMsg());
                }

                반환할_이슈우선순위_데이터_목록.addAll(클라우드_지라이슈우선순위_데이터.getValues());

                if (클라우드_지라이슈우선순위_데이터.getTotal() == 반환할_이슈우선순위_데이터_목록.size()) {
                    isLast = true;
                }
                else {
                    startAt += 최대_검색수;
                }
            }

            return 반환할_이슈우선순위_데이터_목록;

        } catch (Exception e) {
            String 에러로그 = 에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "클라우드 지라("+ 연결_아이디 +") :: 이슈우선순위_목록_가져오기에 실패하였습니다.");
            throw new IllegalArgumentException(에러코드.이슈우선순위_조회_오류.getErrorMsg() + " :: " + 에러로그);
        }
    }
}
