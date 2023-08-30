package com.arms.jira.jiraissue.strategy;

import com.arms.jira.jiraissue.model.지라이슈생성_데이터;
import com.arms.jira.jiraissue.model.지라이슈_데이터;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Component
public class 지라이슈_전략_등록_및_실행 {

    지라이슈_전략 지라이슈_전략;

    public List<지라이슈_데이터> 이슈_전체_목록_가져오기(Long 연결_아이디, String 프로젝트_키_또는_아이디) throws Exception{
        return this.지라이슈_전략.이슈_전체_목록_가져오기(연결_아이디, 프로젝트_키_또는_아이디);
    }

    public 지라이슈_데이터 이슈_상세정보_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) throws Exception{
        return this.지라이슈_전략.이슈_상세정보_가져오기(연결_아이디, 이슈_키_또는_아이디);
    }

    public 지라이슈_데이터 이슈_생성하기(Long 연결_아이디,
                                   지라이슈생성_데이터 지라이슈생성_데이터) throws Exception {
        return this.지라이슈_전략.이슈_생성하기(연결_아이디, 지라이슈생성_데이터);
    }

    public Map<String,Object> 이슈_수정하기(Long 연결_아이디, String 이슈_키_또는_아이디,
                               지라이슈생성_데이터 지라이슈생성_데이터) throws Exception {
        return this.지라이슈_전략.이슈_수정하기(연결_아이디, 이슈_키_또는_아이디, 지라이슈생성_데이터);
    }

    public Map<String,Object> 이슈_삭제_라벨_처리하기(Long 연결_아이디, String 이슈_키_또는_아이디) throws Exception {
        return this.지라이슈_전략.이슈_삭제_라벨_처리하기(연결_아이디, 이슈_키_또는_아이디);
    }

    public void 지라_이슈_전략_등록(지라이슈_전략 지라이슈_전략) {
        this.지라이슈_전략 = 지라이슈_전략;
    }

    public List<지라이슈_데이터> 이슈링크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) throws URISyntaxException, IOException, ExecutionException, InterruptedException {
        return this.지라이슈_전략.이슈링크_가져오기(연결_아이디, 이슈_키_또는_아이디);
    }

    public List<지라이슈_데이터> 서브테스크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) throws URISyntaxException, IOException, ExecutionException, InterruptedException {
        return this.지라이슈_전략.서브테스크_가져오기(연결_아이디, 이슈_키_또는_아이디);
    }
}
