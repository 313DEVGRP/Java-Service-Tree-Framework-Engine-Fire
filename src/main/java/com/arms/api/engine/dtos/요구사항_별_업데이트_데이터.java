package com.arms.api.engine.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class 요구사항_별_업데이트_데이터 {
    private String key;
    private String parentReqKey;
    private String updated;
    private long pdServiceVersion;
    private String summary;
}
