package com.arms.api.jira.user.service.strategy;

import com.arms.api.jira.user.model.UserDTO;
import com.arms.api.serverinfo.model.서버정보_데이터;
import com.arms.utils.지라유틸;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Status;
import com.atlassian.jira.rest.client.api.domain.User;
import io.atlassian.util.concurrent.Promise;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Component
public class OnPremiseUserStrategy implements UserStrategy {
    @Override
    public List<UserDTO> findAllUsers(Long connectId, 서버정보_데이터 serverInfo) throws URISyntaxException, IOException {
        JiraRestClient restClient = 지라유틸.온프레미스_통신기_생성(serverInfo.getUri(),
                serverInfo.getUserId(),
                serverInfo.getPasswordOrToken());

        Promise<Iterable<User>> iterablePromise = restClient.getUserClient().findUsers(serverInfo.getUserId());
        Iterable<User> users = iterablePromise.claim();
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            UserDTO userDTO = convertToUserDTO(user);
            userDTOs.add(userDTO);
        }

        return userDTOs;
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setSelf(user.getSelf().toString());
        userDTO.setAccountId(user.getName());
        userDTO.setDisplayName(user.getDisplayName());
        userDTO.setEmailAddress(user.getEmailAddress());
        return userDTO;
    }

    @Override
    public String getType() {
        return "온프레미스";
    }
}
