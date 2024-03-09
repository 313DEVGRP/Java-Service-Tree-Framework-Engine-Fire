package com.arms.api.jira.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String self;
    private String accountId;
//    private String accountType;
    private String emailAddress;
//    private Map<String, String> avatarUrls;
    private String displayName;
    private boolean active;
    private String locale;
}
