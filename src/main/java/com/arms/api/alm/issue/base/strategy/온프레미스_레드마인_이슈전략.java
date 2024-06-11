package com.arms.api.alm.issue.base.strategy;

import com.arms.api.alm.issue.base.model.*;
import com.arms.api.alm.issue.priority.model.이슈우선순위_데이터;
import com.arms.api.alm.issue.status.model.이슈상태_데이터;
import com.arms.api.alm.issue.status.strategy.온프레미스_레드마인_이슈상태_전략;
import com.arms.api.alm.issue.type.model.이슈유형_데이터;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import com.arms.api.alm.utils.레드마인API_정보;
import com.arms.api.alm.utils.레드마인유틸;
import com.arms.api.util.errors.codes.에러코드;
import com.arms.api.util.errors.에러로그_유틸;
import com.taskadapter.redmineapi.Include;
import com.taskadapter.redmineapi.Params;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueRelation;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.bean.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Component
public class 온프레미스_레드마인_이슈전략 implements 이슈전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private final 레드마인유틸 레드마인유틸;
    private final 레드마인API_정보 레드마인API_정보;

    @Autowired
    public 온프레미스_레드마인_이슈전략(레드마인유틸 레드마인유틸,
                                레드마인API_정보 레드마인API_정보) {
        this.레드마인유틸 = 레드마인유틸;
        this.레드마인API_정보 = 레드마인API_정보;
    }

    @Autowired
    private 온프레미스_레드마인_이슈상태_전략 온프레미스_레드마인_이슈상태_전략;

    @Override
    public List<지라이슈_데이터> 이슈_목록_가져오기(서버정보_데이터 서버정보, String 프로젝트_키_또는_아이디) {

        RedmineManager 레드마인_매니저 = 레드마인유틸.레드마인_온프레미스_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        List<지라이슈_데이터> 반환이슈목록;

        List<Issue> 이슈목록 = null;
        try {
            이슈목록 = 레드마인_매니저.getIssueManager().getIssues(프로젝트_키_또는_아이디, null);
        }
        catch (RedmineException e) {
            에러로그_유틸.예외로그출력(e, this.getClass().getName(),
                    "레드마인_온프레미스 ["+ 서버정보.getUri() +"] :: 프로젝트_키_또는_아이디 :: " + 프로젝트_키_또는_아이디 + "이슈_목록_가져오기 오류");
        }

        if (이슈목록 == null) {
            로그.info("레드마인_온프레미스 ["+ 서버정보.getUri() +"] :: 프로젝트_키_또는_아이디 :: "
                    + 프로젝트_키_또는_아이디+ "의 이슈 목록 조회 데이터가 없습니다.");
            return null;
        }

        반환이슈목록 = 이슈목록.stream().map(이슈 -> {
                    지라이슈_데이터 지라이슈_데이터 = 지라이슈_데이터형_변환(레드마인_매니저, 이슈, 서버정보.getUri());
                    return 지라이슈_데이터;
                })
                .filter(Objects::nonNull)
                .collect(toList());

        return 반환이슈목록;
    }

    @Override
    public 지라이슈_데이터 이슈_생성하기(서버정보_데이터 서버정보, 지라이슈생성_데이터 지라이슈생성_데이터) {

        RedmineManager 레드마인_매니저 = 레드마인유틸.레드마인_온프레미스_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        지라이슈생성필드_데이터 필드_데이터 = 지라이슈생성_데이터.getFields();
        if (필드_데이터 == null) {
            String 에러로그 = 서버정보.getType() + " :: "+ 서버정보.getUri() +" 이슈 생성 필드 데이터가 존재 하지 않습니다.";
            throw new IllegalArgumentException(에러코드.요청본문_오류체크.getErrorMsg() + " :: " + 에러로그);
        }

        지라프로젝트_데이터 프로젝트_데이터 = 필드_데이터.getProject();
        이슈유형_데이터 이슈유형_데이터 = 필드_데이터.getIssuetype();
        이슈우선순위_데이터 우선순위_데이터 = 필드_데이터.getPriority();
        이슈상태_데이터 상태_데이터 = 필드_데이터.getStatus();

        if (프로젝트_데이터 == null) {
            String 에러로그 = "레드마인_온프레미스 ["+ 서버정보.getUri() +"] :: 생성 데이터 :: 프로젝트_데이터 Null :: 이슈_생성하기 오류";
            로그.error(에러로그);
            throw new IllegalArgumentException(에러로그);
        }

        if (이슈유형_데이터 == null) {
            String 에러로그 = "레드마인_온프레미스 ["+ 서버정보.getUri() +"] :: 생성 데이터 :: 이슈유형_데이터 Null :: 이슈_생성하기 오류";
            로그.error(에러로그);
            throw new IllegalArgumentException(에러로그);
        }

        Tracker 이슈타입 = new Tracker()
                .setId(Integer.parseInt(이슈유형_데이터.getId()))
                .setName(이슈유형_데이터.getName());

        Issue 생성이슈 = new Issue(레드마인_매니저.getTransport(), Integer.parseInt(프로젝트_데이터.getId()))
                .setSubject(필드_데이터.getSummary())
                .setTracker(이슈타입)
                .setDescription(필드_데이터.getDescription());

        if (우선순위_데이터 != null && !우선순위_데이터.getId().isEmpty()) {
            생성이슈.setPriorityId(Integer.parseInt(필드_데이터.getPriority().getId()));
        }

        if (필드_데이터.getStartDate() != null) {
            생성이슈.setStartDate(필드_데이터.getStartDate());
        }

        if (필드_데이터.getDueDate() != null) {
            생성이슈.setDueDate(필드_데이터.getDueDate());
        }

        if (상태_데이터 != null && !상태_데이터.getId().isEmpty()) {
            if (이슈_상태_검증하기(서버정보, 상태_데이터.getId())) {
                생성이슈.setStatusId(Integer.parseInt(상태_데이터.getId()));
            }
        }

        try {
            생성이슈 = 생성이슈.create();
        }
        catch (RedmineException e) {
            String 에러로그 = 에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "레드마인_온프레미스 ["+ 서버정보.getUri() +"] :: 생성 데이터 :: " + 지라이슈생성_데이터.toString() + "이슈_생성하기 오류");
            throw new IllegalArgumentException(this.getClass().getName() + " :: " + 에러코드.이슈생성_오류.getErrorMsg() + " :: " + 에러로그);
        }

        지라이슈_데이터 이슈_데이터 = 지라이슈_데이터형_변환(레드마인_매니저, 생성이슈, 서버정보.getUri());
        return 이슈_데이터;
    }

    @Override
    public Map<String, Object> 이슈_수정하기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디, 지라이슈생성_데이터 지라이슈생성_데이터) {

        RedmineManager 레드마인_매니저 = 레드마인유틸.레드마인_온프레미스_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        Map<String, Object> 결과 = new HashMap<>();
        지라이슈생성필드_데이터 필드_데이터 = 지라이슈생성_데이터.getFields();
        String 제목 = 필드_데이터.getSummary();
        String 내용 = 필드_데이터.getDescription();
        이슈상태_데이터 상태_데이터 = 필드_데이터.getStatus();

        try {
            Issue 수정이슈 = 레드마인_매니저.getIssueManager().getIssueById(Integer.parseInt(이슈_키_또는_아이디));

            if (제목 != null && !제목.isEmpty()) {
                수정이슈.setSubject(제목);
            }
            if (내용 != null) {
                수정이슈.setDescription(내용);
            }

            if (상태_데이터 != null && !상태_데이터.getId().isEmpty()) {
                if (이슈_상태_검증하기(서버정보, 상태_데이터.getId())) {
                    수정이슈.setStatusId(Integer.parseInt(상태_데이터.getId()));
                }
            }

            수정이슈.update();

            결과.put("success", true);
            결과.put("message", "이슈 수정 성공");
        }
        catch (RedmineException e) {
            String 에러로그 = 에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "레드마인_온프레미스 ["+ 서버정보.getUri() +"] :: 이슈_키_또는_아이디 :: "
                            + 이슈_키_또는_아이디+ " :: 수정데이터 :: " + 필드_데이터.toString() + "이슈_수정하기 오류");
            로그.error(에러로그);

            결과.put("success", false);
            결과.put("message", 에러로그);
        }

        return 결과;
    }

    @Override
    public Map<String, Object> 이슈_상태_변경하기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디, String 상태_아이디) {

        RedmineManager 레드마인_매니저 = 레드마인유틸.레드마인_온프레미스_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        Map<String, Object> 결과 = new HashMap<>();

        try {
            Issue 수정이슈 = 레드마인_매니저.getIssueManager().getIssueById(Integer.parseInt(이슈_키_또는_아이디));

            boolean 상태_아이디_확인 = 이슈_상태_검증하기(서버정보, 상태_아이디);

            if (상태_아이디_확인) {
                수정이슈.setStatusId(Integer.parseInt(상태_아이디));
                수정이슈.update();
                결과.put("success", true);
                결과.put("message", "이슈 상태 변경 성공");
            } else {
                결과.put("success", false);
                결과.put("message", "유효하지 않은 상태 아이디 (" + 상태_아이디 + ")");
            }

        }
        catch (RedmineException e) {
            String 에러로그 = 에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "레드마인_온프레미스 [" + 서버정보.getUri() + "] :: 이슈_키_또는_아이디 :: "
                            + 이슈_키_또는_아이디 + " :: 수정데이터 :: " + 상태_아이디 + "이슈_상태_변경하기 오류");
            로그.error(에러로그);

            결과.put("success", false);
            결과.put("message", 에러로그);
        }

        return 결과;
    }

    public boolean 이슈_상태_검증하기(서버정보_데이터 서버정보, String 상태_아이디) {

        boolean 상태_아이디_확인 = false;

        List<이슈상태_데이터> 상태목록 = 온프레미스_레드마인_이슈상태_전략.이슈상태_목록_가져오기(서버정보);

        if (상태목록 != null) {
            for (이슈상태_데이터 상태_데이터 : 상태목록) {
                if (StringUtils.equals(상태_데이터.getId(), 상태_아이디)) {
                    상태_아이디_확인 = true;
                    break;
                }
            }
        }

        return 상태_아이디_확인;
    }

    @Override
    public Map<String, Object> 이슈_삭제하기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디) {

        Map<String, Object> 결과 = new HashMap<>();
        RedmineManager 레드마인_매니저 = 레드마인유틸.레드마인_온프레미스_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        try {
            레드마인_매니저.getIssueManager().deleteIssue(Integer.valueOf(이슈_키_또는_아이디));

            결과.put("success", true);
            결과.put("message", "이슈 삭제 성공");
        }
        catch (RedmineException e) {
            String 에러로그 = 에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "레드마인_온프레미스 ["+ 서버정보.getUri() +"] :: 이슈_키_또는_아이디 :: "
                            + 이슈_키_또는_아이디+ " :: 이슈_삭제하기 오류");
            로그.error(에러로그);

            결과.put("success", false);
            결과.put("message", 에러로그);
        }

        return 결과;
    }

    @Override
    public 지라이슈_데이터 이슈_상세정보_가져오기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디) {

        RedmineManager 레드마인_매니저 = 레드마인유틸.레드마인_온프레미스_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        Issue 조회할_이슈 = null;
        try {
            조회할_이슈 = 레드마인_매니저.getIssueManager().getIssueById(Integer.parseInt(이슈_키_또는_아이디));
        }
        catch (RedmineException e) {
            에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "레드마인_온프레미스 ["+ 서버정보.getUri() +"] :: 이슈_키_또는_아이디 :: "
                            + 이슈_키_또는_아이디+ " :: 이슈_상세정보_가져오기 오류");
        }

        if (조회할_이슈 == null) {
            로그.info("레드마인_온프레미스 ["+ 서버정보.getUri() +"] :: 이슈_키_또는_아이디 :: "
                    + 이슈_키_또는_아이디 + " :: 상세 정보 데이터가 없습니다.");
            return null;
        }

        지라이슈_데이터 이슈_데이터 = 지라이슈_데이터형_변환(레드마인_매니저, 조회할_이슈, 서버정보.getUri());
        return 이슈_데이터;
    }

    @Override
    public List<지라이슈_데이터> 이슈링크_가져오기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디) {

        RedmineManager 레드마인_매니저 = 레드마인유틸.레드마인_온프레미스_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        Issue 부모이슈 = null;
        try {
            부모이슈 = 레드마인_매니저.getIssueManager().getIssueById(Integer.parseInt(이슈_키_또는_아이디), Include.relations);
        }
        catch (RedmineException e) {
            에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "레드마인_온프레미스 ["+ 서버정보.getUri() +"] :: 이슈_키_또는_아이디 :: "
                            + 이슈_키_또는_아이디+ " :: 이슈링크_가져오기 오류");
        }

        if (부모이슈 == null || 부모이슈.getRelations() == null || 부모이슈.getRelations().isEmpty()) {
            로그.info("레드마인_온프레미스 ["+ 서버정보.getUri() +"] :: 이슈_키_또는_아이디 :: "
                    + 이슈_키_또는_아이디 + " :: 이슈링크 데이터가 없습니다.");
            return null;
        }

        List<지라이슈_데이터> 이슈_목록 = new ArrayList<>();
        for (IssueRelation 연관이슈 : 부모이슈.getRelations()) {

            String 연관이슈_아이디;

            if (이슈_키_또는_아이디.equals(String.valueOf(연관이슈.getIssueId()))) {
                연관이슈_아이디 = String.valueOf(연관이슈.getIssueToId());
            } else {
                연관이슈_아이디 = String.valueOf(연관이슈.getIssueId());
            }

            Optional.ofNullable(연관이슈_아이디)
                    .map(이슈_아이디 -> 이슈_상세정보_가져오기(서버정보, 이슈_아이디))
                    .ifPresent(이슈_목록::add);
        }

        return 이슈_목록;
    }

    @Override
    public List<지라이슈_데이터> 서브테스크_가져오기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디) {

        RedmineManager 레드마인_매니저 = 레드마인유틸.레드마인_온프레미스_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        Params params = new Params()
                .add("parent_id", 이슈_키_또는_아이디)
                .add("status_id", "*");

        List<Issue> 조회된_하위이슈_목록 = null;
        try {
            조회된_하위이슈_목록 = 레드마인_매니저.getIssueManager().getIssues(params).getResults();
        }
        catch (RedmineException e) {
            에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "레드마인_온프레미스 ["+ 서버정보.getUri() +"] :: 이슈_키_또는_아이디 :: "
                            + 이슈_키_또는_아이디+ " :: 서브테스크_가져오기 오류");
        }

        if (조회된_하위이슈_목록 == null || 조회된_하위이슈_목록.isEmpty()) {
            로그.info("레드마인_온프레미스 ["+ 서버정보.getUri() +"] :: 이슈_키_또는_아이디 :: "
                    + 이슈_키_또는_아이디 + " :: 서브테스크 이슈 데이터가 없습니다.");
            return null;
        }

        List<지라이슈_데이터> 하위이슈_목록 = 조회된_하위이슈_목록.stream().map(이슈 -> {
                    지라이슈_데이터 지라이슈_데이터 = 지라이슈_데이터형_변환(레드마인_매니저, 이슈, 서버정보.getUri());
                    return 지라이슈_데이터;
                })
                .filter(Objects::nonNull)
                .collect(toList());

        return 하위이슈_목록;
    }

    @Override
    public 지라이슈_데이터 증분이슈_상세정보_가져오기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디) {

        RedmineManager 레드마인_매니저 = 레드마인유틸.레드마인_온프레미스_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        Issue 조회할_이슈;
        try {
            조회할_이슈 = 레드마인_매니저.getIssueManager().getIssueById(Integer.parseInt(이슈_키_또는_아이디));
        }
        catch (RedmineException e) {
            에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "레드마인_온프레미스 ["+ 서버정보.getUri() +"] :: 이슈_키_또는_아이디 :: "
                            + 이슈_키_또는_아이디+ " :: 증분이슈_상세정보_가져오기 오류");
            return null;
        }

        // 이슈 업데이트 날짜 확인
        LocalDate 업데이트_날짜 = 조회할_이슈.getUpdatedOn().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();

        지라이슈_데이터 이슈_데이터;
        // 업데이트 날짜와 어제 날짜 비교
        if (업데이트_날짜.equals(어제날짜_얻기())) {
            이슈_데이터 = 지라이슈_데이터형_변환(레드마인_매니저, 조회할_이슈, 서버정보.getUri());
        }
        else {
            로그.info("레드마인_온프레미스 ["+ 서버정보.getUri() +"] :: 이슈_키_또는_아이디 :: "
                    + 이슈_키_또는_아이디+ "는 업데이트 되지 않은 이슈입니다");
            return null;
        }

        return 이슈_데이터;
    }

    @Override
    public List<지라이슈_데이터> 증분이슈링크_가져오기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디) {

        RedmineManager 레드마인_매니저 = 레드마인유틸.레드마인_온프레미스_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        Issue 부모이슈 = null;
        try {
            부모이슈 = 레드마인_매니저.getIssueManager().getIssueById(Integer.parseInt(이슈_키_또는_아이디), Include.relations);
        }
        catch (RedmineException e) {
            에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "레드마인_온프레미스 ["+ 서버정보.getUri() +"] :: 이슈_키_또는_아이디 :: "
                            + 이슈_키_또는_아이디+ " :: 증분이슈링크_가져오기 오류");
        }

        if (부모이슈 == null || 부모이슈.getRelations().isEmpty()) {
            로그.info("레드마인_온프레미스 ["+ 서버정보.getUri() +"] :: 이슈_키_또는_아이디 :: "
                    + 이슈_키_또는_아이디+ " 연관된 이슈가 없습니다");
            return null;
        }

        List<지라이슈_데이터> 이슈_목록 = new ArrayList<>();
        for (IssueRelation 연관이슈 : 부모이슈.getRelations()) {

            String 연관이슈_아이디;

            if (이슈_키_또는_아이디.equals(String.valueOf(연관이슈.getIssueId()))) {
                연관이슈_아이디 = String.valueOf(연관이슈.getIssueToId());
            }
            else {
                연관이슈_아이디 = String.valueOf(연관이슈.getIssueId());
            }

            Optional.ofNullable(연관이슈_아이디)
                    .map(이슈_아이디 -> 증분이슈_상세정보_가져오기(서버정보, 이슈_아이디))
                    .ifPresent(이슈_목록::add);
        }

        return 이슈_목록;
    }

    @Override
    public List<지라이슈_데이터> 증분서브테스크_가져오기(서버정보_데이터 서버정보, String 이슈_키_또는_아이디) {

        RedmineManager 레드마인_매니저 = 레드마인유틸.레드마인_온프레미스_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        Params params = new Params()
                .add("parent_id", 이슈_키_또는_아이디)
                .add("status_id", "*")
                .add("updated_on", "><" + 어제날짜_얻기() + "|" + 어제날짜_얻기());

        List<Issue> 조회된_하위이슈_목록 = null;
        try {
            조회된_하위이슈_목록 = 레드마인_매니저.getIssueManager().getIssues(params).getResults();
        }
        catch (RedmineException e) {
            에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "레드마인_온프레미스 ["+ 서버정보.getUri() +"] :: 이슈_키_또는_아이디 :: "
                            + 이슈_키_또는_아이디+ " :: 증분서브테스크_가져오기 오류");
        }

        if (조회된_하위이슈_목록 == null || 조회된_하위이슈_목록.isEmpty()) {
            로그.info("레드마인_온프레미스 ["+ 서버정보.getUri() +"] :: 이슈_키_또는_아이디 :: "
                    + 이슈_키_또는_아이디+ "는 업데이트된 서브테스크 이슈가 없습니다.");
            return null;
        }

        List<지라이슈_데이터> 하위이슈_목록 = 조회된_하위이슈_목록.stream().map(이슈 -> {
                    지라이슈_데이터 지라이슈_데이터 = 지라이슈_데이터형_변환(레드마인_매니저, 이슈, 서버정보.getUri());
                    return 지라이슈_데이터;
                })
                .filter(Objects::nonNull)
                .collect(toList());

        return 하위이슈_목록;
    }

    private 지라이슈_데이터 지라이슈_데이터형_변환(RedmineManager 레드마인_매니저, Issue 이슈, String 서버정보경로) {

        지라이슈_데이터 지라이슈_데이터 = new 지라이슈_데이터();
        지라이슈필드_데이터 지라이슈필드_데이터 = new 지라이슈필드_데이터();
        String 기본경로 = 레드마인유틸.서버정보경로_체크(서버정보경로);

        Optional.ofNullable(이슈.getId())
                .map(아이디 -> String.valueOf(아이디))
                .ifPresent(아이디 -> {
                    지라이슈_데이터.setId(아이디);
                    지라이슈_데이터.setKey(아이디);
                    지라이슈_데이터.setSelf(기본경로 + 레드마인API_정보.아이디_대체하기(레드마인API_정보.getEndpoint().getIssue(), 아이디));
                });

        Optional.ofNullable(이슈.getProjectId())
                .map(아이디 -> String.valueOf(아이디))
                .ifPresent(아이디 -> {
                    String 프로젝트_경로 = 기본경로 + 레드마인API_정보.아이디_대체하기(레드마인API_정보.getEndpoint().getProject(), 아이디);
                    지라이슈필드_데이터.setProject(new 지라프로젝트_데이터(프로젝트_경로, 아이디, 아이디, 이슈.getProjectName()));
                });

        Optional.ofNullable(이슈.getTracker().getId())
                .map(아이디 -> String.valueOf(아이디))
                .ifPresent(아이디 -> {
                    String 이슈유형_경로 = 기본경로 + 레드마인API_정보.아이디_대체하기(레드마인API_정보.getEndpoint().getIssuetype(), 아이디);
                    지라이슈필드_데이터.setIssuetype(new 이슈유형_데이터(이슈유형_경로, 아이디, 이슈.getTracker().getName()));
                });

        Optional.ofNullable(이슈.getPriorityId())
                .map(아이디 -> String.valueOf(아이디))
                .ifPresent(아이디 -> {
                    String 우선순위_경로 = 기본경로 + 레드마인API_정보.아이디_대체하기(레드마인API_정보.getEndpoint().getPriority(), 아이디);
                    지라이슈필드_데이터.setPriority(new 이슈우선순위_데이터(우선순위_경로, 아이디, 이슈.getPriorityText()));
                });

        Optional.ofNullable(이슈.getStatusId())
                .map(아이디 -> String.valueOf(아이디))
                .ifPresent(아이디 -> {
                    String 이슈상태_경로 = 기본경로 + 레드마인API_정보.아이디_대체하기(레드마인API_정보.getEndpoint().getIssuestatus(), 아이디);
                    지라이슈필드_데이터.setStatus(new 이슈상태_데이터(이슈상태_경로, 아이디, 이슈.getStatusName()));
                });

        Optional.ofNullable(이슈.getAuthorId())
                .map(아이디 -> String.valueOf(아이디))
                .ifPresent(아이디 -> {
                    지라사용자_데이터 사용자_데이터 = new 지라사용자_데이터(아이디, 이슈.getAuthorName());
                    지라이슈필드_데이터.setCreator(사용자_데이터);
                    지라이슈필드_데이터.setReporter(사용자_데이터);
                });

        Optional.ofNullable(이슈.getAssigneeId())
                .map(아이디 -> String.valueOf(아이디))
                .ifPresent(아이디 -> {
                    User 사용자정보 = 레드마인유틸.사용자정보_조회(레드마인_매니저, 아이디);

                    if (사용자정보 != null && !사용자정보.getMail().isEmpty()) {
                        지라이슈필드_데이터.setAssignee(
                                new 지라사용자_데이터(아이디, 사용자정보.getMail(), 이슈.getAssigneeName()));
                    }
                    else {
                        지라이슈필드_데이터.setAssignee(new 지라사용자_데이터(아이디, 이슈.getAssigneeName()));
                    }
                });

        Optional.ofNullable(이슈.getCreatedOn())
                .map(생성일 -> 날짜변환(생성일))
                .ifPresent(지라이슈필드_데이터::setCreated);

        Optional.ofNullable(이슈.getUpdatedOn())
                .map(업데이트일 -> 날짜변환(업데이트일))
                .ifPresent(지라이슈필드_데이터::setUpdated);

        Optional.ofNullable(이슈.getClosedOn())
                .map(해결책 -> 날짜변환(해결책))
                .ifPresent(지라이슈필드_데이터::setResolutiondate);

        지라이슈필드_데이터.setSummary(이슈.getSubject());

        지라이슈_데이터.setFields(지라이슈필드_데이터);

        return 지라이슈_데이터;
    }

    private String 날짜변환(Date 원본_날짜) {

        // 원본 형식
        DateTimeFormatter originalFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

        // ZonedDateTime 객체로 파싱
        ZonedDateTime 날짜시간 = ZonedDateTime.parse(String.valueOf(원본_날짜), originalFormatter);

        // ISO 8601 형식으로 변환
        String 변환된_날짜시간 = 날짜시간.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        return 변환된_날짜시간;
    }

    private LocalDate 어제날짜_얻기() {
        return LocalDate.now().minusDays(1);
    }
}
