package com.arms.api.util.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class 요구사항_지라이슈상태_주별_집계 {
    private long totalIssues;
    private Map<String, Long> statuses;
    private long totalRequirements;
}
