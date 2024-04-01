package com.arms.api.alm.issuestatus.strategy;

import com.arms.api.alm.issuestatus.model.이슈상태_데이터;
import com.arms.api.alm.utils.레드마인API_정보;
import com.arms.api.alm.utils.레드마인유틸;
import com.arms.api.serverinfo.model.서버정보_데이터;
import com.arms.api.serverinfo.service.서버정보_서비스;
import com.arms.api.utils.errors.codes.에러코드;
import com.arms.api.utils.errors.에러로그_유틸;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.IssueStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Component
public class 온프레미스_레드마인_이슈상태_전략 implements 이슈상태_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 서버정보_서비스 서버정보_서비스;

    @Autowired
    private 레드마인유틸 레드마인유틸;

    @Autowired
    private 레드마인API_정보 레드마인API_정보;

    @Override
    public List<이슈상태_데이터> 이슈상태_목록_가져오기(Long 연결_아이디) {
        로그.info("레드마인_온프레미스_이슈상태_전략 "+ 연결_아이디 +" 이슈상태_목록_가져오기");

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
        RedmineManager 레드마인_매니저 = 레드마인유틸.레드마인_온프레미스_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        List<이슈상태_데이터> 지라이슈상태_목록;
        List<IssueStatus> 이슈상태_목록;

        try {
            이슈상태_목록 = 레드마인_매니저.getIssueManager().getStatuses();
        } catch (RedmineException e) {
            에러로그_유틸.예외로그출력(e, this.getClass().getName(), "이슈상태_목록_가져오기");
            throw new IllegalArgumentException(this.getClass().getName() + " :: "
                    + 에러코드.이슈상태_조회_오류.getErrorMsg() + " :: " +e.getMessage());
        }

        지라이슈상태_목록 = 이슈상태_목록.stream().map(이슈유형 -> {
                    이슈상태_데이터 이슈상태_데이터 = 지라이슈상태_데이터형_변환(이슈유형, 서버정보.getUri());
                    return 이슈상태_데이터;
                })
                .filter(Objects::nonNull)
                .collect(toList());

        return 지라이슈상태_목록;
    }

    @Override
    public List<이슈상태_데이터> 프로젝트별_이슈상태_목록_가져오기(Long 연결_아이디, String 프로젝트_아이디) {
        로그.info("레드마인_온프레미스_이슈상태_전략 "+ 연결_아이디 +" 프로젝트별_이슈상태_목록_가져오기를 사용하지 않습니다.");
        return null;
    }

    private 이슈상태_데이터 지라이슈상태_데이터형_변환(IssueStatus 이슈상태, String 서버정보경로) {
        이슈상태_데이터 이슈상태_데이터 = new 이슈상태_데이터();

        이슈상태_데이터.setId(String.valueOf(이슈상태.getId()));
        이슈상태_데이터.setName(이슈상태.getName());
        이슈상태_데이터.setSelf(레드마인유틸.서버정보경로_체크(서버정보경로) + 레드마인API_정보.아이디_대체하기(레드마인API_정보.getEndpoint().getIssuestatus(), String.valueOf(이슈상태.getId())));

        return 이슈상태_데이터;
    }
}
