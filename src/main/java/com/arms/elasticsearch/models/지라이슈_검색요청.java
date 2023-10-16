package com.arms.elasticsearch.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
public class 지라이슈_검색요청 {
    private String type;
    private String field;
    private String fieldKeyword;
}
