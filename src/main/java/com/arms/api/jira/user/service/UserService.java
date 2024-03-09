package com.arms.api.jira.user.service;

import com.arms.api.jira.user.factory.UserFactory;
import com.arms.api.jira.user.model.UserDTO;
import com.arms.api.jira.user.service.strategy.UserStrategy;
import com.arms.api.serverinfo.model.서버정보_데이터;
import com.arms.api.serverinfo.service.서버정보_서비스;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserFactory strategyFactory;
    private final 서버정보_서비스 serviceInfo;

    public List<UserDTO> findAllUsers(String connectId) throws URISyntaxException, IOException {
        서버정보_데이터 serverInfo = serviceInfo.서버정보_검증(Long.valueOf(connectId));
        String type = serverInfo.getType();
        UserStrategy strategy = strategyFactory.getStrategy(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Invalid Jira type: " + type);
        }
        return strategy.findAllUsers(Long.valueOf(connectId), serverInfo);
    }

    public List<UserDTO> findAllUsersByConnectIds(List<String> connectIds) throws URISyntaxException, IOException {
        List<UserDTO> users = new ArrayList<>();
        for (String connectId : connectIds) {
            서버정보_데이터 serverInfo = serviceInfo.서버정보_검증(Long.valueOf(connectId));
            String type = serverInfo.getType();
            UserStrategy strategy = strategyFactory.getStrategy(type);
            if (strategy == null) {
                log.info("Jira User Service :: findAllUsersByConnectIds :: Invalid Jira type " + type);
            } else {
                users.addAll(strategy.findAllUsers(Long.valueOf(connectId), serverInfo));
            }
        }
        return users;
    }

}
