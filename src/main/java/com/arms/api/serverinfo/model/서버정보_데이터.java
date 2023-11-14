package com.arms.api.serverinfo.model;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class 서버정보_데이터 {
    private Long connectId;
    private String type;
    private String userId;
    private String passwordOrToken;
    private String uri;
}
