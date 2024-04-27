package com.arms.api.util.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskList {
    String id;
    String name;
    Map<String, Integer> data;
}
