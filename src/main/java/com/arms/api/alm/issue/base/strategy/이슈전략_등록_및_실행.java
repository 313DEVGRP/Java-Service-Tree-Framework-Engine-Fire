package com.arms.api.alm.issue.base.strategy;

import com.arms.api.alm.issue.base.model.지라이슈_데이터;
import com.arms.api.alm.issue.base.model.지라이슈생성_데이터;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class 이슈전략_등록_및_실행 {

    이슈전략 이슈전략;

    public void 이슈전략_등록(이슈전략 이슈전략) {
        this.이슈전략 = 이슈전략;
    }

    public List<지라이슈_데이터> 이슈_목록_가져오기(서버정보_데이터 서버정보, String 프로젝트_키_또는_아이디) {
        return this.이슈전략.이슈_목록_가져오기(서버정보, 프로젝트_키_또는_아이디);
    }

    public 지라이슈_데이터 이슈_생성하기(서버정보_데이터 서버정보, 지라이슈생성_데이터 지라이슈생성_데이터) {
        return this.이슈전략.이슈_생성하기(서버정보, 지라이슈생성_데이터);
    }

    public Map<String,Object> 이슈_수정하기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디, 지라이슈생성_데이터 지라이슈생성_데이터) {
        return this.이슈전략.이슈_수정하기(서버정보, 이슈_키_또는_아이디, 지라이슈생성_데이터);
    }

    public Map<String,Object> 이슈_삭제하기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디) {
        return this.이슈전략.이슈_삭제하기(서버정보, 이슈_키_또는_아이디);
    }

    public 지라이슈_데이터 이슈_상세정보_가져오기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디) {
        return this.이슈전략.이슈_상세정보_가져오기(서버정보, 이슈_키_또는_아이디);
    }

    public List<지라이슈_데이터> 이슈링크_가져오기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디) {
        return this.이슈전략.이슈링크_가져오기(서버정보, 이슈_키_또는_아이디);
    }

    public List<지라이슈_데이터> 서브테스크_가져오기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디) {
        return this.이슈전략.서브테스크_가져오기(서버정보, 이슈_키_또는_아이디);
    }

    public 지라이슈_데이터 증분이슈_상세정보_가져오기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디) {
        return this.이슈전략.증분이슈_상세정보_가져오기(서버정보, 이슈_키_또는_아이디);
    }
    
    public List<지라이슈_데이터> 증분이슈링크_가져오기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디) {
        return this.이슈전략.증분이슈링크_가져오기(서버정보, 이슈_키_또는_아이디);
    }

    public List<지라이슈_데이터> 증분서브테스크_가져오기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디) {
        return this.이슈전략.증분서브테스크_가져오기(서버정보, 이슈_키_또는_아이디);
    }
}
