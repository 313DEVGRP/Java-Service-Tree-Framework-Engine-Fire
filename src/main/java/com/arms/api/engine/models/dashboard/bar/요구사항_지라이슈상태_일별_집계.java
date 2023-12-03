package com.arms.api.engine.models.dashboard.bar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class 요구사항_지라이슈상태_일별_집계 {
    private Map<String, Long> statuses;
}
