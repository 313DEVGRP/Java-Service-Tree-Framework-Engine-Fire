package com.arms.jira.jiraproject.service;

import com.arms.errors.codes.에러코드;
import com.arms.jira.info.model.지라연결정보_데이터;
import com.arms.jira.info.model.지라유형_정보;
import com.arms.jira.info.service.지라연결_서비스;
import com.arms.jira.jiraproject.model.지라프로젝트_데이터;
import com.arms.jira.jiraproject.strategy.온프레미스_지라프로젝트_전략;
import com.arms.jira.jiraproject.strategy.지라프로젝트_전략_등록_및_실행;
import com.arms.jira.jiraproject.strategy.클라우드_지라프로젝트_전략;
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

    지라프로젝트_전략_등록_및_실행 지라프로젝트_전략_등록_및_실행;

    클라우드_지라프로젝트_전략 클라우드_지라_프로젝트_전략;

    온프레미스_지라프로젝트_전략 온프레미스_지라_프로젝트_전략;

    지라연결_서비스 지라연결_서비스;

    @Autowired
    public 지라프로젝트_전략_호출(지라프로젝트_전략_등록_및_실행 지라프로젝트_전략_등록_및_실행,
                        클라우드_지라프로젝트_전략 클라우드_지라_프로젝트_전략,
                        온프레미스_지라프로젝트_전략 온프레미스_지라_프로젝트_전략,
                        지라연결_서비스 지라연결_서비스) {

        this.지라프로젝트_전략_등록_및_실행 = 지라프로젝트_전략_등록_및_실행;
        this.클라우드_지라_프로젝트_전략 = 클라우드_지라_프로젝트_전략;
        this.온프레미스_지라_프로젝트_전략 = 온프레미스_지라_프로젝트_전략;
        this.지라연결_서비스 = 지라연결_서비스;
    }

    private 지라프로젝트_전략_등록_및_실행 지라_프로젝트_전략_확인(지라연결정보_데이터 연결정보) {

        if (연결정보 == null || 연결정보.getType().isEmpty()) {
            로그.error("프로젝트 전략 등록 Error: 연결정보_유형 " + 에러코드.서버_유형_정보없음.getErrorMsg());
            throw new IllegalArgumentException("프로젝트 전략 등록 Error: 연결정보_유형 " + 에러코드.서버_유형_정보없음.getErrorMsg());
        }

        지라유형_정보 지라_유형 = 지라유형_정보.valueOf(연결정보.getType());

        if (지라_유형 == 지라유형_정보.클라우드) {
            지라프로젝트_전략_등록_및_실행.지라_프로젝트_전략_등록(클라우드_지라_프로젝트_전략);
        }
        else if (지라_유형 == 지라유형_정보.온프레미스) {
            지라프로젝트_전략_등록_및_실행.지라_프로젝트_전략_등록(온프레미스_지라_프로젝트_전략);
        }

        return 지라프로젝트_전략_등록_및_실행;

    }

    public 지라프로젝트_데이터 프로젝트_상세정보_가져오기(Long 연결_아이디, String 프로젝트_키_또는_아이디) throws Exception {

        if (연결_아이디 == null) {
            로그.error("프로젝트 상세 정보 가져오기 Error: 연결_아이디 " + 에러코드.서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("프로젝트 상세 정보 가져오기 Error: 연결_아이디 " + 에러코드.서버_아이디_없음.getErrorMsg());
        }

        if (프로젝트_키_또는_아이디 == null || 프로젝트_키_또는_아이디.isEmpty()) {
            로그.error("프로젝트 상세 정보 가져오기 Error 프로젝트_키_또는_아이디 " + 에러코드.검색정보_오류.getErrorMsg());
            throw new IllegalArgumentException("프로젝트 상세 정보 가져오기 Error 프로젝트_키_또는_아이디 " + 에러코드.검색정보_오류.getErrorMsg());
        }

        지라연결정보_데이터 연결정보 = 지라연결_서비스.checkInfo(연결_아이디);

        지라프로젝트_전략_등록_및_실행 = 지라_프로젝트_전략_확인(연결정보);

        지라프로젝트_데이터 반환할_지라_프로젝트_상세정보
                = 지라프로젝트_전략_등록_및_실행.프로젝트_상세정보_가져오기(연결_아이디, 프로젝트_키_또는_아이디);

        return 반환할_지라_프로젝트_상세정보;
    }

    public List<지라프로젝트_데이터> 프로젝트_전체_목록_가져오기(Long 연결_아이디) throws Exception {

        if (연결_아이디 == null) {
            로그.error("프로젝트 전체 목록 가져오기 Error: 연결_아이디 " + 에러코드.서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("프로젝트 전체 목록 가져오기 Error: 연결_아이디 " + 에러코드.서버_아이디_없음.getErrorMsg());
        }

        지라연결정보_데이터 연결정보 = 지라연결_서비스.checkInfo(연결_아이디);

        지라프로젝트_전략_등록_및_실행 = 지라_프로젝트_전략_확인(연결정보);

        List<지라프로젝트_데이터> 반환할_지라_프로젝트_목록
                = 지라프로젝트_전략_등록_및_실행.프로젝트_전체_목록_가져오기(연결_아이디);

        return 반환할_지라_프로젝트_목록;

    }
}
