package com.arms.jira.jiraissue.model;

import com.arms.jira.jiraissueresolution.model.지라이슈해결책_데이터;
import com.arms.jira.jiraissuestatus.model.지라이슈상태_데이터;
import com.arms.jira.jiraissuetype.model.지라이슈유형_데이터;
import com.arms.jira.jirapriority.model.지라이슈우선순위_데이터;
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
public class 지라이슈필드_데이터 {

    private 지라프로젝트_데이터 project;

    private 지라이슈유형_데이터 issuetype;

    private 지라사용자_데이터 creator;

    private 지라사용자_데이터 reporter;

    private 지라사용자_데이터 assignee;

    private List<String> labels;

    private 지라이슈우선순위_데이터 priority;

    private 지라이슈상태_데이터 status;

    private 지라이슈해결책_데이터 resolution;

    private String resolutiondate;

    private String created;

    private String updated;

    private List<지라이슈워크로그_데이터> worklogs;

    private Integer timespent;

    private String summary;
}
