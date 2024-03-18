package com.arms.api.engine.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class 히트맵날짜데이터 {
    private Set<String> items = new HashSet<>();
    private Set<String> contents = new HashSet<>();
    private int count = 0;
}
