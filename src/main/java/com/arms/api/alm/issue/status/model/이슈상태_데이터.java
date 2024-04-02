package com.arms.api.alm.issue.status.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class 이슈상태_데이터 {

    private String self;

    private String id;

    private String name;

    private String description;

    public 이슈상태_데이터(String self, String id, String name) {
        this.self = self;
        this.id = id;
        this.name = name;
    }
}
