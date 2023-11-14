package com.arms.api.jira.jiraissueresolution.strategy;

import com.arms.api.jira.jiraissueresolution.model.지라이슈해결책_데이터;
import com.arms.api.jira.jiraissueresolution.model.클라우드_지라이슈해결책_데이터;
import com.arms.api.serverinfo.model.서버정보_데이터;
import com.arms.errors.codes.에러코드;
import com.arms.api.serverinfo.service.서버정보_서비스;
import com.arms.utils.지라유틸;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class 클라우드_지라이슈해결책_전략 implements 지라이슈해결책_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 서버정보_서비스 서버정보_서비스;

    @Autowired
    private 지라유틸 지라유틸;

    @Override
    public List<지라이슈해결책_데이터> 이슈해결책_목록_가져오기(Long 연결_아이디) {

        로그.info("클라우드 지라 이슈해결책_목록_가져오기");

        try {
            서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
            WebClient webClient = 지라유틸.클라우드_통신기_생성(서버정보.getUri(), 서버정보.getUserId(), 서버정보.getPasswordOrToken());

            int startAt = 0;
            int 최대_검색수 = 지라유틸.최대_검색수_가져오기();
            boolean checkLast = false;

            List<지라이슈해결책_데이터> 반환할_지라이슈해결책_데이터_목록 = new ArrayList<지라이슈해결책_데이터>();

            while(!checkLast) {
                String endpoint = "/rest/api/3/resolution/search?maxResults="+ 최대_검색수 + "&startAt=" + startAt;
                클라우드_지라이슈해결책_데이터 resolutions = 지라유틸.get(webClient, endpoint,
                                                            클라우드_지라이슈해결책_데이터.class).block();

                반환할_지라이슈해결책_데이터_목록.addAll(resolutions.getValues());

                if (resolutions.getTotal() == 반환할_지라이슈해결책_데이터_목록.size()) {
                    checkLast = true;
                }
                else {
                    startAt += 최대_검색수;
                }
            }

            return 반환할_지라이슈해결책_데이터_목록;

        } catch (Exception e) {
            로그.error("클라우드 지라 이슈 해결책 목록 조회에 실패하였습니다."+e.getMessage());
            throw new IllegalArgumentException(에러코드.이슈해결책_조회_오류.getErrorMsg());
        }
    }

}
