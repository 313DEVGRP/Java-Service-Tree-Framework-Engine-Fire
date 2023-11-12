package com.arms.api.jira.jiraproject.model;

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
public class 클라우드_지라프로젝트_전체_데이터 {
    private String self;
    private Integer maxResults;
    private Integer startAt;
    private Integer total;
    private boolean isLast;
    private List<지라프로젝트_데이터> values;
}
