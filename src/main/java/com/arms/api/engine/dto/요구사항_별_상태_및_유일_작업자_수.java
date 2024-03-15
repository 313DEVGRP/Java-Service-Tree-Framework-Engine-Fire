package com.arms.api.engine.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class 요구사항_별_상태_및_유일_작업자_수 {

    // 요구사항 key
    private String key;

    // 요구사항 명
    private String summary;

    // status_name.keyword
    private String status;

    // 작업자 수
    private Integer uniqueAssignees;
}
