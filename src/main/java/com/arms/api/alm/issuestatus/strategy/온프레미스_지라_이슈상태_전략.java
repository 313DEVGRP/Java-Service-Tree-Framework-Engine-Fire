package com.arms.api.alm.issuestatus.strategy;

import com.arms.api.alm.issuestatus.model.이슈상태_데이터;
import com.arms.api.alm.utils.지라유틸;
import com.arms.api.serverinfo.model.서버정보_데이터;
import com.arms.api.utils.errors.codes.에러코드;
import com.arms.api.serverinfo.service.서버정보_서비스;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Status;
import io.atlassian.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class 온프레미스_지라_이슈상태_전략 implements 이슈상태_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 서버정보_서비스 서버정보_서비스;

    @Override
    public List<이슈상태_데이터> 이슈상태_목록_가져오기(Long 연결_아이디) throws Exception {

        로그.info("온프레미스 이슈 상태 목록 가져오기");

        try {
            서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
            JiraRestClient restClient = 지라유틸.온프레미스_통신기_생성(서버정보.getUri(),
                                                            서버정보.getUserId(),
                                                            서버정보.getPasswordOrToken());

            Promise<Iterable<Status>> statusesPromise = restClient.getMetadataClient().getStatuses();
            Iterable<Status> statuses = statusesPromise.claim();

            List<이슈상태_데이터> 반환할_이슈상태_데이터_목록 = new ArrayList<>();
            for (Status status : statuses) {
                이슈상태_데이터 이슈상태_데이터 = new 이슈상태_데이터();
                이슈상태_데이터.setSelf(status.getSelf().toString());
                이슈상태_데이터.setId(status.getId().toString());
                이슈상태_데이터.setName(status.getName());
                이슈상태_데이터.setDescription(status.getDescription());
                반환할_이슈상태_데이터_목록.add(이슈상태_데이터);
            }

            return 반환할_이슈상태_데이터_목록;

        } catch (Exception e) {
            로그.error("온프레미스 이슈 상태 목록 조회에 실패하였습니다");
            로그.error(e.getClass().getName() + " : "+ e.getMessage());

            return Collections.emptyList();
        }
    }

    @Override
    public List<이슈상태_데이터> 프로젝트별_이슈상태_목록_가져오기(Long 연결_아이디, String 프로젝트_아이디) throws Exception {

        로그.info("온프레미스 이슈상태_목록_가져오기 실행");

        if (프로젝트_아이디 == null || 프로젝트_아이디.isEmpty()) {
            throw new IllegalArgumentException(에러코드.파라미터_NULL_오류.getErrorMsg());
        }

        try {
            서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
            JiraRestClient restClient = 지라유틸.온프레미스_통신기_생성(서버정보.getUri(),
                                                                서버정보.getUserId(),
                                                                서버정보.getPasswordOrToken());

            Promise<Iterable<Status>> statusesPromise = restClient.getMetadataClient().getStatuses();
            Iterable<Status> statuses = statusesPromise.claim();

            List<이슈상태_데이터> 반환할_이슈상태_데이터_목록 = new ArrayList<>();
            for (Status status : statuses) {
                이슈상태_데이터 이슈상태_데이터 = new 이슈상태_데이터();
                이슈상태_데이터.setSelf(status.getSelf().toString());
                이슈상태_데이터.setId(status.getId().toString());
                이슈상태_데이터.setName(status.getName());
                이슈상태_데이터.setDescription(status.getDescription());

                반환할_이슈상태_데이터_목록.add(이슈상태_데이터);
            }

            return 반환할_이슈상태_데이터_목록;

        } catch (Exception e) {
            로그.error("온프레미스 이슈 상태 목록 조회에 실패하였습니다");
            로그.error(e.getClass().getName() + " : "+ e.getMessage());

            return Collections.emptyList();
        }
    }

}
