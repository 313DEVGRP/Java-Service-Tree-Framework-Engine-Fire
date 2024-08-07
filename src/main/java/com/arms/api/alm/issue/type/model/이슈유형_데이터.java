package com.arms.api.alm.issue.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class 이슈유형_데이터 {
    // 온프레미스, 클라우드 공통
    private String self;
    private String id;
    private String description;
    private String name;
    private Boolean subtask;
    
    // 클라우드만 사용
    private String untranslatedName;
    private Integer hierarchyLevel;

    public 이슈유형_데이터(String self, String id, String name) {
        this.self = self;
        this.id = id;
        this.name = name;
    }
}
