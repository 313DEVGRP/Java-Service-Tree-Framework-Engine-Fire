package com.arms.api.alm.issue.base.service;

import com.arms.api.alm.issue.base.model.dto.지라이슈_데이터;
import com.arms.api.alm.issue.base.model.dto.지라이슈생성_데이터;
import com.arms.api.alm.issue.base.model.vo.지라이슈_벌크_추가_요청;
import com.arms.api.alm.issue.base.strategy.*;
import com.arms.api.alm.serverinfo.model.enums.서버유형_정보;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import com.arms.api.alm.serverinfo.service.서버정보_서비스;
import com.arms.api.util.errors.codes.에러코드;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class 이슈전략_호출 {
    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private 서버정보_서비스 서버정보_서비스;
    private 클라우드_지라_이슈전략 클라우드_지라_이슈전략;
    private 온프레미스_지라_이슈전략 온프레미스_지라이슈_전략;
    private 온프레미스_레드마인_이슈전략 온프레미스_레드마인_이슈전략;

    @Autowired
    public 이슈전략_호출(서버정보_서비스 서버정보_서비스,
                    클라우드_지라_이슈전략 클라우드_지라_이슈전략,
                    온프레미스_지라_이슈전략 온프레미스_지라이슈_전략,
                    온프레미스_레드마인_이슈전략 온프레미스_레드마인_이슈전략) {

        this.서버정보_서비스 = 서버정보_서비스;
        this.클라우드_지라_이슈전략 = 클라우드_지라_이슈전략;
        this.온프레미스_지라이슈_전략 = 온프레미스_지라이슈_전략;
        this.온프레미스_레드마인_이슈전략 = 온프레미스_레드마인_이슈전략;
    }

    private 이슈전략_등록_및_실행 이슈전략_확인(서버정보_데이터 서버정보) {

        if (서버정보 == null || 서버정보.getType().isEmpty()) {
            로그.error("지라이슈 전략 등록 Error: 서버정보_유형 " + 에러코드.서버유형_정보오류.getErrorMsg());
            throw new IllegalArgumentException("지라이슈 전략 등록 Error: 서버정보_유형 " + 에러코드.서버유형_정보오류.getErrorMsg());
        }

        서버유형_정보 서버_유형 = 서버유형_정보.valueOf(서버정보.getType());

        이슈전략 이슈전략;
        if (서버_유형 == 서버유형_정보.클라우드) {
            이슈전략 = this.클라우드_지라_이슈전략;
        }
        else if (서버_유형 == 서버유형_정보.온프레미스) {
            이슈전략 = this.온프레미스_지라이슈_전략;
        }
        else if (서버_유형 == 서버유형_정보.레드마인_온프레미스) {
            이슈전략 = this.온프레미스_레드마인_이슈전략;
        }
        else {
            throw new IllegalArgumentException("이슈 전략 확인 Error: 허용하지 않는 서버정보_유형입니다. :: "+ 서버_유형+ " :: " + 에러코드.서버유형_정보오류.getErrorMsg());
        }

        이슈전략_등록_및_실행 이슈전략_등록_및_실행 = new 이슈전략_등록_및_실행();
        이슈전략_등록_및_실행.이슈전략_등록(이슈전략);

        return 이슈전략_등록_및_실행;
    }

    public List<지라이슈_데이터> 이슈_목록_가져오기(Long 연결_아이디, String 프로젝트_키_또는_아이디) {

        if (연결_아이디 == null) {
            로그.error("이슈 목록 가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("이슈 전체 목록 가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
        }

        if (프로젝트_키_또는_아이디 == null || 프로젝트_키_또는_아이디.isEmpty()) {
            로그.error("이슈 목록 가져오기 Error 프로젝트_키_또는_아이디 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("이슈 목록 가져오기 Error 프로젝트_키_또는_아이디 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);

        이슈전략_등록_및_실행 이슈전략_등록_및_실행 = this.이슈전략_확인(서버정보);
        List<지라이슈_데이터> 반환할_지라이슈_데이터
                = 이슈전략_등록_및_실행.이슈_목록_가져오기(서버정보, 프로젝트_키_또는_아이디);

        return 반환할_지라이슈_데이터;

    }

    public 지라이슈_데이터 이슈_생성하기(Long 연결_아이디,
                            지라이슈생성_데이터 지라이슈생성_데이터) {

        if (연결_아이디 == null) {
            로그.error("이슈 생성하기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("이슈 생성하기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
        }

        if (지라이슈생성_데이터 == null) {
            로그.error("이슈 생성하기 Error 지라이슈생성_데이터가 없습니다.");
            throw new IllegalArgumentException("이슈 생성하기 Error 지라이슈생성_데이터가 없습니다.");
        }

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);

        이슈전략_등록_및_실행 이슈전략_등록_및_실행 = this.이슈전략_확인(서버정보);
        지라이슈_데이터 반환할_지라이슈_데이터
                = 이슈전략_등록_및_실행.이슈_생성하기(서버정보, 지라이슈생성_데이터);

        return 반환할_지라이슈_데이터;

    }

    public Map<String,Object> 이슈_수정하기(Long 연결_아이디, String 이슈_키_또는_아이디,
                                      지라이슈생성_데이터 지라이슈생성_데이터) {

        if (연결_아이디 == null) {
            로그.error("이슈 수정하기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("이슈 수정하기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
        }

        if (이슈_키_또는_아이디==null || 이슈_키_또는_아이디.isEmpty()) {
            로그.error("이슈 수정하기 Error 이슈_키_또는_아이디가 없습니다.");
            throw new IllegalArgumentException(에러코드.파라미터_NULL_오류.getErrorMsg() + " :: 이슈 수정하기 Error 이슈_키_또는_아이디가 없습니다.");
        }

        if (지라이슈생성_데이터 == null) {
            로그.error("이슈 수정하기 Error 지라이슈생성_데이터가 없습니다.");
            throw new IllegalArgumentException("이슈 수정하기 Error 지라이슈생성_데이터가 없습니다.");
        }

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);

        이슈전략_등록_및_실행 이슈전략_등록_및_실행 = this.이슈전략_확인(서버정보);
        Map<String,Object> 이슈수정_결과
                = 이슈전략_등록_및_실행.이슈_수정하기(서버정보, 이슈_키_또는_아이디, 지라이슈생성_데이터);

        return 이슈수정_결과;

    }

    public Map<String,Object> 이슈_상태_변경하기(Long 연결_아이디, String 이슈_키_또는_아이디, String 상태_아이디) {

        if (연결_아이디 == null) {
            로그.error("이슈 상태 변경하기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("이슈 상태 변경하기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
        }

        if (이슈_키_또는_아이디 == null || 이슈_키_또는_아이디.isEmpty()) {
            로그.error("이슈 상태 변경하기 Error 이슈_키_또는_아이디가 없습니다.");
            throw new IllegalArgumentException(에러코드.파라미터_NULL_오류.getErrorMsg() + " :: 이슈 상태 변경하기 Error 이슈_키_또는_아이디가 없습니다.");
        }

        if (상태_아이디 == null || 상태_아이디.isEmpty()) {
            로그.error("이슈 상태 변경하기 Error 상태 아이디가 없습니다.");
            throw new IllegalArgumentException("이슈 상태 변경하기 Error 상태 아이디가 없습니다.");
        }

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);

        이슈전략_등록_및_실행 이슈전략_등록_및_실행 = this.이슈전략_확인(서버정보);
        Map<String,Object> 상태변경_결과
                = 이슈전략_등록_및_실행.이슈_상태_변경하기(서버정보, 이슈_키_또는_아이디, 상태_아이디);

        return 상태변경_결과;

    }

    public Map<String,Object> 이슈_삭제하기(Long 연결_아이디, String 이슈_키_또는_아이디) {

        if (연결_아이디 == null) {
            로그.error("이슈 삭제 라벨 처리하기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("이슈 삭제 라벨 처리하기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
        }

        if (이슈_키_또는_아이디 == null || 이슈_키_또는_아이디.isEmpty()) {
            로그.error("이슈 삭제 라벨 처리하기 Error 이슈_키_또는_아이디 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("이슈 삭제 라벨 처리하기 Error 이슈_키_또는_아이디 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);

        이슈전략_등록_및_실행 이슈전략_등록_및_실행 = this.이슈전략_확인(서버정보);
        Map<String,Object> 이슈삭제라벨_결과
                = 이슈전략_등록_및_실행.이슈_삭제하기(서버정보, 이슈_키_또는_아이디);

        return 이슈삭제라벨_결과;

    }

    public 지라이슈_데이터 이슈_상세정보_가져오기(지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값) {
        return this.이슈_상세정보_가져오기(지라이슈_벌크_추가_요청값.get지라서버_아이디(),지라이슈_벌크_추가_요청값.get이슈_키());
    }

    public 지라이슈_데이터 이슈_상세정보_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) {

        if (연결_아이디 == null) {
            로그.error("이슈 상세정보 가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("이슈 상세정보 가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
        }

        if (이슈_키_또는_아이디 == null || 이슈_키_또는_아이디.isEmpty()) {
            로그.error("이슈 상세정보 가져오기 Error 이슈_키_또는_아이디 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("이슈 상세정보 가져오기 Error 이슈_키_또는_아이디 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);

        이슈전략_등록_및_실행 이슈전략_등록_및_실행 = this.이슈전략_확인(서버정보);
        지라이슈_데이터 반환할_지라이슈_데이터
                = 이슈전략_등록_및_실행.이슈_상세정보_가져오기(서버정보, 이슈_키_또는_아이디);

        return 반환할_지라이슈_데이터;

    }

    public List<지라이슈_데이터> 이슈링크_가져오기(지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값) {
        List<지라이슈_데이터> 이슈링크_가져오기 = this.이슈링크_가져오기(지라이슈_벌크_추가_요청값.get지라서버_아이디(), 지라이슈_벌크_추가_요청값.get이슈_키());
        return Optional.ofNullable(이슈링크_가져오기).orElse(new ArrayList<>());
    }

    public List<지라이슈_데이터> 이슈링크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) {

        if (연결_아이디 == null) {
            로그.error("이슈 링크 가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("이슈 링크 가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
        }

        if (이슈_키_또는_아이디 == null || 이슈_키_또는_아이디.isEmpty()) {
            로그.error("이슈 링크 가져오기 Error 이슈_키_또는_아이디 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("이슈 링크 가져오기 Error 이슈_키_또는_아이디 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);

        이슈전략_등록_및_실행 이슈전략_등록_및_실행 = this.이슈전략_확인(서버정보);
        List<지라이슈_데이터> 반환할_이슈링크_목록
                = 이슈전략_등록_및_실행.이슈링크_가져오기(서버정보, 이슈_키_또는_아이디);

        return 반환할_이슈링크_목록;

    }

    public List<지라이슈_데이터> 서브테스크_가져오기(지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값) {
        List<지라이슈_데이터> 서브테스크_가져오기 = this.서브테스크_가져오기(지라이슈_벌크_추가_요청값.get지라서버_아이디(), 지라이슈_벌크_추가_요청값.get이슈_키());
        return Optional.ofNullable(서브테스크_가져오기).orElse(new ArrayList<>());
    }

    public List<지라이슈_데이터> 서브테스크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) {

        if (연결_아이디 == null) {
            로그.error("이슈 서브테스크 가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("이슈 서브테스크 가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
        }

        if (이슈_키_또는_아이디 == null || 이슈_키_또는_아이디.isEmpty()) {
            로그.error("이슈 서브테스크 가져오기 Error 이슈_키_또는_아이디 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("이슈 서브테스크 가져오기 Error 이슈_키_또는_아이디 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);

        이슈전략_등록_및_실행 이슈전략_등록_및_실행 = this.이슈전략_확인(서버정보);
        List<지라이슈_데이터> 반환할_이슈링크_목록
                = 이슈전략_등록_및_실행.서브테스크_가져오기(서버정보, 이슈_키_또는_아이디);

        return 반환할_이슈링크_목록;
    }

    public 지라이슈_데이터 증분이슈_상세정보_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) {

        if (연결_아이디 == null) {
            로그.error("증분이슈_상세정보_가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("증분이슈_상세정보_가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
        }

        if (이슈_키_또는_아이디 == null || 이슈_키_또는_아이디.isEmpty()) {
            로그.error("증분이슈_상세정보_가져오기 Error 이슈_키_또는_아이디 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("증분이슈_상세정보_가져오기 Error 이슈_키_또는_아이디 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);

        이슈전략_등록_및_실행 이슈전략_등록_및_실행 = this.이슈전략_확인(서버정보);
        지라이슈_데이터 반환할_지라이슈_데이터 = 이슈전략_등록_및_실행.증분이슈_상세정보_가져오기(서버정보, 이슈_키_또는_아이디);

        return 반환할_지라이슈_데이터;

    }

    public List<지라이슈_데이터> 증분이슈링크_가져오기(지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값) {
        List<지라이슈_데이터> 증분이슈링크_가져오기 = this.증분이슈링크_가져오기(지라이슈_벌크_추가_요청값.get지라서버_아이디(), 지라이슈_벌크_추가_요청값.get이슈_키());
        return Optional.ofNullable(증분이슈링크_가져오기).orElse(new ArrayList<>());
    }

    public List<지라이슈_데이터> 증분이슈링크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) {

        if (연결_아이디 == null) {
            로그.error("이슈 링크 가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("이슈 링크 가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
        }

        if (이슈_키_또는_아이디 == null || 이슈_키_또는_아이디.isEmpty()) {
            로그.error("이슈 링크 가져오기 Error 이슈_키_또는_아이디 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("이슈 링크 가져오기 Error 이슈_키_또는_아이디 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);

        이슈전략_등록_및_실행 이슈전략_등록_및_실행 = this.이슈전략_확인(서버정보);
        List<지라이슈_데이터> 반환할_이슈링크_목록
                = 이슈전략_등록_및_실행.증분이슈링크_가져오기(서버정보, 이슈_키_또는_아이디);

        return 반환할_이슈링크_목록;

    }

    public List<지라이슈_데이터> 증분서브테스크_가져오기(지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청값) {
        List<지라이슈_데이터> 증분서브테스크_가져오기 = 증분서브테스크_가져오기(지라이슈_벌크_추가_요청값.get지라서버_아이디(), 지라이슈_벌크_추가_요청값.get이슈_키());
        return Optional.ofNullable(증분서브테스크_가져오기).orElse(new ArrayList<>());
    }

    public List<지라이슈_데이터> 증분서브테스크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) {

        if (연결_아이디 == null) {
            로그.error("이슈 서브테스크 가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
            throw new IllegalArgumentException("이슈 서브테스크 가져오기 Error: 연결_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg());
        }

        if (이슈_키_또는_아이디 == null || 이슈_키_또는_아이디.isEmpty()) {
            로그.error("이슈 서브테스크 가져오기 Error 이슈_키_또는_아이디 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
            throw new IllegalArgumentException("이슈 서브테스크 가져오기 Error 이슈_키_또는_아이디 " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);

        이슈전략_등록_및_실행 이슈전략_등록_및_실행 = this.이슈전략_확인(서버정보);
        List<지라이슈_데이터> 반환할_이슈링크_목록
                = 이슈전략_등록_및_실행.증분서브테스크_가져오기(서버정보, 이슈_키_또는_아이디);

        return 반환할_이슈링크_목록;
    }
}
