package com.arms.api.jira.user.service.strategy;

import com.arms.api.jira.user.model.UserDTO;
import com.arms.api.serverinfo.model.서버정보_데이터;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface UserStrategy {

    List<UserDTO> findAllUsers(Long connectId, 서버정보_데이터 serverInfo) throws URISyntaxException, IOException;
    String getType();
}
