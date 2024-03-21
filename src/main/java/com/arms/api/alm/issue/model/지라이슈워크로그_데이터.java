package com.arms.api.alm.issue.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class 지라이슈워크로그_데이터 {

    private String self;

    private 지라사용자_데이터 author;

    private 지라사용자_데이터 updateAuthor;

    private String created;

    private String updated;

    private String started;

    private String timeSpent;

    private Integer timeSpentSeconds;

    private String id;

    private String issueId;

}
