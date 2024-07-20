package com.arms.api.alm.issue.status.strategy;

import com.arms.api.alm.issue.status.model.이슈상태_데이터;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import com.arms.api.alm.utils.지라유틸;
import com.arms.api.util.errors.에러로그_유틸;
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

    private 지라유틸 지라유틸;

    @Autowired
    public 온프레미스_지라_이슈상태_전략(지라유틸 지라유틸) {
        this.지라유틸 = 지라유틸;
    }

    @Override
    public List<이슈상태_데이터> 이슈상태_목록_가져오기(서버정보_데이터 서버정보) {

        try {
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
        }
        catch (Exception e) {
            String 에러로그 =  에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                    "온프레미스 지라("+ 서버정보.getConnectId() +") :: 이슈상태_목록_가져오기에 실패하였습니다.");
            throw new IllegalArgumentException(에러로그);
        }
    }

    @Override
    public List<이슈상태_데이터> 프로젝트별_이슈상태_목록_가져오기(서버정보_데이터 서버정보, String 프로젝트_아이디) {
        return null;
    }

}
