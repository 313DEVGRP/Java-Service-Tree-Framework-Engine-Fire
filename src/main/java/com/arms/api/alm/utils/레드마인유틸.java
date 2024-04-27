package com.arms.api.alm.utils;

import com.arms.api.util.errors.에러로그_유틸;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class 레드마인유틸 {

    public RedmineManager 레드마인_온프레미스_통신기_생성(String uri, String apiKey) {
        return RedmineManagerFactory.createWithApiKey(uri, apiKey);
    }

    public static WebClient 레드마인_웹클라이언트_통신기_생성(String uri, String apiToken) {

        return WebClient.builder()
                .baseUrl(uri)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-Redmine-API-Key", apiToken)
                .build();
    }

    public String 서버정보경로_체크(String 서버정보경로) {
        return 서버정보경로.endsWith("/") ? 서버정보경로.substring(0, 서버정보경로.length() - 1) : 서버정보경로;
    }

    public User 사용자정보_조회(RedmineManager 레드마인_매니저, String 아이디) {

        User 사용자정보 = null;
        try {
            사용자정보 = 레드마인_매니저.getUserManager().getUserById(Integer.valueOf(아이디));
        } catch (RedmineException e) {
            에러로그_유틸.예외로그출력(e, this.getClass().getName(), 아이디 + " 사용자정보 조회 오류");
        }

        return 사용자정보;
    }

}
