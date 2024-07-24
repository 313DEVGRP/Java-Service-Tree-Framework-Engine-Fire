package com.arms.api.alm.issue.base.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class 클라우드_이슈생성필드_메타데이터 {

    private Integer startAt;
    private Integer maxResults;
    private Integer total;
    private List<필드_메타데이터> fields;

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class 필드_메타데이터 {
        private boolean required;
        private String name;
        private String key;
        private String fieldId;
    }
}
