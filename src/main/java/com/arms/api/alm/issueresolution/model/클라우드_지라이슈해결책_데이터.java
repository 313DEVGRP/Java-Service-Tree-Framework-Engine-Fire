package com.arms.api.alm.issueresolution.model;

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
public class 클라우드_지라이슈해결책_데이터 {
    private Integer maxResults;
    private Integer startAt;
    private Integer total;
    private boolean isLast;
    private List<이슈해결책_데이터> values;
}
