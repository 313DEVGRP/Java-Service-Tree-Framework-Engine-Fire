package com.arms.api.alm.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jira.api") // application.yml에 정의된 내용
public class 지라API_정보 {

    private Fields fields;
    private Parameter parameter;
    private Endpoint endpoint;

    @Getter
    @Setter
    public static class Fields {
        private String project;
        private String issuetype;
        private String summary;
        private String description;
        private String reporter;
        private String priority;
        private String duedate;
    }

    @Getter
    @Setter
    public static class Parameter {

        private boolean deleteSubtasks;
        private int maxResults;
        private String fields;
        private Jql jql;

        @Getter
        @Setter
        public static class Jql {
            private String updated;
            private String issue;
            private String subtask;
            private String linkedIssue;
        }

    }

    @Getter
    @Setter
    public static class Endpoint {

        private String createmeta;
        private String search;
        private Issue issue;

        @Getter
        @Setter
        public static class Issue {
            private Full full;
            private Increment increment;

            @Getter
            @Setter
            public static class Full {
                private String detail;
                private String subtask;
                private String linkedIssue;
            }

            @Getter
            @Setter
            public static class Increment {
                private String detail;
                private String subtask;
                private String linkedIssue;
            }
        }
    }

    public String 이슈키_대체하기(String 쿼리, String 대체이슈키) {
        return 쿼리.replace("{이슈키}", 대체이슈키);
    }

    public String 프로젝트키_대체하기(String 쿼리, String 대체_프로젝트키) {
        return 쿼리.replace("{프로젝트아이디_또는_키}", 대체_프로젝트키);
    }

    public String 이슈유형키_대체하기(String 쿼리, String 대체_이슈유형키) {
        return 쿼리.replace("{이슈유형아이디_또는_키}", 대체_이슈유형키);
    }

}
