package com.arms.jira.jiraissuestatus.strategy;

import com.arms.errors.codes.에러코드;

import com.arms.jira.jiraissuestatus.model.지라이슈상태_데이터;
import com.arms.jira.utils.지라유틸;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Status;
import io.atlassian.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.arms.serverinfo.model.서버정보_데이터;
import com.arms.serverinfo.service.서버정보_서비스;
import java.util.ArrayList;
import java.util.List;

@Component
public class 온프레미스_지라이슈상태_전략 implements 지라이슈상태_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

//    @Autowired
//    private 지라연결_서비스 지라연결_서비스;

    @Autowired
    private 서버정보_서비스 서버정보_서비스;

    @Override
    public List<지라이슈상태_데이터> 이슈_상태_목록_가져오기(Long 연결_아이디) throws Exception {
        로그.info("온프레미스 이슈 상태 목록 가져오기");

        try {
            서버정보_데이터 연결정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
            //지라연결정보_데이터 info = 지라연결_서비스.checkInfo(연결_아이디);
            JiraRestClient restClient = 지라유틸.온프레미스_통신기_생성(연결정보.getUri(),
                    연결정보.getUserId(),
                    연결정보.getPasswordOrToken());

            Promise<Iterable<Status>> statusesPromise = restClient.getMetadataClient().getStatuses();
            Iterable<Status> statuses = statusesPromise.claim();

            List<지라이슈상태_데이터> 반환할_지라_이슈_상태_데이터전송객체_목록 = new ArrayList<>();
            for (Status status : statuses) {
                지라이슈상태_데이터 지라이슈상태_데이터 = new 지라이슈상태_데이터();
                지라이슈상태_데이터.setSelf(status.getSelf().toString());
                지라이슈상태_데이터.setId(status.getId().toString());
                지라이슈상태_데이터.setName(status.getName());
                지라이슈상태_데이터.setDescription(status.getDescription());
                반환할_지라_이슈_상태_데이터전송객체_목록.add(지라이슈상태_데이터);
            }

            return 반환할_지라_이슈_상태_데이터전송객체_목록;
        }catch (Exception e){
            로그.error("온프레미스 이슈 상태 목록 조회에 실패하였습니다" +e.getMessage());
            throw new IllegalArgumentException(에러코드.이슈상태_조회_오류.getErrorMsg());
        }
    }

    @Override
    public List<지라이슈상태_데이터> 프로젝트별_이슈_상태_목록_가져오기(Long 연결_아이디, String 프로젝트_아이디) throws Exception {

        로그.info("온프레미스 이슈_상태_목록_가져오기 실행");
        if (프로젝트_아이디 == null || 프로젝트_아이디.isEmpty()) {
            throw new IllegalArgumentException(에러코드.검색정보_오류.getErrorMsg());
        }
        try {
            서버정보_데이터 연결정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
            //지라연결정보_데이터 info = 지라연결_서비스.checkInfo(연결_아이디);
            JiraRestClient restClient = 지라유틸.온프레미스_통신기_생성(연결정보.getUri(),
                    연결정보.getUserId(),
                    연결정보.getPasswordOrToken());

            Promise<Iterable<Status>> statusesPromise = restClient.getMetadataClient().getStatuses();
            Iterable<Status> statuses = statusesPromise.claim();

            List<지라이슈상태_데이터> 반환할_지라_이슈_상태_데이터전송객체_목록 = new ArrayList<>();
            for (Status status : statuses) {
                지라이슈상태_데이터 지라이슈상태_데이터 = new 지라이슈상태_데이터();
                지라이슈상태_데이터.setSelf(status.getSelf().toString());
                지라이슈상태_데이터.setId(status.getId().toString());
                지라이슈상태_데이터.setName(status.getName());
                지라이슈상태_데이터.setDescription(status.getDescription());
                반환할_지라_이슈_상태_데이터전송객체_목록.add(지라이슈상태_데이터);
            }

            return 반환할_지라_이슈_상태_데이터전송객체_목록;
        }catch (Exception e){
            로그.error("온프레미스 이슈 상태 목록 조회에 실패하였습니다" +e.getMessage());
            throw new IllegalArgumentException(에러코드.이슈상태_조회_오류.getErrorMsg());
        }
    }

}
