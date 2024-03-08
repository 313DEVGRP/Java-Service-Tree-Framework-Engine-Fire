package com.arms.api.jira.jiraissue.strategy;

import com.arms.api.jira.jiraissue.model.*;
import com.arms.api.jira.jiraissuestatus.model.지라이슈상태_데이터;
import com.arms.api.jira.jiraissuetype.model.지라이슈유형_데이터;
import com.arms.api.jira.jirapriority.model.지라이슈우선순위_데이터;
import com.arms.api.jira.utils.지라API_정보;
import com.arms.api.serverinfo.model.서버정보_데이터;
import com.arms.api.serverinfo.service.서버정보_서비스;
import com.arms.errors.codes.에러코드;
import com.arms.utils.지라유틸;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static java.util.stream.Collectors.toList;

@Component
public class 레드마인_온프레미스_이슈_전략 implements 지라이슈_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 서버정보_서비스 서버정보_서비스;

    @Autowired
    private 지라유틸 지라유틸;

    @Autowired
    private 지라API_정보 지라API_정보;

    @Override
    public List<지라이슈_데이터> 이슈_목록_가져오기(Long 연결_아이디, String 프로젝트_키_또는_아이디) throws Exception {

        로그.info("레드마인_온프레미스_이슈_전략 "+ 연결_아이디 +" 이슈_목록_가져오기");

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
        RedmineManager 레드마인_매니저 = 지라유틸.레드마인_온프레미스_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        레드마인_매니저.setObjectsPerPage(지라API_정보.getParameter().getMaxResults());
        List<지라이슈_데이터> 지라이슈_목록 = new ArrayList<>();

        try {
            List<Issue> 이슈목록 = 레드마인_매니저.getIssueManager().getIssues(프로젝트_키_또는_아이디, null);
            지라이슈_목록 = 이슈목록.stream().map(이슈 -> {
                        지라이슈_데이터 지라이슈_데이터 = 지라이슈_데이터형_변환(이슈);
                        return 지라이슈_데이터;
                    })
                    .filter(Objects::nonNull)
                    .collect(toList());
        }
        catch (Exception e) {
            로그.error("레드마인 온프레미스 이슈 목록 가져오기에 실패하였습니다." +e.getMessage());
            throw new IllegalArgumentException(에러코드.이슈_조회_오류.getErrorMsg());
        }

        return 지라이슈_목록;
    }

    @Override
    public 지라이슈_데이터 이슈_생성하기(Long 연결_아이디, 지라이슈생성_데이터 지라이슈생성_데이터) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> 이슈_수정하기(Long 연결_아이디, String 이슈_키_또는_아이디, 지라이슈생성_데이터 지라이슈생성_데이터) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> 이슈_삭제_라벨_처리하기(Long 연결_아이디, String 이슈_키_또는_아이디) throws Exception {
        return null;
    }

    @Override
    public 지라이슈_데이터 이슈_상세정보_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) throws Exception {
        return null;
    }

    @Override
    public List<지라이슈_데이터> 이슈링크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) throws URISyntaxException, IOException, ExecutionException, InterruptedException {
        return null;
    }

    @Override
    public List<지라이슈_데이터> 서브테스크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) throws URISyntaxException, IOException, ExecutionException, InterruptedException {
        return null;
    }

    @Override
    public 지라이슈_데이터 증분이슈_상세정보_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) throws Exception {
        return null;
    }

    @Override
    public List<지라이슈_데이터> 증분이슈링크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) throws URISyntaxException, IOException, ExecutionException, InterruptedException {
        return null;
    }

    @Override
    public List<지라이슈_데이터> 증분서브테스크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) throws URISyntaxException, IOException, ExecutionException, InterruptedException {
        return null;
    }

    private 지라이슈_데이터 지라이슈_데이터형_변환(Issue 이슈) {

        지라이슈_데이터 지라이슈_데이터 = new 지라이슈_데이터();
        지라이슈필드_데이터 지라이슈필드_데이터 = new 지라이슈필드_데이터();

        지라이슈_데이터.setId(String.valueOf(이슈.getId()));

        지라이슈필드_데이터.setProject(new 지라프로젝트_데이터(String.valueOf(이슈.getId()), 이슈.getProjectName()));
        지라이슈필드_데이터.setIssuetype(new 지라이슈유형_데이터(String.valueOf(이슈.getTracker().getId()), 이슈.getTracker().getName()));
        지라이슈필드_데이터.setPriority(new 지라이슈우선순위_데이터(String.valueOf(이슈.getPriorityId()), 이슈.getPriorityText()));
        지라이슈필드_데이터.setStatus(new 지라이슈상태_데이터(String.valueOf(이슈.getStatusId()), 이슈.getStatusName()));

        지라이슈필드_데이터.setCreator(new 지라사용자_데이터(String.valueOf(이슈.getAuthorId()), 이슈.getAuthorName()));
        지라이슈필드_데이터.setAssignee(new 지라사용자_데이터(String.valueOf(이슈.getAssigneeId()), 이슈.getAssigneeName()));

        지라이슈필드_데이터.setCreated(String.valueOf(이슈.getCreatedOn()));
        지라이슈필드_데이터.setUpdated(String.valueOf(이슈.getUpdatedOn()));
        지라이슈필드_데이터.setResolutiondate(String.valueOf(이슈.getClosedOn()));

        지라이슈필드_데이터.setSummary(이슈.getSubject());

        지라이슈_데이터.setFields(지라이슈필드_데이터);

        return 지라이슈_데이터;
    }
}
