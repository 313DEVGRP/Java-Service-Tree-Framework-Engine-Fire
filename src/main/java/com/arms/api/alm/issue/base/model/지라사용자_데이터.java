package com.arms.api.alm.issue.base.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class 지라사용자_데이터 {

    private String accountId;

    private String emailAddress;

    private String displayName;

    public 지라사용자_데이터(String accountId, String displayName) {
        this.accountId = accountId;
        this.displayName = displayName;
    }
}
