package com.arms.api.alm.project.model;

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
public class 클라우드_지라프로젝트_데이터 {
    private String self;
    private Integer maxResults;
    private Integer startAt;
    private Integer total;
    private boolean isLast;
    private List<프로젝트_데이터> values;
}
