package com.arms.api.alm.issue.priority.strategy;

import com.arms.api.alm.issue.priority.model.온프레미스_레드마인_우선순위_데이터;
import com.arms.api.alm.issue.priority.model.이슈우선순위_데이터;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import com.arms.api.alm.utils.레드마인API_정보;
import com.arms.api.alm.utils.레드마인유틸;
import com.arms.api.util.errors.codes.에러코드;
import com.arms.api.util.errors.에러로그_유틸;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Component
public class 온프레미스_레드마인_이슈우선순위_전략 implements 이슈우선순위_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private 레드마인유틸 레드마인유틸;
    private 레드마인API_정보 레드마인API_정보;

    @Autowired
    public 온프레미스_레드마인_이슈우선순위_전략(레드마인유틸 레드마인유틸,
                                        레드마인API_정보 레드마인API_정보) {
        this.레드마인유틸 = 레드마인유틸;
        this.레드마인API_정보 = 레드마인API_정보;
    }

    @Override
    public List<이슈우선순위_데이터> 우선순위_목록_가져오기(서버정보_데이터 서버정보) {

        WebClient webClient = 레드마인유틸.레드마인_웹클라이언트_통신기_생성(서버정보.getUri(), 서버정보.getPasswordOrToken());

        String endpoint = 레드마인API_정보.getEndpoint().getPriorityList();

        온프레미스_레드마인_우선순위_데이터 온프레미스_레드마인_우선순위_데이터;
        try {
            온프레미스_레드마인_우선순위_데이터 = 레드마인유틸.get(webClient, endpoint, 온프레미스_레드마인_우선순위_데이터.class).block();
        }
        catch (Exception e) {
            String 에러로그 = 에러로그_유틸.예외로그출력_및_반환(e, this.getClass().getName(),
                                                    "우선순위_목록_가져오기");
            throw new IllegalArgumentException(에러로그);
        }

        List<이슈우선순위_데이터> 지라이슈우선순위_목록 = null;
        if (온프레미스_레드마인_우선순위_데이터 != null && 온프레미스_레드마인_우선순위_데이터.getIssue_priorities() != null) {
            지라이슈우선순위_목록 = 온프레미스_레드마인_우선순위_데이터.getIssue_priorities().stream()
                    .map(우선순위 -> {
                        if (우선순위.isActive()) {
                            return 레드마인_웹클라이언트_이슈우선순위_데이터형_변환(우선순위, 서버정보.getUri());
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        return 지라이슈우선순위_목록;
    }

    private 이슈우선순위_데이터 레드마인_웹클라이언트_이슈우선순위_데이터형_변환(온프레미스_레드마인_우선순위_데이터.이슈_우선순위 우선순위, String 서버정보경로) {
        이슈우선순위_데이터 이슈우선순위_데이터 = new 이슈우선순위_데이터();

        이슈우선순위_데이터.setId(String.valueOf(우선순위.getId()));
        이슈우선순위_데이터.setName(우선순위.getName());
        이슈우선순위_데이터.setDefault(우선순위.is_default());
        이슈우선순위_데이터.setSelf(레드마인유틸.서버정보경로_체크(서버정보경로) + 레드마인API_정보.아이디_대체하기(레드마인API_정보.getEndpoint().getPriority(), String.valueOf(우선순위.getId())));

        return 이슈우선순위_데이터;
    }
}
