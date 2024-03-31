package com.arms.api.alm.account.service;

import com.arms.api.alm.account.model.계정정보_데이터;
import com.arms.api.alm.account.strategy.계정전략_등록_및_실행;
import com.arms.api.alm.account.strategy.온프레미스_레드마인_계정전략;
import com.arms.api.alm.account.strategy.온프레미스_지라_계정전략;
import com.arms.api.alm.account.strategy.클라우드_지라_계정전략;
import com.arms.api.serverinfo.model.enums.서버유형_정보;
import com.arms.api.serverinfo.model.서버정보_데이터;
import com.arms.api.serverinfo.service.서버정보_서비스;
import com.arms.utils.errors.codes.에러코드;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class 계정전략_호출 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private 계정전략_등록_및_실행 계정전략_등록_및_실행;

    private 클라우드_지라_계정전략 클라우드_지라_계정전략;

    private 온프레미스_지라_계정전략 온프레미스_지라_계정전략;

    private 온프레미스_레드마인_계정전략 온프레미스_레드마인_계정전략;

   서버정보_서비스 서버정보_서비스;


    @Autowired
    public 계정전략_호출(계정전략_등록_및_실행 계정전략_등록_및_실행,
                   클라우드_지라_계정전략 클라우드_지라_계정전략,
                   온프레미스_지라_계정전략 온프레미스_지라_계정전략,
                   온프레미스_레드마인_계정전략 온프레미스_레드마인_계정전략,
                   서버정보_서비스 서버정보_서비스) {

        this.계정전략_등록_및_실행 = 계정전략_등록_및_실행;
        this.클라우드_지라_계정전략 = 클라우드_지라_계정전략;
        this.온프레미스_지라_계정전략 = 온프레미스_지라_계정전략;
        this.온프레미스_레드마인_계정전략 = 온프레미스_레드마인_계정전략;
        this.서버정보_서비스 = 서버정보_서비스;
    }

    private 계정전략_등록_및_실행 계정_전략_확인(서버정보_데이터 서버정보) {

        if (서버정보 == null || 서버정보.getType().isEmpty()) {
            로그.error("계정 전략 등록 Error: 서버정보_유형 " + 에러코드.서버유형_정보오류.getErrorMsg());
            throw new IllegalArgumentException("계정 전략 등록 Error: 서버정보_유형 " + 에러코드.서버유형_정보오류.getErrorMsg());
        }

        서버유형_정보 서버_유형 = 서버유형_정보.valueOf(서버정보.getType());

        if (서버_유형 == 서버유형_정보.클라우드) {
            계정전략_등록_및_실행.계정_전략_등록(클라우드_지라_계정전략);
        }
        else if (서버_유형 == 서버유형_정보.온프레미스) {
            계정전략_등록_및_실행.계정_전략_등록(온프레미스_지라_계정전략);
        }
        else if (서버_유형 == 서버유형_정보.레드마인_온프레미스) {
            계정전략_등록_및_실행.계정_전략_등록(온프레미스_레드마인_계정전략);
        }

        return 계정전략_등록_및_실행;
    }

    public 계정정보_데이터 계정정보_검증하기(String uri,String serverType, String apiToken,String userId) throws Exception{


        if (uri == null) {
            로그.error("계정 정보 검증하기 Error: 서버주소 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("계정 정보 가져오기  Error: 서버주소 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        if (serverType == null) {
            로그.error("계정 정보 검증하기 Error: 서버타입 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("계정 정보 가져오기  Error: 서버타입 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        if (apiToken == null) {
            로그.error("계정 정보 검증하기 Error: 토큰정보 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("계정 정보 가져오기  Error: 토큰정보 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        if (userId == null) {
            로그.error("계정 정보 검증하기 Error: 유저_아이디 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("계정 정보 가져오기  Error: 유저_아이디 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        서버정보_데이터 서버정보 = new 서버정보_데이터();
        서버정보.setUri(uri);
        서버정보.setType(serverType);
        서버정보.setPasswordOrToken(apiToken);
        서버정보.setUserId(userId);

        계정전략_등록_및_실행 = 계정_전략_확인(서버정보);

        계정정보_데이터 조회된_계정정보 = 계정전략_등록_및_실행.계정정보_검증(서버정보);

        return 조회된_계정정보;

    }

    public 계정정보_데이터 계정정보_가져오기(Long 연결_아이디) throws Exception{

        if (연결_아이디 == null) {
            로그.error("계정 정보 가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("계정 정보 가져오기  Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
        }

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);

        계정전략_등록_및_실행 = 계정_전략_확인(서버정보);

        계정정보_데이터 조회된_계정정보 = 계정전략_등록_및_실행.계정정보_가져오기(연결_아이디);

        return 조회된_계정정보;

    }

//    public void 계정권한_가져오기(Long 연결_아이디) throws Exception{
//
//        if (연결_아이디 == null) {
//            로그.error("이슈 전체 목록 가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
//            throw new IllegalArgumentException("이슈 전체 목록 가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
//        }
//
//        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
//
//
//        계정전략_등록_및_실행 = 계정_전략_확인(서버정보);
//
//    }
}
