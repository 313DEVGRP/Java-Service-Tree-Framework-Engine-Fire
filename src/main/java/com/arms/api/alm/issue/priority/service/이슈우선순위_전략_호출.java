package com.arms.api.alm.issue.priority.service;

import com.arms.api.alm.issue.priority.strategy.*;
import com.arms.api.alm.issue.priority.model.이슈우선순위_데이터;
import com.arms.api.alm.issue.priority.strategy.온프레미스_레드마인_이슈우선순위_전략;
import com.arms.api.alm.serverinfo.model.enums.서버유형_정보;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import com.arms.api.alm.serverinfo.service.서버정보_서비스;
import com.arms.api.util.errors.codes.에러코드;
import com.arms.api.alm.issue.priority.strategy.온프레미스_지라_이슈우선순위_전략;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class 이슈우선순위_전략_호출 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private 서버정보_서비스 서버정보_서비스;
    private 클라우드_지라_이슈우선순위_전략 클라우드_지라_이슈우선순위_전략;
    private 온프레미스_지라_이슈우선순위_전략 온프레미스_지라_이슈우선순위_전략;
    private 온프레미스_레드마인_이슈우선순위_전략 온프레미스_레드마인_이슈우선순위_전략;

    @Autowired
    public 이슈우선순위_전략_호출(서버정보_서비스 서버정보_서비스,
                        클라우드_지라_이슈우선순위_전략 클라우드_지라_이슈우선순위_전략,
                        온프레미스_지라_이슈우선순위_전략 온프레미스_지라_이슈우선순위_전략,
                        온프레미스_레드마인_이슈우선순위_전략 온프레미스_레드마인_이슈우선순위_전략) {

        this.서버정보_서비스 = 서버정보_서비스;
        this.클라우드_지라_이슈우선순위_전략 = 클라우드_지라_이슈우선순위_전략;
        this.온프레미스_지라_이슈우선순위_전략 = 온프레미스_지라_이슈우선순위_전략;
        this.온프레미스_레드마인_이슈우선순위_전략 = 온프레미스_레드마인_이슈우선순위_전략;
    }

    private 이슈우선순위_전략 이슈우선순위_전략_확인(서버정보_데이터 서버정보) {

        if (서버정보 == null || 서버정보.getType().isEmpty()) {
            로그.error("이슈 우선순위 전략 등록 Error: 서버정보_유형 " + 에러코드.서버유형_정보오류.getErrorMsg());
            throw new IllegalArgumentException("지라이슈 우선순위 전략 등록 Error: 서버정보_유형 " + 에러코드.서버유형_정보오류.getErrorMsg());
        }

        서버유형_정보 서버_유형 = 서버유형_정보.valueOf(서버정보.getType());

        이슈우선순위_전략 이슈우선순위_전략;
        if (서버_유형 == 서버유형_정보.클라우드) {
            이슈우선순위_전략 = this.클라우드_지라_이슈우선순위_전략;
        }
        else if (서버_유형 == 서버유형_정보.온프레미스) {
            이슈우선순위_전략 = this.온프레미스_지라_이슈우선순위_전략;
        }
        else if (서버_유형 == 서버유형_정보.레드마인_온프레미스) {
            이슈우선순위_전략 = this.온프레미스_레드마인_이슈우선순위_전략;
        }        
        else {
            throw new IllegalArgumentException("이슈 우선순위 전략 확인 Error: 허용하지 않는 서버정보_유형입니다. :: "+ 서버_유형+ " :: " + 에러코드.서버유형_정보오류.getErrorMsg());
        }

        return 이슈우선순위_전략;

    }

    public List<이슈우선순위_데이터> 우선순위_목록_가져오기(Long 연결_아이디) {

        if (연결_아이디 == null) {
            로그.error("이슈 우선순위 목록 가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("이슈 우선순위 목록 가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
        }

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);

        이슈우선순위_전략 이슈우선순위_전략 = 이슈우선순위_전략_확인(서버정보);
        이슈우선순위_전략_등록_및_실행 이슈우선순위_전략_등록_및_실행 = new 이슈우선순위_전략_등록_및_실행();
        이슈우선순위_전략_등록_및_실행.이슈우선순위_전략_등록(이슈우선순위_전략);

        List<이슈우선순위_데이터> 반환할_이슈우선순위_데이터_목록
                = 이슈우선순위_전략_등록_및_실행.우선순위_목록_가져오기(서버정보);

        return 반환할_이슈우선순위_데이터_목록;
    }

}
