package com.arms.api.util.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class 검색어_검색결과<T> {
    private List<T> 검색결과_목록;
    private long 결과_총수;
}
