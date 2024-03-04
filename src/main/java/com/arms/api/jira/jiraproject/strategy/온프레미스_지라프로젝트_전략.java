package com.arms.api.jira.jiraproject.strategy;

import com.arms.api.jira.jiraproject.model.지라프로젝트_데이터;
import com.arms.errors.codes.에러코드;
import com.arms.utils.지라유틸;
import com.arms.api.serverinfo.model.서버정보_데이터;
import com.arms.api.serverinfo.service.서버정보_서비스;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Component
public class 온프레미스_지라프로젝트_전략 implements 지라프로젝트_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 서버정보_서비스 서버정보_서비스;

    @Override
    public 지라프로젝트_데이터 프로젝트_상세정보_가져오기(Long 연결_아이디, String 프로젝트_키_또는_아이디) {

        로그.info("온프레미스 지라 프로젝트 "+ 프로젝트_키_또는_아이디 +" 상세정보 가져오기");

        try {
            서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
            JiraRestClient restClient = 지라유틸.온프레미스_통신기_생성(서버정보.getUri(),
                                                                서버정보.getUserId(),
                                                                서버정보.getPasswordOrToken());

            BasicProject 온프레미스_지라프로젝트 = restClient.getProjectClient()
                                                        .getProject(프로젝트_키_또는_아이디)
                                                        .claim();

            지라프로젝트_데이터 반환할_지라프로젝트_데이터 = new 지라프로젝트_데이터();
            반환할_지라프로젝트_데이터.setSelf(온프레미스_지라프로젝트.getSelf().toString());
            반환할_지라프로젝트_데이터.setId(온프레미스_지라프로젝트.getId().toString());
            반환할_지라프로젝트_데이터.setKey(온프레미스_지라프로젝트.getKey());
            반환할_지라프로젝트_데이터.setName(온프레미스_지라프로젝트.getName());

            return 반환할_지라프로젝트_데이터;

        } catch (Exception e) {
            로그.error("온프레미스 프로젝트 정보 가져오기에 실패하였습니다." +e.getMessage());
            throw new IllegalArgumentException(에러코드.요청한_데이터가_유효하지않음.getErrorMsg());
        }

    }

    @Override
    public List<지라프로젝트_데이터> 프로젝트_목록_가져오기(Long 연결_아이디) {

        로그.info("온프레미스 지라 프로젝트 전체목록 가져오기");

        try {
            서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
            JiraRestClient restClient = 지라유틸.온프레미스_통신기_생성(서버정보.getUri(),
                                                                서버정보.getUserId(),
                                                                서버정보.getPasswordOrToken());

            Iterable<BasicProject> 모든_온프레미스_프로젝트 = restClient.getProjectClient()
                                                                    .getAllProjects()
                                                                    .claim();

            List<지라프로젝트_데이터> 반환할_지라프로젝트_목록 = new ArrayList<>();

            for (BasicProject project : 모든_온프레미스_프로젝트) {

                지라프로젝트_데이터 온프레미스_지라프로젝트 = new 지라프로젝트_데이터();
                온프레미스_지라프로젝트.setSelf(project.getSelf().toString());
                온프레미스_지라프로젝트.setId(project.getId().toString());
                온프레미스_지라프로젝트.setKey(project.getKey());
                온프레미스_지라프로젝트.setName(project.getName());

                반환할_지라프로젝트_목록.add(온프레미스_지라프로젝트);
            }

            return 반환할_지라프로젝트_목록;

        } catch (Exception e) {
            로그.error("온프레미스 프로젝트 전체 목록 가져오기에 실패하였습니다." +e.getMessage());
            throw new IllegalArgumentException(에러코드.프로젝트_조회_오류.getErrorMsg());
        }
    }
}
