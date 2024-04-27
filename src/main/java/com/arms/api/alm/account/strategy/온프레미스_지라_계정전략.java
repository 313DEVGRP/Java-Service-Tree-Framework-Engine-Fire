package com.arms.api.alm.account.strategy;

import com.arms.api.alm.account.model.계정정보_데이터;
import com.arms.api.alm.utils.지라API_정보;
import com.arms.api.alm.utils.지라유틸;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import com.arms.api.alm.serverinfo.service.서버정보_서비스;
import com.arms.api.util.errors.codes.에러코드;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class 온프레미스_지라_계정전략 implements 계정전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private final 서버정보_서비스 서버정보_서비스;

    private final 지라유틸 지라유틸;

    private final 지라API_정보 지라API_정보;

    @Autowired
    public 온프레미스_지라_계정전략(서버정보_서비스 서버정보_서비스, 지라유틸 지라유틸, 지라API_정보 지라API_정보) {
        this.서버정보_서비스 = 서버정보_서비스;
        this.지라유틸 = 지라유틸;
        this.지라API_정보 = 지라API_정보;
    }

    @Override
    public 계정정보_데이터 계정정보_검증(서버정보_데이터 서버정보){
        로그.info("온프라미스_지라_계정전략 :: 계정정보_검증");
        return 계정정보_조회(서버정보);
    }

    @Override
    public 계정정보_데이터 계정정보_가져오기(Long 연결_아이디){
        로그.info("온프라미스_지라_계정전략 :: 계정정보_가져오기, 연결_아이디: {}", 연결_아이디);

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
        return 계정정보_조회(서버정보);
    }

    private 계정정보_데이터 계정정보_조회(서버정보_데이터 서버정보) {
        try {
            String uri = 서버정보.getUri();
            String serverType = 서버정보.getType();
            String apiToken = 서버정보.getPasswordOrToken();
            String userId = 서버정보.getUserId();

            로그.info("온프라미스_지라_계정전략 :: 계정정보_조회, 서버 주소: {}, 서버 타입: {}, apiToken: {}, 유저 아이디: {}",uri,serverType,apiToken,userId);

            JiraRestClient restClient = 지라유틸.온프레미스_통신기_생성(uri, userId, apiToken);
            User 계정정보_조회결과 = restClient.getUserClient().getUser(userId).claim();

            if (계정정보_조회결과 == null) {
                로그.error("온프라미스 지라 계정 조회 결과가 Null입니다.");
                throw new IllegalArgumentException(에러코드.계정정보_조회_오류.getErrorMsg());
            }

            return 계정정보_데이터로_변환(계정정보_조회결과);

        } catch (Exception e) {
            로그.error("온프라미스 계정 정보 조회시 오류가 발생하였습니다." + e.getMessage());
            throw new IllegalArgumentException(에러코드.계정정보_조회_오류.getErrorMsg());
        }
    }

    private 계정정보_데이터 계정정보_데이터로_변환(User 계정정보_조회결과){
        계정정보_데이터 계정정보_데이터 = new 계정정보_데이터();
        계정정보_데이터.setSelf(String.valueOf(계정정보_조회결과.getSelf()));
        계정정보_데이터.setName(계정정보_조회결과.getName());
        계정정보_데이터.setEmailAddress(계정정보_조회결과.getEmailAddress());
        계정정보_데이터.setDisplayName(계정정보_조회결과.getDisplayName());
        계정정보_데이터.setActive(계정정보_조회결과.isActive());
        return 계정정보_데이터;
    }


}
