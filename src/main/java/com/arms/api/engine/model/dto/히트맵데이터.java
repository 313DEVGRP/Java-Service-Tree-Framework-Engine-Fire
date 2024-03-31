package com.arms.api.engine.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class 히트맵데이터 {
    private Map<String, 히트맵날짜데이터> requirement = new HashMap<>();
    private Map<String, 히트맵날짜데이터> relationIssue = new HashMap<>();
    private Map<String, String> requirementColors = new HashMap<>();
    private Map<String, String> relationIssueColors = new HashMap<>();
}