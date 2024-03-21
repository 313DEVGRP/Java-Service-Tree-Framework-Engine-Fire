package com.arms.api.alm.issue.strategy;

import com.arms.api.alm.issue.model.*;
import com.arms.api.alm.issuestatus.model.지라이슈상태_데이터;
import com.arms.api.alm.issuetype.model.지라이슈유형_데이터;
import com.arms.api.alm.priority.model.지라이슈우선순위_데이터;
import com.arms.utils.errors.에러로그_유틸;
import com.arms.api.serverinfo.model.서버정보_데이터;
import com.arms.api.serverinfo.service.서버정보_서비스;
import com.arms.utils.errors.codes.에러코드;
import com.arms.api.alm.utils.지라유틸;
import com.taskadapter.redmineapi.Include;
import com.taskadapter.redmineapi.Params;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueRelation;
import com.taskadapter.redmineapi.bean.Tracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static java.util.stream.Collectors.toList;

@Component
public class 온프레미스_레드마인_이슈전략 implements 이슈전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 서버정보_서비스 서버정보_서비스;

    @Autowired
    private 지라유틸 지라유틸;

    @Override
    public List<지라이슈_데이터> 이슈_목록_가져오기(Long 연결_아이디, String 프로젝트_키_또는_아이디) {

        로그.info("레드마인_온프레미스_이슈_전략 "+ 연결_아이디 +" 이슈_목록_가져오기");

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
        RedmineManager 레드마인_매니저 = 지라유틸.레드마인_온프레미스_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        List<지라이슈_데이터> 반환이슈목록;

        List<Issue> 이슈목록 = null;
        try {
            이슈목록 = 레드마인_매니저.getIssueManager().getIssues(프로젝트_키_또는_아이디, null);
        } catch (RedmineException e) {
            에러로그_유틸.예외로그출력(e, this.getClass().getName(), "이슈_목록_가져오기");
        }

        if (이슈목록 == null) {
            로그.info(연결_아이디 +" :: 이슈_목록_가져오기 :: " + 프로젝트_키_또는_아이디 + " 프로젝트 이슈 목록 조회 데이터가 없습니다.");
            return null;
        }

        반환이슈목록 = 이슈목록.stream().map(이슈 -> {
                        지라이슈_데이터 지라이슈_데이터 = 지라이슈_데이터형_변환(이슈, 서버정보.getUri());
                        return 지라이슈_데이터;
                    })
                    .filter(Objects::nonNull)
                    .collect(toList());

        return 반환이슈목록;
    }

    @Override
    public 지라이슈_데이터 이슈_생성하기(Long 연결_아이디, 지라이슈생성_데이터 지라이슈생성_데이터) {

        로그.info("레드마인_온프레미스_이슈_전략 "+ 연결_아이디 +" 이슈_생성하기");

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
        RedmineManager 레드마인_매니저 = 지라유틸.레드마인_온프레미스_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        지라이슈_데이터 이슈_데이터 = null;
        지라이슈생성필드_데이터 필드_데이터 = 지라이슈생성_데이터.getFields();
        지라프로젝트_데이터 프로젝트_데이터 = 필드_데이터.getProject();
        지라이슈유형_데이터 이슈유형_데이터 = 필드_데이터.getIssuetype();
        지라이슈우선순위_데이터 우선순위_데이터 = 필드_데이터.getPriority();
/*
        try {

            // 3.1.3 버전

            Issue 생성할_이슈 = IssueFactory.create(Integer.parseInt(프로젝트_데이터.getId()), 필드_데이터.getSummary());
            Tracker 이슈타입 = TrackerFactory.create(Integer.parseInt(이슈유형_데이터.getId()), 이슈유형_데이터.getName());
            생성할_이슈.setTracker(이슈타입);
            생성할_이슈.setDescription(필드_데이터.getDescription());
            생성할_이슈.setPriorityId(Integer.parseInt(우선순위_데이터.getId()));

            Issue 생성된_이슈 = 레드마인_매니저.getIssueManager().createIssue(생성할_이슈);
            이슈_데이터 = 지라이슈_데이터형_변환(생성된_이슈, 서버정보.getUri());

        }
        catch (Exception e) {
            로그.error("레드마인 온프레미스 이슈 생성하기에 실패하였습니다." +e.getMessage());
            throw new IllegalArgumentException(에러코드.이슈생성_오류.getErrorMsg());
        }
*/

        Tracker 이슈타입 = new Tracker()
                .setId(Integer.parseInt(이슈유형_데이터.getId()))
                .setName(이슈유형_데이터.getName());

        Issue 생성이슈;
        try {
            생성이슈 = new Issue(레드마인_매니저.getTransport(), Integer.parseInt(프로젝트_데이터.getId()))
                    .setSubject(필드_데이터.getSummary())
                    .setTracker(이슈타입)
                    .setDescription(필드_데이터.getDescription())
                    .setPriorityId(Integer.parseInt(우선순위_데이터.getId()))
                    .create();
        }
        catch (RedmineException e) {
            에러로그_유틸.예외로그출력(e, this.getClass().getName(), "이슈_생성하기");
            throw new IllegalArgumentException(this.getClass().getName() + " :: " + 에러코드.이슈생성_오류.getErrorMsg() + " :: " + e.getMessage());
        }

        이슈_데이터 = 지라이슈_데이터형_변환(생성이슈, 서버정보.getUri());
        return 이슈_데이터;
    }

    @Override
    public Map<String, Object> 이슈_수정하기(Long 연결_아이디, String 이슈_키_또는_아이디, 지라이슈생성_데이터 지라이슈생성_데이터) throws Exception {

        로그.info("레드마인_온프레미스_이슈_전략 :: "+ 연결_아이디 + " :: "
                + 이슈_키_또는_아이디 +" :: 이슈_수정하기");

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
        RedmineManager 레드마인_매니저 = 지라유틸.레드마인_온프레미스_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        Map<String, Object> 결과 = new HashMap<>();
        지라이슈생성필드_데이터 필드_데이터 = 지라이슈생성_데이터.getFields();
        String 제목 = 필드_데이터.getSummary();
        String 내용 = 필드_데이터.getDescription();

        try {
            Issue 수정이슈 = 레드마인_매니저.getIssueManager().getIssueById(Integer.parseInt(이슈_키_또는_아이디));

            if (제목 != null && !제목.isEmpty()) {
                수정이슈.setSubject(제목);
            }
            if (내용 != null) {
                수정이슈.setDescription(내용);
            }

            수정이슈.update();

            결과.put("success", true);
            결과.put("message", "이슈 수정 성공");

        } catch (Exception e) {
            로그.error("레드마인 온프레미스 이슈 수정하기에 실패하였습니다." +e.getMessage());
            결과.put("success", false);
            결과.put("message", "이슈 수정 실패");
            //throw new IllegalArgumentException(에러코드.이슈수정_오류.getErrorMsg());
        }

        return 결과;
    }

    @Override
    public Map<String, Object> 이슈_삭제_라벨_처리하기(Long 연결_아이디, String 이슈_키_또는_아이디) throws Exception {
        return null;
    }

    @Override
    public 지라이슈_데이터 이슈_상세정보_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) {

        로그.info("레드마인_온프레미스_이슈_전략 :: "+ 연결_아이디 + " :: "
                + 이슈_키_또는_아이디 +" :: 이슈_상세정보_가져오기");

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
        RedmineManager 레드마인_매니저 = 지라유틸.레드마인_온프레미스_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        지라이슈_데이터 이슈_데이터;
        Issue 조회할_이슈 = null;

        try {
            조회할_이슈 = 레드마인_매니저.getIssueManager().getIssueById(Integer.parseInt(이슈_키_또는_아이디));
        } catch (RedmineException e) {
            에러로그_유틸.예외로그출력(e, this.getClass().getName(), "이슈_상세정보_가져오기");
        }

        if (조회할_이슈 == null) {
            로그.info(이슈_키_또는_아이디 + "는 조회할 수 없습니다.");
            return null;
        }

        이슈_데이터 = 지라이슈_데이터형_변환(조회할_이슈, 서버정보.getUri());
        return 이슈_데이터;
    }

    @Override
    public List<지라이슈_데이터> 이슈링크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) throws URISyntaxException, IOException, ExecutionException, InterruptedException {

        로그.info("레드마인_온프레미스_이슈_전략 :: "+ 연결_아이디 + " :: "
                + 이슈_키_또는_아이디 +" :: 이슈링크_가져오기");

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
        RedmineManager 레드마인_매니저 = 지라유틸.레드마인_온프레미스_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        List<지라이슈_데이터> 이슈_목록 = new ArrayList<>();
        Issue 부모이슈 = null;

        try {
            부모이슈 = 레드마인_매니저.getIssueManager().getIssueById(Integer.parseInt(이슈_키_또는_아이디), Include.relations);
        } catch (RedmineException e) {
            에러로그_유틸.예외로그출력(e, this.getClass().getName(), "이슈링크_가져오기");
        }

        if (부모이슈 == null || 부모이슈.getRelations().isEmpty()) {
            로그.info(이슈_키_또는_아이디 + "에 연관된 이슈가 없습니다.");
            return null;
        }

        for (IssueRelation 연관이슈 : 부모이슈.getRelations()) {
            //System.out.println("연관이슈: " + 연관이슈);
            이슈_목록.add(이슈_상세정보_가져오기(연결_아이디, String.valueOf(연관이슈.getIssueToId())));
        }

        return 이슈_목록;
    }

    @Override
    public List<지라이슈_데이터> 서브테스크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) {
        로그.info("레드마인_온프레미스_이슈_전략 :: "+ 연결_아이디 + " :: "
                + 이슈_키_또는_아이디 + " :: 서브테스크_가져오기");

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
        RedmineManager 레드마인_매니저 = 지라유틸.레드마인_온프레미스_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        Params params = new Params()
                .add("parent_id", 이슈_키_또는_아이디)
                .add("status_id", "*");

        List<지라이슈_데이터> 하위이슈_목록;
        List<Issue> 조회된_하위이슈_목록 = null;
        try {
            조회된_하위이슈_목록 = 레드마인_매니저.getIssueManager().getIssues(params).getResults();
        }
        catch (RedmineException e) {
            에러로그_유틸.예외로그출력(e, this.getClass().getName(), "서브테스크_가져오기");
        }

        if (조회된_하위이슈_목록 == null || 조회된_하위이슈_목록.isEmpty()) {
            로그.info(이슈_키_또는_아이디 + "에 하위 이슈가 없습니다.");
            return null;
        }

        하위이슈_목록 = 조회된_하위이슈_목록.stream().map(이슈 -> {
                        지라이슈_데이터 지라이슈_데이터 = 지라이슈_데이터형_변환(이슈, 서버정보.getUri());
                        return 지라이슈_데이터;
                    })
                    .filter(Objects::nonNull)
                    .collect(toList());

        return 하위이슈_목록;
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
    public List<지라이슈_데이터> 증분서브테스크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디) {

        로그.info("레드마인_온프레미스_이슈_전략 :: "+ 연결_아이디 + " :: "
                + 이슈_키_또는_아이디 + " :: 증분서브테스크_가져오기");

        서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
        RedmineManager 레드마인_매니저 = 지라유틸.레드마인_온프레미스_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        // 어제 날짜 포맷팅
        LocalDate 어제 = LocalDate.now().minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        String 어제_날짜 = 어제.format(formatter);

        Params params = new Params()
                .add("parent_id", 이슈_키_또는_아이디)
                .add("status_id", "*")
                .add("updated_on", "><" + 어제_날짜 + "|" + 어제_날짜);

        List<지라이슈_데이터> 하위이슈_목록;
        List<Issue> 조회된_하위이슈_목록 = null;
        try {
            조회된_하위이슈_목록 = 레드마인_매니저.getIssueManager().getIssues(params).getResults();
        }
        catch (RedmineException e) {
            에러로그_유틸.예외로그출력(e, this.getClass().getName(), "증분서브테스크_가져오기");
        }

        if (조회된_하위이슈_목록 == null || 조회된_하위이슈_목록.isEmpty()) {
            로그.info(이슈_키_또는_아이디 + "에 업데이트된 하위 이슈가 없습니다.");
            return null;
        }

        하위이슈_목록 = 조회된_하위이슈_목록.stream().map(이슈 -> {
                    지라이슈_데이터 지라이슈_데이터 = 지라이슈_데이터형_변환(이슈, 서버정보.getUri());
                    return 지라이슈_데이터;
                })
                .filter(Objects::nonNull)
                .collect(toList());

        return 하위이슈_목록;
    }

    private 지라이슈_데이터 지라이슈_데이터형_변환(Issue 이슈, String 서버정보경로) {

        지라이슈_데이터 지라이슈_데이터 = new 지라이슈_데이터();
        지라이슈필드_데이터 지라이슈필드_데이터 = new 지라이슈필드_데이터();

        지라이슈_데이터.setId(String.valueOf(이슈.getId()));
        지라이슈_데이터.setSelf(지라유틸.서버정보경로_체크(서버정보경로) + "/issues/" + 이슈.getId() + ".json");

        지라이슈필드_데이터.setProject(new 지라프로젝트_데이터(String.valueOf(이슈.getProjectId()), 이슈.getProjectName()));
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
