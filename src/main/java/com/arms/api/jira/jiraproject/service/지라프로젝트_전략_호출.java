package com.arms.api.jira.jiraproject.service;

import com.arms.api.jira.jiraproject.model.지라프로젝트_데이터;
import com.arms.errors.codes.에러코드;
import com.arms.api.serverinfo.model.서버정보_데이터;
import com.arms.api.serverinfo.service.서버정보_서비스;
import com.arms.api.jira.jiraproject.strategy.온프레미스_지라프로젝트_전략;
import com.arms.api.jira.jiraproject.strategy.지라프로젝트_전략_등록_및_실행;
import com.arms.api.jira.jiraproject.strategy.클라우드_지라프로젝트_전략;
import com.arms.api.jira.jiraproject.strategy.레드마인_온프레미스_프로젝트_전략;
import com.arms.api.serverinfo.helper.서버유형_정보;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class 지라프로젝트_전략_호출 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private 지라프로젝트_전략_등록_및_실행 지라프로젝트_전략_등록_및_실행;

    private 클라우드_지라프로젝트_전략 클라우드_지라프로젝트_전략;

    private 온프레미스_지라프로젝트_전략 온프레미스_지라프로젝트_전략;

    private 레드마인_온프레미스_프로젝트_전략 레드마인_온프레미스_프로젝트_전략;

    private 서버정보_서비스 서버정보_서비스;

    @Autowired
    public 지라프로젝트_전략_호출(지라프로젝트_전략_등록_및_실행 지라프로젝트_전략_등록_및_실행,
                        클라우드_지라프로젝트_전략 클라우드_지라프로젝트_전략,
                        온프레미스_지라프로젝트_전략 온프레미스_지라프로젝트_전략,
                        레드마인_온프레미스_프로젝트_전략 레드마인_온프레미스_프로젝트_전략,
                        서버정보_서비스 서버정보_서비스) {

        this.지라프로젝트_전략_등록_및_실행 = 지라프로젝트_전략_등록_및_실행;
        this.클라우드_지라프로젝트_전략 = 클라우드_지라프로젝트_전략;
        this.온프레미스_지라프로젝트_전략 = 온프레미스_지라프로젝트_전략;
        this.레드마인_온프레미스_프로젝트_전략 = 레드마인_온프레미스_프로젝트_전략;
        this.서버정보_서비스 = 서버정보_서비스;
    }

    private 지라프로젝트_전략_등록_및_실행 지라프로젝트_전략_확인(서버정보_데이터 서버정보) {

        if (서버정보 == null || 서버정보.getType().isEmpty()) {
            로그.error("프로젝트 전략 등록 Error: 서버정보_유형 " + 에러코드.서버유형_정보오류.getErrorMsg());
            throw new IllegalArgumentException("프로젝트 전략 등록 Error: 서버정보_유형 " + 에러코드.서버유형_정보오류.getErrorMsg());
        }

        서버유형_정보 지라_유형 = 서버유형_정보.valueOf(서버정보.getType());

        if (지라_유형 == 서버유형_정보.클라우드) {
            지라프로젝트_전략_등록_및_실행.지라프로젝트_전략_등록(클라우드_지라프로젝트_전략);
        }
        else if (지라_유형 == 서버유형_정보.온프레미스) {
            지라프로젝트_전략_등록_및_실행.지라프로젝트_전략_등록(온프레미스_지라프로젝트_전략);
        }
        else if (지라_유형 == 서버유형_정보.레드마인_온프레미스) {
            지라프로젝트_전략_등록_및_실행.지라프로젝트_전략_등록(레드마인_온프레미스_프로젝트_전략);
        }

        return 지라프로젝트_전략_등록_및_실행;

    }

    public 지라프로젝트_데이터 프로젝트_상세정보_가져오기(Long 연결_아이디, String 프로젝트_키_또는_아이디) throws Exception {

        if (연결_아이디 == null) {
            로그.error("프로젝트 상세 정보 가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("프로젝트 상세 정보 가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
        }

        if (프로젝트_키_또는_아이디 == null || 프로젝트_키_또는_아이디.isEmpty()) {
            로그.error("프로젝트 상세 정보 가져오기 Error 프로젝트_키_또는_아이디 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("프로젝트 상세 정보 가져오기 Error 프로젝트_키_또는_아이디 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);

        지라프로젝트_전략_등록_및_실행 = 지라프로젝트_전략_확인(서버정보);

        지라프로젝트_데이터 반환할_지라프로젝트_데이터
                = 지라프로젝트_전략_등록_및_실행.프로젝트_상세정보_가져오기(연결_아이디, 프로젝트_키_또는_아이디);

        return 반환할_지라프로젝트_데이터;
    }

    public List<지라프로젝트_데이터> 프로젝트_목록_가져오기(Long 연결_아이디) throws Exception {

        if (연결_아이디 == null) {
            로그.error("프로젝트 전체 목록 가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("프로젝트 전체 목록 가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
        }

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);

        지라프로젝트_전략_등록_및_실행 = 지라프로젝트_전략_확인(서버정보);

        List<지라프로젝트_데이터> 반환할_지라프로젝트_목록
                = 지라프로젝트_전략_등록_및_실행.프로젝트_목록_가져오기(연결_아이디);

        return 반환할_지라프로젝트_목록;

    }
}
