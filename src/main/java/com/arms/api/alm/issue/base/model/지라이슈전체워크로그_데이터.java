package com.arms.api.alm.issue.base.model;

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
public class 지라이슈전체워크로그_데이터 {

    private Integer startAt;

    private Integer maxResults;

    private Integer total;

    private List<지라이슈워크로그_데이터> worklogs;

}
