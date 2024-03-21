package com.arms.api.alm.issue.model;

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
public class 지라이슈조회_데이터 {

    private Integer startAt;

    private Integer maxResults;

    private Integer total;

    private List<지라이슈_데이터> issues;

}
