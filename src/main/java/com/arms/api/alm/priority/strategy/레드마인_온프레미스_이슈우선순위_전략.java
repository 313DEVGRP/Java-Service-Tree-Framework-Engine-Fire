package com.arms.api.alm.priority.strategy;

import com.arms.api.alm.priority.model.이슈우선순위_데이터;
import com.arms.api.alm.utils.레드마인API_정보;
import com.arms.api.alm.utils.레드마인유틸;
import com.arms.api.utils.errors.에러로그_유틸;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import com.arms.api.alm.serverinfo.service.서버정보_서비스;
import com.arms.api.utils.errors.codes.에러코드;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.IssuePriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Component
public class 레드마인_온프레미스_이슈우선순위_전략 implements 이슈우선순위_전략 {
    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 서버정보_서비스 서버정보_서비스;

    @Autowired
    private 레드마인유틸 레드마인유틸;

    @Autowired
    private 레드마인API_정보 레드마인API_정보;

    @Override
    public List<이슈우선순위_데이터> 우선순위_목록_가져오기(Long 연결_아이디) {
        로그.info("레드마인_온프레미스_지라이슈우선순위_전략 "+ 연결_아이디 +" 우선순위_목록_가져오기");

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
        RedmineManager 레드마인_매니저 = 레드마인유틸.레드마인_온프레미스_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        List<이슈우선순위_데이터> 지라이슈우선순위_목록;
        List<IssuePriority> 우선순위_목록;
        try {
            우선순위_목록 = 레드마인_매니저.getIssueManager().getIssuePriorities();
        } catch (RedmineException e) {
            에러로그_유틸.예외로그출력(e, this.getClass().getName(), "우선순위_목록_가져오기");
            throw new IllegalArgumentException(this.getClass().getName() + " :: "
                    + 에러코드.이슈우선순위_조회_오류.getErrorMsg() + " :: " +e.getMessage());
        }

        지라이슈우선순위_목록 = 우선순위_목록.stream().map(우선순위 -> {
                        이슈우선순위_데이터 이슈우선순위_데이터 = 지라이슈우선순위_데이터형_변환(우선순위, 서버정보.getUri());
                        return 이슈우선순위_데이터;
                    })
                    .filter(Objects::nonNull)
                    .collect(toList());

        return 지라이슈우선순위_목록;
    }

    private 이슈우선순위_데이터 지라이슈우선순위_데이터형_변환(IssuePriority 우선순위, String 서버정보경로) {
        이슈우선순위_데이터 이슈우선순위_데이터 = new 이슈우선순위_데이터();

        이슈우선순위_데이터.setId(String.valueOf(우선순위.getId()));
        이슈우선순위_데이터.setName(우선순위.getName());
        이슈우선순위_데이터.setDefault(우선순위.isDefault());
        이슈우선순위_데이터.setSelf(레드마인유틸.서버정보경로_체크(서버정보경로) + 레드마인API_정보.아이디_대체하기(레드마인API_정보.getEndpoint().getPriority(), String.valueOf(우선순위.getId())));

        return 이슈우선순위_데이터;
    }
}
