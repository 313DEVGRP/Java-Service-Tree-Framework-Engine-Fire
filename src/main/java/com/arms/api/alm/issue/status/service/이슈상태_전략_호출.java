package com.arms.api.alm.issue.status.service;

import com.arms.api.alm.issue.status.model.이슈상태_데이터;
import com.arms.api.alm.issue.status.strategy.온프레미스_레드마인_이슈상태_전략;
import com.arms.api.alm.issue.status.strategy.온프레미스_지라_이슈상태_전략;
import com.arms.api.alm.issue.status.strategy.클라우드_지라_이슈상태_전략;
import com.arms.api.alm.issue.status.strategy.이슈상태_전략_등록_및_실행;
import com.arms.api.alm.serverinfo.model.enums.서버유형_정보;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import com.arms.api.alm.serverinfo.service.서버정보_서비스;
import com.arms.api.utils.errors.codes.에러코드;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class 이슈상태_전략_호출 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private 이슈상태_전략_등록_및_실행 이슈상태_전략_등록_및_실행;

    private 클라우드_지라_이슈상태_전략 클라우드_지라이슈상태_전략;

    private 온프레미스_지라_이슈상태_전략 온프레미스_지라이슈상태_전략;

    private com.arms.api.alm.issue.status.strategy.온프레미스_레드마인_이슈상태_전략 온프레미스_레드마인_이슈상태_전략;

    private 서버정보_서비스 서버정보_서비스;

    @Autowired
    public 이슈상태_전략_호출(이슈상태_전략_등록_및_실행 이슈상태_전략_등록_및_실행,
                      클라우드_지라_이슈상태_전략 클라우드_지라이슈상태_전략,
                      온프레미스_지라_이슈상태_전략 온프레미스_지라이슈상태_전략,
                      온프레미스_레드마인_이슈상태_전략 온프레미스_레드마인_이슈상태_전략,
                      서버정보_서비스 서버정보_서비스) {

        this.이슈상태_전략_등록_및_실행 = 이슈상태_전략_등록_및_실행;
        this.클라우드_지라이슈상태_전략 = 클라우드_지라이슈상태_전략;
        this.온프레미스_지라이슈상태_전략 = 온프레미스_지라이슈상태_전략;
        this.온프레미스_레드마인_이슈상태_전략 = 온프레미스_레드마인_이슈상태_전략;
        this.서버정보_서비스 = 서버정보_서비스;
    }

    private 이슈상태_전략_등록_및_실행 지라이슈상태_전략_확인(서버정보_데이터 서버정보) throws Exception {

        if (서버정보 == null || 서버정보.getType().isEmpty()) {
            로그.error("지라이슈 상태 전략 등록 Error: 서버정보_유형 " + 에러코드.서버유형_정보오류.getErrorMsg());
            throw new IllegalArgumentException("지라이슈 상태 전략 등록 Error: 서버정보_유형 " + 에러코드.서버유형_정보오류.getErrorMsg());
        }

        서버유형_정보 서버_유형 = 서버유형_정보.valueOf(서버정보.getType());

        if (서버_유형 == 서버유형_정보.클라우드) {
            이슈상태_전략_등록_및_실행.지라이슈상태_전략_등록(클라우드_지라이슈상태_전략);
        }
        else if (서버_유형 == 서버유형_정보.온프레미스) {
            이슈상태_전략_등록_및_실행.지라이슈상태_전략_등록(온프레미스_지라이슈상태_전략);
        }
        else if (서버_유형 == 서버유형_정보.레드마인_온프레미스) {
            이슈상태_전략_등록_및_실행.지라이슈상태_전략_등록(온프레미스_레드마인_이슈상태_전략);
        }

        return 이슈상태_전략_등록_및_실행;

    }

    public List<이슈상태_데이터> 이슈상태_목록_가져오기(Long 연결_아이디) throws Exception {

        if (연결_아이디 == null) {
            로그.error("이슈 상태 목록 가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("이슈 상태 목록 가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
        }

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);

        서버유형_정보 서버_유형 = 서버유형_정보.valueOf(서버정보.getType());

        if (서버_유형 == 서버유형_정보.클라우드) {
            throw new IllegalArgumentException("클라우드 타입은 이슈 상태 목록 가져오기를 사용할 수 없습니다.");
        }

        이슈상태_전략_등록_및_실행 = 지라이슈상태_전략_확인(서버정보);

        List<이슈상태_데이터> 반환할_이슈상태_데이터_목록
                = 이슈상태_전략_등록_및_실행.이슈상태_목록_가져오기(연결_아이디);

        return 반환할_이슈상태_데이터_목록;

    }


    public List<이슈상태_데이터> 프로젝트별_이슈상태_목록_가져오기(Long 연결_아이디, String 프로젝트_아이디) throws Exception {

        if (연결_아이디 == null) {
            로그.error("프로젝트별_이슈상태_목록_가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("프로젝트별_이슈상태_목록_가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
        }

        if (프로젝트_아이디 == null || 프로젝트_아이디.isEmpty()) {
            로그.error("프로젝트별_이슈상태_목록_가져오기 Error 프로젝트_아이디 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("프로젝트별_이슈상태_목록_가져오기 Error 프로젝트_아이디 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);

        서버유형_정보 서버_유형 = 서버유형_정보.valueOf(서버정보.getType());

        if (서버_유형 == 서버유형_정보.온프레미스) {
            throw new IllegalArgumentException("온프레미스 타입은 프로젝트별 이슈 상태 목록 가져오기를 사용할 수 없습니다.");
        }
        else if (서버_유형 == 서버유형_정보.레드마인_온프레미스) {
            로그.info("레드마인_온프레미스_이슈상태_전략 "+ 연결_아이디 +" 프로젝트별_이슈상태_목록_가져오기를 사용하지 않습니다.");
            throw new IllegalArgumentException("레드마인 온프레미스 타입은 프로젝트별 이슈 상태 목록 가져오기를 사용할 수 없습니다.");
        }

        이슈상태_전략_등록_및_실행 = 지라이슈상태_전략_확인(서버정보);

        List<이슈상태_데이터> 반환할_이슈상태_데이터_목록
                = 이슈상태_전략_등록_및_실행.프로젝트별_이슈상태_목록_가져오기(연결_아이디, 프로젝트_아이디);

        return 반환할_이슈상태_데이터_목록;
    }
}
