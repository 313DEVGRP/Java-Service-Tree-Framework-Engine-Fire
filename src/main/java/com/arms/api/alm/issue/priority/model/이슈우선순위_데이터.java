package com.arms.api.alm.issue.priority.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class 이슈우선순위_데이터 {
    
    // 온프레미스, 클라우드 공통
    private String self;
    private String id;
    private String name;
    private String description;
    
    // 클라우드
    private boolean isDefault;

    public 이슈우선순위_데이터(String self, String id, String name) {
        this.self = self;
        this.id = id;
        this.name = name;
    }
}
