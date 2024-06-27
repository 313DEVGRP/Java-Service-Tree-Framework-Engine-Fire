package com.arms.api.alm.issue.priority.strategy;

import com.arms.api.alm.issue.priority.model.이슈우선순위_데이터;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import com.arms.api.alm.utils.지라유틸;
import com.arms.api.util.errors.에러로그_유틸;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class 온프레미스_지라_이슈우선순위_전략 implements 이슈우선순위_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private 지라유틸 지라유틸;

    @Autowired
    public 온프레미스_지라_이슈우선순위_전략(지라유틸 지라유틸) {
        this.지라유틸 = 지라유틸;
    }

    @Override
    public List<이슈우선순위_데이터> 우선순위_목록_가져오기(서버정보_데이터 서버정보) {

        try {
            JiraRestClient restClient = 지라유틸.온프레미스_통신기_생성(서버정보.getUri(),
                                                                     서버정보.getUserId(),
                                                                     서버정보.getPasswordOrToken());

            Iterable<Priority> 이슈_우선순위_목록 = restClient.getMetadataClient().getPriorities().claim();

            if (이슈_우선순위_목록 == null) {
                로그.error("온프레미스 지라(" + 서버정보.getUri() + ") 서버에 설정된 이슈 우선순위가 없습니다. 서버 정보 확인이 필요합니다.");
                return null;
            }

            List<이슈우선순위_데이터> 반환할_이슈우선순위_데이터_목록 = StreamSupport.stream(이슈_우선순위_목록.spliterator(), false)
                    .map(priority -> {
                        이슈우선순위_데이터 온프레미스_지라이슈우선순위_데이터 = new 이슈우선순위_데이터();
                        온프레미스_지라이슈우선순위_데이터.setSelf(priority.getSelf().toString());
                        온프레미스_지라이슈우선순위_데이터.setId(priority.getId().toString());
                        온프레미스_지라이슈우선순위_데이터.setName(priority.getName());
                        온프레미스_지라이슈우선순위_데이터.setDescription(priority.getDescription());

                        return 온프레미스_지라이슈우선순위_데이터;
                    })
                    .collect(Collectors.toList());

            return 반환할_이슈우선순위_데이터_목록;
        }
        catch (Exception e) {
            String 에러로그 = 에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(), "우선순위_목록_가져오기");
            throw new IllegalArgumentException(에러로그);
        }
    }

}
