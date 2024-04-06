package com.arms.api.alm.account.strategy;

import com.arms.api.alm.account.model.계정정보_데이터;
import com.arms.api.alm.account.model.레드마인_유저_데이터;
import com.arms.api.alm.utils.레드마인API_정보;
import com.arms.api.alm.utils.레드마인유틸;
import com.arms.api.alm.utils.지라유틸;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import com.arms.api.alm.serverinfo.service.서버정보_서비스;
import com.arms.api.utils.errors.codes.에러코드;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class 온프레미스_레드마인_계정전략 implements 계정전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private final 서버정보_서비스 서버정보_서비스;

    private final 레드마인유틸 레드마인유틸;

    private final 레드마인API_정보 레드마인API_정보;

    @Autowired
    public 온프레미스_레드마인_계정전략(서버정보_서비스 서버정보_서비스, 레드마인유틸 레드마인유틸, 레드마인API_정보 레드마인API_정보) {
        this.서버정보_서비스 = 서버정보_서비스;
        this.레드마인유틸 = 레드마인유틸;
        this.레드마인API_정보 = 레드마인API_정보;
    }

    @Override
    public 계정정보_데이터 계정정보_검증(서버정보_데이터 서버정보) {
        로그.info("레드마인_온프레미스_계정_전략 :: 계정정보_검증");

        return 계정정보_조회(서버정보);
    }

    @Override
    public 계정정보_데이터 계정정보_가져오기(Long 연결_아이디) {
        로그.info("레드마인_온프레미스_계정_전략 :: 계정정보_가져오기");

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);

        return 계정정보_조회(서버정보);
    }

    private 계정정보_데이터 계정정보_조회(서버정보_데이터  서버정보) {

        try{
            String uri = 서버정보.getUri();
            String serverType = 서버정보.getType();
            String apiToken = 서버정보.getPasswordOrToken();
            String userId = 서버정보.getUserId();

            로그.info("레드마인_온프레미스_계정_전략 :: 계정정보_조회, 서버 주소: {}, 서버 타입: {}, apiToken: {}, 유저 아이디: {}",uri,serverType,apiToken,userId);

            String endpoint = "/my/account.json";

            WebClient webClient = 레드마인유틸.레드마인_웹클라이언트_통신기_생성(uri,apiToken);

            레드마인_유저_데이터 계정정보_조회결과 = 지라유틸.get(webClient, endpoint, 레드마인_유저_데이터.class).block();

            if (계정정보_조회결과 == null) {
                로그.error("온프라미스 레드마인 계정 조회 결과가 Null입니다.");
                throw new IllegalArgumentException(에러코드.계정정보_조회_오류.getErrorMsg());
            }else{
                return  계정정보_데이터_변환(계정정보_조회결과 , uri);
            }
        }
        catch (Exception e){
            로그.error("온프라미스 레드마인 계정 정보 조회시 오류가 발생하였습니다." + e.getMessage());
            throw new IllegalArgumentException(에러코드.계정정보_조회_오류.getErrorMsg());
        }

    }

    private 계정정보_데이터 계정정보_데이터_변환(레드마인_유저_데이터 계정정보_조회결과,String uri){

        계정정보_데이터 계정정보_데이터 = new 계정정보_데이터();

        String self = uri+"users/"+계정정보_조회결과.getUser().getId()+".json";
        계정정보_데이터.setSelf(self);
        계정정보_데이터.setName(계정정보_조회결과.getUser().getLogin());
        계정정보_데이터.setDisplayName(계정정보_조회결과.getUser().getLastname());
        계정정보_데이터.setEmailAddress(계정정보_조회결과.getUser().getMail());
        계정정보_데이터.setAdmin(계정정보_조회결과.getUser().getAdmin());
        계정정보_데이터.setApi_key(계정정보_조회결과.getUser().getApi_key());

        return 계정정보_데이터;
    }

}
