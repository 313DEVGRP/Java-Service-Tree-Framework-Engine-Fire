package com.arms.api.util.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Worker {
    String id;
    String name;
    Map<String, Integer> data;
    List<TaskList> children = new ArrayList<>();

    public Worker(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
