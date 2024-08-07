package com.arms.api.alm.project.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class 프로젝트_데이터 {
    private String self;
    private String id;
    private String key;
    private String name;
}
