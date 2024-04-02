package com.arms.api.alm.issue.resolution.strategy;

import com.arms.api.alm.issue.resolution.model.이슈해결책_데이터;
import com.arms.api.alm.utils.지라유틸;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import com.arms.api.utils.errors.codes.에러코드;
import com.arms.api.alm.serverinfo.service.서버정보_서비스;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Resolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Component
public class 온프레미스_지라이슈해결책_전략 implements 지라이슈해결책_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 서버정보_서비스 서버정보_서비스;

    @Override
    public List<이슈해결책_데이터> 이슈해결책_목록_가져오기(Long 연결_아이디) throws URISyntaxException, IOException {

        로그.info("온프레미스 지라 이슈해결책_목록_가져오기");
        try {
            서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
            JiraRestClient restClient = 지라유틸.온프레미스_통신기_생성(서버정보.getUri(),
                                                                서버정보.getUserId(),
                                                                서버정보.getPasswordOrToken());

            Iterable<Resolution> 온프레미스_이슈_해결책_목록 = restClient.getMetadataClient()
                                                                    .getResolutions()
                                                                    .claim();

            List<이슈해결책_데이터> 반환할_이슈_해결책_목록 = new ArrayList<>();

            for (Resolution 온프레미스_이슈_해결책 : 온프레미스_이슈_해결책_목록) {
                이슈해결책_데이터 반환할_이슈_해결책 = new 이슈해결책_데이터();

                반환할_이슈_해결책.setSelf(온프레미스_이슈_해결책.getSelf().toString());
                반환할_이슈_해결책.setId(온프레미스_이슈_해결책.getId().toString());
                반환할_이슈_해결책.setName(온프레미스_이슈_해결책.getName());
                반환할_이슈_해결책.setDescription(온프레미스_이슈_해결책.getDescription());

                반환할_이슈_해결책_목록.add(반환할_이슈_해결책);
            }

            return 반환할_이슈_해결책_목록;

        } catch (Exception e) {
            로그.error("온프레미스 지라 이슈 해결책 목록 조회에 실패하였습니다."+e.getMessage());
            throw new IllegalArgumentException(에러코드.이슈해결책_조회_오류.getErrorMsg());
        }
    }
}
