package com.arms.api.alm.issue.type.strategy;

import com.arms.api.alm.issue.type.model.이슈유형_데이터;
import com.arms.api.alm.utils.지라유틸;
import com.arms.api.utils.errors.codes.에러코드;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import com.arms.api.alm.serverinfo.service.서버정보_서비스;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class 온프레미스_지라_이슈유형_전략 implements 이슈유형_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 서버정보_서비스 서버정보_서비스;

    @Override
    public List<이슈유형_데이터> 이슈유형_목록_가져오기(Long 연결_아이디) throws URISyntaxException, IOException, ExecutionException, InterruptedException {

        로그.info("온프레미스 지라 이슈유형_목록_가져오기");

        try {
            서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
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

            로그.info(반환할_이슈_유형_목록.toString());

            return 반환할_이슈_유형_목록;

        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 스레드를 다시 interrupt

            로그.error("온프레미스 지라 이슈 유형 목록 가져오기 가져오기에 실패하였습니다.");
            로그.error(e.getClass().getName() + " : "+ e.getMessage());

            return Collections.emptyList();
        }
        catch (Exception e) {
            로그.error("온프레미스 지라 이슈 유형 목록 가져오기 가져오기에 실패하였습니다.");
            로그.error(e.getClass().getName() + " : "+ e.getMessage());

            return Collections.emptyList();
        }

    }

    @Override
    public List<이슈유형_데이터> 프로젝트별_이슈유형_목록_가져오기(Long 연결_아이디, String 프로젝트_아이디) throws URISyntaxException, IOException, ExecutionException, InterruptedException {

        로그.info("온프레미스는 전역 지라 이슈유형_목록_가져오기");

        if (프로젝트_아이디 == null || 프로젝트_아이디.isEmpty()) {
            throw new IllegalArgumentException(에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        try {
            서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
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

            로그.info(반환할_이슈_유형_목록.toString());

            return 반환할_이슈_유형_목록;

        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 스레드를 다시 interrupt

            로그.error("온프레미스 지라 프로젝트 아이디("+ 프로젝트_아이디 +")별_이슈유형_목록_가져오기에 실패하였습니다.");
            로그.error(e.getClass().getName() + " : "+ e.getMessage());

            return Collections.emptyList();
        }
        catch (Exception e) {
            로그.error("온프레미스 지라 프로젝트 아이디("+ 프로젝트_아이디 +")별_이슈유형_목록_가져오기에 실패하였습니다.");
            로그.error(e.getClass().getName() + " : "+ e.getMessage());

            return Collections.emptyList();
        }
    }
}
