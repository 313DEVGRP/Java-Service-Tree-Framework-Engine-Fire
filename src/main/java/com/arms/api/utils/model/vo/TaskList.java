package com.arms.api.utils.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskList {
    String id;
    String name;
    Map<String, Integer> data;
}
