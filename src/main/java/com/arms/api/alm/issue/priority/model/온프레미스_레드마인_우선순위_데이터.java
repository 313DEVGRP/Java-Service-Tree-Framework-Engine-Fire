package com.arms.api.alm.issue.priority.model;

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
public class 온프레미스_레드마인_우선순위_데이터 {

    private List<이슈_우선순위> issue_priorities;

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class 이슈_우선순위 {
        private Long id;
        private String name;
        boolean is_default;
        boolean active;
    }
}
