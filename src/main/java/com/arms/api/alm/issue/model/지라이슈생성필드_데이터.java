package com.arms.api.alm.issue.model;

import com.arms.api.alm.issueresolution.model.이슈해결책_데이터;
import com.arms.api.alm.issuestatus.model.이슈상태_데이터;
import com.arms.api.alm.issuetype.model.지라이슈유형_데이터;
import com.arms.api.alm.priority.model.지라이슈우선순위_데이터;
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
public class 지라이슈생성필드_데이터 {

    private 지라프로젝트_데이터 project;

    private 지라이슈유형_데이터 issuetype;

    private String summary;

    private String description;

    private 지라사용자_데이터 reporter;

    private 지라사용자_데이터 assignee;

    private List<String> labels;

    private 지라이슈우선순위_데이터 priority;

    private 이슈상태_데이터 status;

    private 이슈해결책_데이터 resolution;

}
