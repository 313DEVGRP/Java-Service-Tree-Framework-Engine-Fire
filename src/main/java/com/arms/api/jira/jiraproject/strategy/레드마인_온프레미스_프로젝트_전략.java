package com.arms.api.jira.jiraproject.strategy;

import com.arms.api.jira.jiraproject.model.지라프로젝트_데이터;
import com.arms.api.jira.utils.지라API_정보;
import com.arms.api.serverinfo.model.서버정보_데이터;
import com.arms.api.serverinfo.service.서버정보_서비스;
import com.arms.errors.codes.에러코드;
import com.arms.utils.지라유틸;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Component
public class 레드마인_온프레미스_프로젝트_전략 implements 지라프로젝트_전략 {
    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 서버정보_서비스 서버정보_서비스;

    @Autowired
    private 지라유틸 지라유틸;

    @Override
    public 지라프로젝트_데이터 프로젝트_상세정보_가져오기(Long 연결_아이디, String 프로젝트_키_또는_아이디) {

        로그.info("레드마인_온프레미스_프로젝트_전략 "+ 프로젝트_키_또는_아이디 +" 상세정보 가져오기");

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
        RedmineManager 레드마인_매니저 = 지라유틸.레드마인_온프레미스_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        지라프로젝트_데이터 지라프로젝트_데이터;
        try {
            Project 프로젝트 = 레드마인_매니저.getProjectManager().getProjectById(Integer.parseInt(프로젝트_키_또는_아이디));
            지라프로젝트_데이터 = 지라프로젝트_데이터형_변환(프로젝트, 서버정보.getUri());
        }
        catch (RedmineException e) {
            로그.error("레드마인 온프레미스 프로젝트 상세 정보 가져오기에 실패하였습니다.");
            로그.error(e.getMessage());
            throw new IllegalArgumentException(this.getClass().getName() + " :: "
                    + 에러코드.프로젝트_조회_오류.getErrorMsg() + " :: " +e.getMessage());
        }

        return 지라프로젝트_데이터;
    }

    @Override
    public List<지라프로젝트_데이터> 프로젝트_목록_가져오기(Long 연결_아이디) {
        로그.info("레드마인_온프레미스_프로젝트_전략 "+ 연결_아이디 +" 프로젝트_목록_가져오기");

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
        RedmineManager 레드마인_매니저 = 지라유틸.레드마인_온프레미스_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        List<지라프로젝트_데이터> 지라프로젝트_목록;
        List<Project> 프로젝트_목록 = null;
        try {
            프로젝트_목록 = 레드마인_매니저.getProjectManager().getProjects();
        }
        catch (RedmineException e) {
                로그.error("레드마인 온프레미스 프로젝트 전체 목록 가져오기에 실패하였습니다.");
                로그.error(e.getMessage());
                throw new IllegalArgumentException(this.getClass().getName() + " :: "
                        + 에러코드.프로젝트_조회_오류.getErrorMsg() + " :: " +e.getMessage());
        }

        지라프로젝트_목록 = 프로젝트_목록.stream().map(프로젝트 -> {
                지라프로젝트_데이터 지라프로젝트_데이터 = 지라프로젝트_데이터형_변환(프로젝트, 서버정보.getUri());
                return 지라프로젝트_데이터;
            })
            .filter(Objects::nonNull)
            .collect(toList());

        return 지라프로젝트_목록;
    }

    private 지라프로젝트_데이터 지라프로젝트_데이터형_변환(Project 프로젝트, String 서버정보경로) {
        지라프로젝트_데이터 지라프로젝트_데이터 = new 지라프로젝트_데이터();

        지라프로젝트_데이터.setId(String.valueOf(프로젝트.getId()));
        지라프로젝트_데이터.setName(프로젝트.getName());
        지라프로젝트_데이터.setKey(프로젝트.getIdentifier());
        지라프로젝트_데이터.setSelf(서버정보경로 + "/projects/"+프로젝트.getId()+".json");

        return 지라프로젝트_데이터;
    }
}
