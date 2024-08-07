package com.arms.api.alm.project.service;

import com.arms.api.alm.project.model.프로젝트_데이터;
import com.arms.api.alm.project.strategy.*;
import com.arms.api.alm.serverinfo.model.enums.서버유형_정보;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import com.arms.api.alm.serverinfo.service.서버정보_서비스;
import com.arms.api.util.errors.codes.에러코드;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class 프로젝트_전략_호출 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private 서버정보_서비스 서버정보_서비스;
    private 클라우드_지라_프로젝트_전략 클라우드_지라프로젝트_전략;
    private 온프레미스_지라_프로젝트_전략 온프레미스_지라프로젝트_전략;
    private 레드마인_온프레미스_프로젝트_전략 레드마인_온프레미스_프로젝트_전략;

    @Autowired
    public 프로젝트_전략_호출(서버정보_서비스 서버정보_서비스,
                          클라우드_지라_프로젝트_전략 클라우드_지라프로젝트_전략,
                          온프레미스_지라_프로젝트_전략 온프레미스_지라프로젝트_전략,
                          레드마인_온프레미스_프로젝트_전략 레드마인_온프레미스_프로젝트_전략) {

        this.서버정보_서비스 = 서버정보_서비스;
        this.클라우드_지라프로젝트_전략 = 클라우드_지라프로젝트_전략;
        this.온프레미스_지라프로젝트_전략 = 온프레미스_지라프로젝트_전략;
        this.레드마인_온프레미스_프로젝트_전략 = 레드마인_온프레미스_프로젝트_전략;

    }

    private 프로젝트_전략 프로젝트_전략_확인(서버정보_데이터 서버정보) {

        if (서버정보 == null || 서버정보.getType().isEmpty()) {
            로그.error("프로젝트 전략 등록 Error: 서버정보_유형 " + 에러코드.서버유형_정보오류.getErrorMsg());
            throw new IllegalArgumentException("프로젝트 전략 등록 Error: 서버정보_유형 " + 에러코드.서버유형_정보오류.getErrorMsg());
        }

        서버유형_정보 서버_유형 = 서버유형_정보.valueOf(서버정보.getType());

        프로젝트_전략 프로젝트_전략;
        if (서버_유형 == 서버유형_정보.클라우드) {
            프로젝트_전략 = this.클라우드_지라프로젝트_전략;
        }
        else if (서버_유형 == 서버유형_정보.온프레미스) {
            프로젝트_전략 = this.온프레미스_지라프로젝트_전략;
        }
        else if (서버_유형 == 서버유형_정보.레드마인_온프레미스) {
            프로젝트_전략 = this.레드마인_온프레미스_프로젝트_전략;
        }
        else {
            throw new IllegalArgumentException("프로젝트 전략 확인 Error: 허용하지 않는 서버정보_유형입니다. :: "+ 서버_유형+ " :: " + 에러코드.서버유형_정보오류.getErrorMsg());
        }

        return 프로젝트_전략;

    }

    public 프로젝트_데이터 프로젝트_상세정보_가져오기(Long 연결_아이디, String 프로젝트_키_또는_아이디) throws Exception {

        if (연결_아이디 == null) {
            로그.error("프로젝트 상세 정보 가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("프로젝트 상세 정보 가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
        }

        if (프로젝트_키_또는_아이디 == null || 프로젝트_키_또는_아이디.isEmpty()) {
            로그.error("프로젝트 상세 정보 가져오기 Error 프로젝트_키_또는_아이디 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("프로젝트 상세 정보 가져오기 Error 프로젝트_키_또는_아이디 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);

        프로젝트_전략 프로젝트_전략 = 프로젝트_전략_확인(서버정보);
        프로젝트_전략_등록_및_실행 프로젝트_전략_등록_및_실행 = new 프로젝트_전략_등록_및_실행();
        프로젝트_전략_등록_및_실행.프로젝트_전략_등록(프로젝트_전략);

        프로젝트_데이터 반환할_프로젝트_데이터
                = 프로젝트_전략_등록_및_실행.프로젝트_상세정보_가져오기(서버정보, 프로젝트_키_또는_아이디);

        return 반환할_프로젝트_데이터;
    }

    public List<프로젝트_데이터> 프로젝트_목록_가져오기(Long 연결_아이디) throws Exception {

        if (연결_아이디 == null) {
            로그.error("프로젝트 전체 목록 가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("프로젝트 전체 목록 가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
        }

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);

        프로젝트_전략 프로젝트_전략 = 프로젝트_전략_확인(서버정보);
        프로젝트_전략_등록_및_실행 프로젝트_전략_등록_및_실행 = new 프로젝트_전략_등록_및_실행();
        프로젝트_전략_등록_및_실행.프로젝트_전략_등록(프로젝트_전략);

        List<프로젝트_데이터> 반환할_지라프로젝트_목록
                = 프로젝트_전략_등록_및_실행.프로젝트_목록_가져오기(서버정보);

        return 반환할_지라프로젝트_목록;

    }
}
