package com.arms.api.alm.issue.status.model;

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
public class 클라우드_지라_이슈유형별_상태_데이터 {
    private String self;
    private String id;
    private String name;
    private boolean subtask;
    private List<이슈상태_데이터> statuses;
}
