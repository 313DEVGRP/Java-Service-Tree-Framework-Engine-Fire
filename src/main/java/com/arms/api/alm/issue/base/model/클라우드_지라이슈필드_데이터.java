package com.arms.api.alm.issue.base.model;

import com.arms.api.alm.issue.type.model.이슈유형_데이터;
import com.arms.api.alm.issue.priority.model.이슈우선순위_데이터;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class 클라우드_지라이슈필드_데이터 {

    private 지라프로젝트_데이터 project;

    private 이슈유형_데이터 issuetype;

    private String summary;

    private 내용 description;

    private 지라사용자_데이터 reporter;

    private 지라사용자_데이터 assignee;

    private List<String> labels;

    private 이슈우선순위_데이터 priority;

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class 내용 {
        private List<콘텐츠> content;
        private String type;
        private Integer version;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class 콘텐츠 {
        private List<콘텐츠_아이템> content;
        private String type;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class 콘텐츠_아이템 {
        private String text;
        private String type;
    }

}
