package com.arms.api.jira.user.service.strategy;

import com.arms.api.jira.user.model.UserDTO;
import com.arms.api.serverinfo.model.서버정보_데이터;
import com.arms.utils.지라유틸;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Component
public class CloudUserStrategy implements UserStrategy {
    @Override
    public List<UserDTO> findAllUsers(Long connectId, 서버정보_데이터 serverInfo) {
        WebClient webClient = 지라유틸.클라우드_통신기_생성(serverInfo.getUri(), serverInfo.getUserId(), serverInfo.getPasswordOrToken());
        String endpoint = "/rest/api/3/users";

        List<UserDTO> users = webClient.get()
                .uri(endpoint)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserDTO>>() {
                })
                .block();

        return users;
    }

    @Override
    public String getType() {
        return "클라우드";
    }
}
