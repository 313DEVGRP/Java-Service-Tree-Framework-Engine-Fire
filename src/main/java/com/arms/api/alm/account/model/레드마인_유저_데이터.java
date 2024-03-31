package com.arms.api.alm.account.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class 레드마인_유저_데이터 {

    private User user;
    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class User{

        int id;
        String login;
        Boolean admin;
        String firstname;
        String lastname;
        String mail;
        String api_key;

    }

}
