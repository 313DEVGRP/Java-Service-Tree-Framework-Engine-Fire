package com.arms.api.engine.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class 트리맵_담당자_요구사항_기여도 {
    private String name;
    private String path;
    private Long value;
    private List<Map<String, Object>> children;
}