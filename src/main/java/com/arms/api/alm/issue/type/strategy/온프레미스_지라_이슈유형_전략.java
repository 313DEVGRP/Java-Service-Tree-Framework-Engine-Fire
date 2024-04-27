package com.arms.api.alm.issue.type.strategy;

import com.arms.api.alm.issue.type.model.이슈유형_데이터;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import com.arms.api.alm.utils.지라유틸;
import com.arms.api.util.errors.codes.에러코드;
import com.arms.api.util.errors.에러로그_유틸;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class 온프레미스_지라_이슈유형_전략 implements 이슈유형_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private 지라유틸 지라유틸;

    @Autowired
    public 온프레미스_지라_이슈유형_전략(지라유틸 지라유틸) {
        this.지라유틸 = 지라유틸;
    }

    @Override
    public List<이슈유형_데이터> 이슈유형_목록_가져오기(서버정보_데이터 서버정보) {

        try {
            JiraRestClient restClient = 지라유틸.온프레미스_통신기_생성(서버정보.getUri(),
                    서버정보.getUserId(),
                    서버정보.getPasswordOrToken());

            Iterable<IssueType> 온프레미스_이슈_유형_목록 = restClient.getMetadataClient().getIssueTypes().get();
            List<이슈유형_데이터> 반환할_이슈_유형_목록 = new ArrayList<>();

            for (IssueType 온프레미스_이슈_유형 : 온프레미스_이슈_유형_목록) {
                이슈유형_데이터 이슈유형_데이터 = new 이슈유형_데이터();

                이슈유형_데이터.setId(온프레미스_이슈_유형.getId().toString());
                이슈유형_데이터.setName(온프레미스_이슈_유형.getName());
                이슈유형_데이터.setSelf(온프레미스_이슈_유형.getName());
                이슈유형_데이터.setSubtask(온프레미스_이슈_유형.isSubtask());
                이슈유형_데이터.setDescription(온프레미스_이슈_유형.getDescription());

                반환할_이슈_유형_목록.add(이슈유형_데이터);
            }

            return 반환할_이슈_유형_목록;

        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 스레드를 다시 interrupt

            에러로그_유틸.예외로그출력(e, this.getClass().getName(),
                    "온프레미스 지라("+ 서버정보.getConnectId() +") :: 이슈유형_목록_가져오기에 실패하였습니다.");
            return Collections.emptyList();
        }
        catch (Exception e) {
            에러로그_유틸.예외로그출력(e, this.getClass().getName(),
                    "온프레미스 지라("+ 서버정보.getConnectId() +") :: 이슈유형_목록_가져오기에 실패하였습니다.");
            return Collections.emptyList();
        }

    }

    @Override
    public List<이슈유형_데이터> 프로젝트별_이슈유형_목록_가져오기(서버정보_데이터 서버정보, String 프로젝트_아이디) {

        if (프로젝트_아이디 == null || 프로젝트_아이디.isEmpty()) {
            throw new IllegalArgumentException("프로젝트_아이디 :: " + 에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        try {
            JiraRestClient restClient = 지라유틸.온프레미스_통신기_생성(서버정보.getUri(),
                                                                            서버정보.getUserId(),
                                                                            서버정보.getPasswordOrToken());

            Iterable<IssueType> 온프레미스_이슈_유형_목록 = restClient.getMetadataClient().getIssueTypes().get();
            List<이슈유형_데이터> 반환할_이슈_유형_목록 = new ArrayList<>();

            for (IssueType 온프레미스_이슈_유형 : 온프레미스_이슈_유형_목록) {
                이슈유형_데이터 이슈유형_데이터 = new 이슈유형_데이터();

                이슈유형_데이터.setId(온프레미스_이슈_유형.getId().toString());
                이슈유형_데이터.setName(온프레미스_이슈_유형.getName());
                이슈유형_데이터.setSelf(온프레미스_이슈_유형.getName());
                이슈유형_데이터.setSubtask(온프레미스_이슈_유형.isSubtask());
                이슈유형_데이터.setDescription(온프레미스_이슈_유형.getDescription());

                반환할_이슈_유형_목록.add(이슈유형_데이터);
            }

            return 반환할_이슈_유형_목록;

        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 스레드를 다시 interrupt

            에러로그_유틸.예외로그출력(e, this.getClass().getName(),
                    "온프레미스 지라("+ 서버정보.getConnectId() +") :: 프로젝트 아이디("+ 프로젝트_아이디 +")별_이슈유형_목록_가져오기에 실패하였습니다.");
            return Collections.emptyList();
        }
        catch (Exception e) {
            에러로그_유틸.예외로그출력(e, this.getClass().getName(),
                    "온프레미스 지라("+ 서버정보.getConnectId() +") :: 프로젝트 아이디("+ 프로젝트_아이디 +")별_이슈유형_목록_가져오기에 실패하였습니다.");
            return Collections.emptyList();
        }
    }
}
