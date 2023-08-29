package com.arms.jira.jiraissueresolution.strategy;

import com.arms.jira.utils.지라유틸;
import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.지라연결_서비스;
import com.arms.jira.jiraissueresolution.model.지라_이슈_해결책_데이터_전송_객체;
import com.arms.jira.jiraissueresolution.model.클라우드_지라_이슈_해결책_전체_데이터_전송_객체;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class 클라우드_지라_이슈_해결책_전략 implements 지라_이슈_해결책_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 지라연결_서비스 지라연결_서비스;

    @Autowired
    private 지라유틸 지라유틸;

    @Override
    public List<지라_이슈_해결책_데이터_전송_객체> 이슈_해결책_목록_가져오기(Long 연결_아이디) {
        로그.info("클라우드 지라 이슈_해결책_목록_가져오기");

        JiraInfoDTO found = 지라연결_서비스.checkInfo(연결_아이디);
        WebClient webClient = 지라유틸.클라우드_통신기_생성(found.getUri(), found.getUserId(), found.getPasswordOrToken());

        int startAt = 0;
        int 최대_검색수 = 지라유틸.최대_검색수_가져오기();
        boolean checkLast = false;

        List<지라_이슈_해결책_데이터_전송_객체> 반환할_지라_이슈_해결책_데이터전송객체_목록 = new ArrayList<지라_이슈_해결책_데이터_전송_객체>();

        while(!checkLast) {
            String endpoint = "/rest/api/3/resolution/search?maxResults="+ 최대_검색수 + "&startAt=" + startAt;
            클라우드_지라_이슈_해결책_전체_데이터_전송_객체 resolutions
                                                        = 지라유틸.get(webClient, endpoint,
                                                        클라우드_지라_이슈_해결책_전체_데이터_전송_객체.class).block();

            반환할_지라_이슈_해결책_데이터전송객체_목록.addAll(resolutions.getValues());

            if (resolutions.getTotal() == 반환할_지라_이슈_해결책_데이터전송객체_목록.size()) {
                checkLast = true;
            }
            else {
                startAt += 최대_검색수;
            }
        }

        return 반환할_지라_이슈_해결책_데이터전송객체_목록;
    }

}
