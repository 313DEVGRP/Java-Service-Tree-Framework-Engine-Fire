package com.arms.api.jira.user.service.strategy;

import com.arms.api.jira.user.model.UserDTO;
import com.arms.api.serverinfo.model.서버정보_데이터;
import com.arms.utils.지라유틸;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.User;
import io.atlassian.util.concurrent.Promise;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OnPremiseUserStrategy implements UserStrategy {
    @Override
    public List<UserDTO> findAllUsers(Long connectId, 서버정보_데이터 serverInfo) throws URISyntaxException, IOException {
        JiraRestClient restClient = 지라유틸.온프레미스_통신기_생성(serverInfo.getUri(),
                serverInfo.getUserId(),
                serverInfo.getPasswordOrToken());

        Promise<Iterable<User>> iterablePromise = restClient.getUserClient().findUsers(serverInfo.getUserId());
        List<User> users = new ArrayList<>();
        iterablePromise.claim().forEach(users::add);

        return users.stream()
                .filter(User::isActive)
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setSelf(user.getSelf().toString());
        userDTO.setAccountId(user.getName());
        userDTO.setDisplayName(user.getDisplayName());
        userDTO.setEmailAddress(user.getEmailAddress());
        userDTO.setLocale(user.getTimezone());
        return userDTO;
    }

    @Override
    public String getType() {
        return "온프레미스";
    }
}
