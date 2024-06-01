package com.arms.api.alm.project.strategy;

import com.arms.api.alm.project.model.프로젝트_데이터;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import com.arms.api.alm.utils.레드마인API_정보;
import com.arms.api.alm.utils.레드마인유틸;
import com.arms.api.util.errors.codes.에러코드;
import com.arms.api.util.errors.에러로그_유틸;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class 레드마인_온프레미스_프로젝트_전략 implements 프로젝트_전략 {
    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private 레드마인유틸 레드마인유틸;
    private 레드마인API_정보 레드마인API_정보;

    @Autowired
    public 레드마인_온프레미스_프로젝트_전략(레드마인유틸 레드마인유틸,
                                      레드마인API_정보 레드마인API_정보) {
        this.레드마인유틸 = 레드마인유틸;
        this.레드마인API_정보 = 레드마인API_정보;
    }

    @Override
    public 프로젝트_데이터 프로젝트_상세정보_가져오기(서버정보_데이터 서버정보, String 프로젝트_키_또는_아이디) {

        RedmineManager 레드마인_매니저 = 레드마인유틸.레드마인_온프레미스_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        프로젝트_데이터 프로젝트_데이터;
        try {
            Project 프로젝트 = 레드마인_매니저.getProjectManager().getProjectById(Integer.parseInt(프로젝트_키_또는_아이디));
            프로젝트_데이터 = 프로젝트_데이터형_변환(프로젝트, null, 서버정보.getUri());
        }
        catch (RedmineException e) {
            에러로그_유틸.예외로그출력(e, this.getClass().getName(),  "프로젝트_상세정보_가져오기");
            throw new IllegalArgumentException(this.getClass().getName() + " :: "
                    + 에러코드.프로젝트_조회_오류.getErrorMsg() + " :: " +e.getMessage());
        }

        return 프로젝트_데이터;
    }

    @Override
    public List<프로젝트_데이터> 프로젝트_목록_가져오기(서버정보_데이터 서버정보) {

        RedmineManager 레드마인_매니저 = 레드마인유틸.레드마인_온프레미스_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        List<Project> 프로젝트_목록;
        try {
            프로젝트_목록 = 레드마인_매니저.getProjectManager().getProjects();
        }
        catch (RedmineException e) {
            에러로그_유틸.예외로그출력(e, this.getClass().getName(),  "프로젝트_목록_가져오기");
            throw new IllegalArgumentException(this.getClass().getName() + " :: "
                    + 에러코드.프로젝트_조회_오류.getErrorMsg() + " :: " +e.getMessage());
        }

        Map<Integer, Project> 프로젝트맵 = 프로젝트_목록.stream()
                .collect(Collectors.toMap(Project::getId, Function.identity()));

        List<프로젝트_데이터> 지라프로젝트_목록 = 프로젝트맵.values().stream()
                .map(프로젝트 -> {
                    String 전체_프로젝트명 = 상위_프로젝트명_설정(프로젝트, 프로젝트맵);
                    return 프로젝트_데이터형_변환(프로젝트, 전체_프로젝트명, 서버정보.getUri());
                })
                .collect(Collectors.toList());

        return 지라프로젝트_목록;
    }

    private 프로젝트_데이터 프로젝트_데이터형_변환(Project 프로젝트, String 전체_프로젝트명, String 서버정보경로) {
        프로젝트_데이터 프로젝트_데이터 = new 프로젝트_데이터();

        프로젝트_데이터.setId(String.valueOf(프로젝트.getId()));
        프로젝트_데이터.setName(Optional.ofNullable(전체_프로젝트명).orElse(프로젝트.getName()));
        프로젝트_데이터.setKey(String.valueOf(프로젝트.getId()));
        프로젝트_데이터.setSelf(레드마인유틸.서버정보경로_체크(서버정보경로) + 레드마인API_정보.아이디_대체하기(레드마인API_정보.getEndpoint().getProject(), String.valueOf(프로젝트.getId())));

        return 프로젝트_데이터;
    }

    public String 상위_프로젝트명_설정(Project project, Map<Integer, Project> projectMap) {
        // 부모 프로젝트 ID 유무 확인
        return Optional.ofNullable(project.getParentId())
                .map(parentId ->
                        // 있다면 재귀함수 호출로 상위 프로젝트의 Name을 가져와서 설정, 상위 프로젝트 구분자를 위한 '&nbsp;-&nbsp;' 처리
                        상위_프로젝트명_설정(projectMap.get(parentId), projectMap) + "&nbsp;-&nbsp;" + project.getName()
                )
                // 부모 프로젝트가 없다면 프로젝트명 설정
                .orElse(project.getName());
    }
}
