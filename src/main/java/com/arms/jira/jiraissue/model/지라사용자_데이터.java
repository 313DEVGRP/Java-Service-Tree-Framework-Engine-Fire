package com.arms.jira.jiraissue.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class 지라사용자_데이터 {

    private String accountId; ///////

    private String emailAddress;

    private String displayName;

}
