package com.arms.api.engine.dtos;

import com.arms.api.engine.models.지라이슈;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"key", "updated"})
public class 요구사항_지라이슈키별_업데이트_목록_데이터 {
    String key;
    String parentReqKey;
    String updated;
    String resolutiondate;
    Boolean  isReq;
    //private 지라이슈.담당자 assignee;
}
