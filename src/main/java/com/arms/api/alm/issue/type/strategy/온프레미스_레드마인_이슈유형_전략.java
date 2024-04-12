package com.arms.api.alm.issue.type.strategy;

import com.arms.api.alm.issue.type.model.이슈유형_데이터;
import com.arms.api.alm.utils.레드마인API_정보;
import com.arms.api.alm.utils.레드마인유틸;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import com.arms.api.alm.serverinfo.service.서버정보_서비스;
import com.arms.api.utils.errors.codes.에러코드;
import com.arms.api.utils.errors.에러로그_유틸;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.Tracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Component
public class 온프레미스_레드마인_이슈유형_전략 implements 이슈유형_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 서버정보_서비스 서버정보_서비스;

    @Autowired
    private 레드마인유틸 레드마인유틸;

    @Autowired
    private 레드마인API_정보 레드마인API_정보;

    @Override
    public List<이슈유형_데이터> 이슈유형_목록_가져오기(서버정보_데이터 서버정보) {
        RedmineManager 레드마인_매니저 = 레드마인유틸.레드마인_온프레미스_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        List<이슈유형_데이터> 지라이슈유형_목록;
        List<Tracker> 우선순위_목록;

        try {
            우선순위_목록 = 레드마인_매니저.getIssueManager().getTrackers();
        } catch (RedmineException e) {
            에러로그_유틸.예외로그출력(e, this.getClass().getName(), "이슈유형_목록_가져오기");
            throw new IllegalArgumentException(this.getClass().getName() + " :: "
                    + 에러코드.이슈유형_조회_오류.getErrorMsg() + " :: " +e.getMessage());
        }

        지라이슈유형_목록 = 우선순위_목록.stream().map(이슈유형 -> {
                        이슈유형_데이터 이슈유형_데이터 = 지라이슈유형_데이터형_변환(이슈유형, 서버정보.getUri(), null);
                        return 이슈유형_데이터;
                    })
                    .filter(Objects::nonNull)
                    .collect(toList());

        return 지라이슈유형_목록;
    }

    @Override
    public List<이슈유형_데이터> 프로젝트별_이슈유형_목록_가져오기(서버정보_데이터 서버정보, String 프로젝트_아이디) {

        RedmineManager 레드마인_매니저 = 레드마인유틸.레드마인_온프레미스_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        List<이슈유형_데이터> 지라이슈유형_목록;
        Project 프로젝트;

        try {
            프로젝트 = 레드마인_매니저.getProjectManager().getProjectById(Integer.parseInt(프로젝트_아이디));
        } catch (RedmineException e) {
            에러로그_유틸.예외로그출력(e, this.getClass().getName(), "프로젝트별_이슈유형_목록_가져오기");
            throw new IllegalArgumentException(this.getClass().getName() + " :: "
                    + 에러코드.이슈유형_조회_오류.getErrorMsg() + " :: " +e.getMessage());
        }

        지라이슈유형_목록 =  프로젝트.getTrackers().stream().map(이슈유형 -> {
                이슈유형_데이터 이슈유형_데이터 = 지라이슈유형_데이터형_변환(이슈유형, 서버정보.getUri(), 프로젝트_아이디);
                return 이슈유형_데이터;
            })
            .filter(Objects::nonNull)
            .collect(toList());

        return 지라이슈유형_목록;
    }

    private 이슈유형_데이터 지라이슈유형_데이터형_변환(Tracker 이슈유형, String 서버정보경로, String 프로젝트_아이디) {
        이슈유형_데이터 이슈유형_데이터 = new 이슈유형_데이터();

        이슈유형_데이터.setId(String.valueOf(이슈유형.getId()));
        이슈유형_데이터.setName(이슈유형.getName());
        이슈유형_데이터.setSubtask(false);

        String 이슈유형_경로 = 레드마인유틸.서버정보경로_체크(서버정보경로) + 레드마인API_정보.아이디_대체하기(레드마인API_정보.getEndpoint().getIssuetype(), String.valueOf(이슈유형.getId()));
        if (!프로젝트_아이디.isEmpty()) {
            이슈유형_데이터.setSelf(이슈유형_경로 + "&project_id=" + 프로젝트_아이디);
        }
        else {
            이슈유형_데이터.setSelf(이슈유형_경로);
        }

        return 이슈유형_데이터;
    }
}
