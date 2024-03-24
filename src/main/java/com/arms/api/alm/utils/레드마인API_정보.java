package com.arms.api.alm.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "redmine.api")
public class 레드마인API_정보 {

    private Endpoint endpoint;

    @Getter
    @Setter
    public static class Endpoint {
        private String project;
        private String issuetype;
        private String priority;
        private String issuestatus;
        private String issue;
    }

    public String 아이디_대체하기(String 쿼리, String 대체아이디) {
        return 쿼리.replace("{아이디}", 대체아이디);
    }
}
